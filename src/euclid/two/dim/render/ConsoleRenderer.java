package euclid.two.dim.render;

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
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import javax.imageio.ImageIO;

import euclid.two.dim.Configuration;
import euclid.two.dim.ConsoleFrame;
import euclid.two.dim.input.InputManager;
import euclid.two.dim.model.Door;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.model.Room;
import euclid.two.dim.world.WorldState;

public class ConsoleRenderer extends Thread
{
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
	
	
	private Image backgroundImage;
	
	public ConsoleRenderer(ArrayBlockingQueue<WorldState> rendererQueue, InputManager inputManager)
	{
		this.rendererQueue = rendererQueue;
		this.renderCreator = new RenderCreator();
		config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		consoleFrame = new ConsoleFrame(width, height);
		consoleFrame.addWindowListener(new FrameClose());
		
		// Canvas
		canvas = new Canvas(config);
		canvas.setSize(width * scale, height * scale);
		consoleFrame.add(canvas, 0);
		canvas.addMouseListener(inputManager);
		
		canvas.setFocusable(true);
		canvas.requestFocus();
		canvas.addKeyListener(inputManager);
		
		// Background & Buffer
		background = create(width, height, false);
		canvas.createBufferStrategy(2);
		do
		{
			strategy = canvas.getBufferStrategy();
		} while (strategy == null);
		
		try
		{
			this.backgroundImage = ImageIO.read(new File("C:\\Users\\Wylie\\Pictures\\Game\\desert.jpg"));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// create a hardware accelerated image
	public final BufferedImage create(final int width, final int height, final boolean alpha)
	{
		return config.createCompatibleImage(width, height, alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
	}
	
	private class FrameClose extends WindowAdapter
	{
		@Override
		public void windowClosing(final WindowEvent e)
		{
			isRunning = false;
		}
	}
	
	// Screen and buffer stuff
	private Graphics2D getBuffer()
	{
		if (graphics == null)
		{
			try
			{
				graphics = (Graphics2D) strategy.getDrawGraphics();
			} catch (IllegalStateException e)
			{
				return null;
			}
		}
		return graphics;
	}
	
	private boolean updateScreen()
	{
		graphics.dispose();
		graphics = null;
		try
		{
			strategy.show();
			Toolkit.getDefaultToolkit().sync();
			return (!strategy.contentsLost());
			
		} catch (NullPointerException e)
		{
			return true;
			
		} catch (IllegalStateException e)
		{
			return true;
		}
	}
	
	public void run()
	{
		backgroundGraphics = (Graphics2D) background.getGraphics();
		long fpsWait = (long) (1.0 / 30 * 1000);
		
		main: while (isRunning)
		{
			long renderStart = System.nanoTime();
			updateGame();
			
			// Update Graphics
			do
			{
				Graphics2D bg = getBuffer();
				if (!isRunning)
				{
					break main;
				}
				renderGame(backgroundGraphics);
				if (scale != 1)
				{
					bg.drawImage(background, 0, 0, width * scale, height * scale, 0, 0, width, height, null);
				} else
				{
					bg.drawImage(background, 0, 0, null);
				}
				bg.dispose();
			} while (!updateScreen());
			
			// Better do some FPS limiting here
			long renderTime = (System.nanoTime() - renderStart) / 100000;
			try
			{
				Thread.sleep(Math.max(0, fpsWait - renderTime));
			} catch (InterruptedException e)
			{
				Thread.interrupted();
				break;
			}
			renderTime = (System.nanoTime() - renderStart) / 100000;
			
		}
		consoleFrame.dispose();
		
		System.exit(1);
	}
	
	public void updateGame()
	{
		try
		{
			if (rendererQueue.size() > 0)
			{
				currentState = rendererQueue.take();
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void renderGame(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		g.drawImage(backgroundImage, 0, 0, 1000, 1000, 0, 0, backgroundImage.getWidth(null), backgroundImage.getHeight(null), null);
		drawWorldState(g);
	}
	
	public void drawWorldState(Graphics2D g)
	{
		/*
		
		*/
		
		AffineTransform saveAT = g.getTransform();
		g.transform(currentState.buildTransform());
		
		renderCreator.setWorldState(currentState);
		
		// Draw Explosions
		for (Renderable renderable : renderCreator.getRenderables())
		{
			renderable.draw(g);
		}
		
		for (GameSpaceObject gso : currentState.getGsos())
		{
			EuVector pos = gso.getPosition();
			int rad = (int) gso.getRadius();
			
			g.setColor(gso.getColor());
			
			if (rad > 10)
			{
				g.setColor(new Color(25, 25, 25));
			}
			/*
			if (gso instanceof Boid)
			{
				g.setColor(new Color(50, 50, 50));
				//g.drawArc((int) (pos.getX() - rad), (int) (pos.getY() - rad), 2 * rad, 2 * rad, 0, 360);
				
				for (Minion fish : ((Boid) gso).explode())
				{
					rad = (int) fish.getRadius();
					
					//g.setColor(Color.RED);
					
					if (fish.getPosition().subtract(gso.getPosition()).getMagnitude() > gso.getRadius())
					{
						g.setColor(new Color(50, 50, 50));
						g.drawLine((int) fish.getPosition().getX(), (int) fish.getPosition().getY(), (int) pos.getX(), (int) pos.getY());
					}
					g.setColor(fish.getColor());
					g.drawArc((int) (fish.getPosition().getX() - rad), (int) (fish.getPosition().getY() - rad), 2 * rad, 2 * rad, 0, 360);
				}
			} else
			{
				
				//g.setColor(Color.gray);
				//g.drawRect((int) (pos.getX() - rad), (int) (pos.getY() - rad), 2 * rad, 2 * rad);
				//g.drawArc((int) (pos.getX() - 2 * rad), (int) (pos.getY() - 2 * rad), 4 * rad, 4 * rad, 0, 360);
			}
			*/
			boolean draw = false;
			if (draw)
			{
				
				/*
				WorldGrid grid = currentState.getWorldGrid();
				
				EuVector future = gso.getFuture();
				if (future != null)
				{
					EuVector adustedFuture = grid.getForceAt(future.getX(), future.getY()).dividedBy(2);
					adustedFuture = adustedFuture.add(future);
					
					//g.drawLine((int) (pos.getX()), (int) (pos.getY()), (int) future.getX(), (int) future.getY());
					// g.setColor(Color.GRAY);
					// g.drawLine((int) (future.getX()), (int) (future.getY()),
					// (int) adustedFuture.getX(), (int) adustedFuture.getY());
				}
				
				*/
			}
		}
		g.setColor(new Color(200, 200, 200));
		
		for (Room room : currentState.getRooms())
		{
			g.drawRect((int) room.getX(), (int) room.getY(), (int) room.getWidth(), (int) room.getHeight());
		}
		
		g.setColor(new Color(80, 80, 80));
		for (Door door : currentState.getDoors())
		{
			g.drawLine((int) door.getPointOne().getX(), (int) door.getPointOne().getY(), (int) door.getPointTwo().getX(), (int) door.getPointTwo().getY());
		}
		
		g.setTransform(saveAT);
	}
}
