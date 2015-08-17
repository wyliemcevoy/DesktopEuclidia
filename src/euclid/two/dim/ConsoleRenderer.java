package euclid.two.dim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;

import euclid.two.dim.input.InputManager;
import euclid.two.dim.render.RenderCreator;
import euclid.two.dim.render.Renderable;
import euclid.two.dim.threads.WorldStateObserver;
import euclid.two.dim.world.WorldState;

public class ConsoleRenderer extends Thread implements WorldStateObserver {
	private GraphicsConfiguration config;
	private boolean isRunning = true;
	private Canvas canvas;
	private BufferStrategy strategy;
	private BufferedImage background;
	private Graphics2D backgroundGraphics;
	private Graphics2D graphics;
	private ConsoleFrame consoleFrame;
	private int width = Configuration.width;
	private int height = Configuration.height;
	private int scale = 1;
	private ArrayBlockingQueue<WorldState> rendererQueue;
	private WorldState currentState;
	private RenderCreator renderCreator;
	private InputManager inputManager;
	private Image spaceStation;

	public ConsoleRenderer(InputManager inputManager) {
		this.rendererQueue = new ArrayBlockingQueue<WorldState>(10);
		this.renderCreator = RenderCreator.getInstance();
		config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		consoleFrame = new ConsoleFrame(width, height);
		consoleFrame.addWindowListener(new FrameClose());
		this.inputManager = inputManager;
		// Canvas
		canvas = new Canvas(config);
		canvas.setSize(width * scale, height * scale);
		consoleFrame.add(canvas, 0);
		canvas.addMouseListener(inputManager);
		canvas.addMouseWheelListener(inputManager);
		canvas.addMouseMotionListener(inputManager);
		canvas.setFocusable(true);
		canvas.requestFocus();
		canvas.addKeyListener(inputManager);

		// Background & Buffer
		background = create(width, height, false);
		canvas.createBufferStrategy(2);
		do {
			strategy = canvas.getBufferStrategy();
		} while (strategy == null);

		this.spaceStation = (new ImageIcon(this.getClass().getResource("/imgs/SpacePlatformMap.png"))).getImage();
	}

	// create a hardware accelerated image
	public final BufferedImage create(final int width, final int height, final boolean alpha) {
		return config.createCompatibleImage(width, height, alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
	}

	private class FrameClose extends WindowAdapter {
		@Override
		public void windowClosing(final WindowEvent e) {
			isRunning = false;
		}
	}

	// Screen and buffer stuff
	private Graphics2D getBuffer() {
		if (graphics == null) {
			try {
				graphics = (Graphics2D) strategy.getDrawGraphics();
			} catch (IllegalStateException e) {
				return null;
			}
		}
		return graphics;
	}

	private boolean updateScreen() {
		graphics.dispose();
		graphics = null;
		try {
			strategy.show();
			Toolkit.getDefaultToolkit().sync();
			return (!strategy.contentsLost());

		} catch (NullPointerException e) {
			return true;

		} catch (IllegalStateException e) {
			return true;
		}
	}

	public void run() {
		backgroundGraphics = (Graphics2D) background.getGraphics();
		long fpsWait = (long) (1.0 / 30 * 1000);

		main: while (isRunning) {
			long renderStart = System.nanoTime();
			updateGame();

			// Update Graphics
			do {
				Graphics2D bg = getBuffer();
				if (!isRunning) {
					break main;
				}
				renderGame(backgroundGraphics);
				if (scale != 1) {
					bg.drawImage(background, 0, 0, width * scale, height * scale, 0, 0, width, height, null);
				}
				else {
					bg.drawImage(background, 0, 0, null);
				}
				bg.dispose();
			} while (!updateScreen());

			// FPS limiting
			long renderTime = (System.nanoTime() - renderStart) / 100000;
			try {
				Thread.sleep(Math.max(0, fpsWait - renderTime));
			} catch (InterruptedException e) {
				Thread.interrupted();
				break;
			}
			renderTime = (System.nanoTime() - renderStart) / 100000;

		}
		die();
	}

	public void updateGame() {
		try {
			if (rendererQueue.size() > 0) {
				currentState = rendererQueue.take();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void renderGame(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		drawWorldState(g);
	}

	public void drawWorldState(Graphics2D g) {
		renderCreator.setWorldState(currentState);
		// save current transform
		AffineTransform savedAT = g.getTransform();
		g.transform(renderCreator.buildTransform());
		g.drawImage(spaceStation, 0, 0, spaceStation.getWidth(null), spaceStation.getHeight(null), 0, 0, spaceStation.getWidth(null), spaceStation.getHeight(null), null);
		// Draw renderables
		for (Renderable renderable : renderCreator.getRenderables()) {
			renderable.draw(g);
		}
		g.setTransform(savedAT);
	}

	private void die() {
		inputManager.requestStop();
		consoleFrame.dispose();
		System.exit(1);
	}

	@Override
	public void notify(WorldState worldState) {
		try {
			this.rendererQueue.put(worldState);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
