package euclid.two.dim.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import euclid.two.dim.Configuration;
import euclid.two.dim.HumanPlayer;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.render.RenderCreator;
import euclid.two.dim.world.Camera;

public class InputManager implements MouseListener, MouseWheelListener, KeyListener, MouseMotionListener {

	private static Object lock = new Object();
	private HumanPlayer player;
	private RenderCreator renderCreator;
	private static final int windowWidth = Configuration.width;
	private static final int windowHeight = Configuration.height;
	private static final double scrollBound = .2;
	private static final double scrollRate = 6;
	private EuVector delta;
	private ScrollListener scrollListener;
	private Camera camera;

	public InputManager(HumanPlayer player) {
		this.player = player;
		this.renderCreator = RenderCreator.getInstance();
		this.camera = renderCreator.requestCamera();
		this.delta = new EuVector(0, 0);
		startScrollListener();
	}

	public void startScrollListener() {
		scrollListener = new ScrollListener(this);
		scrollListener.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		synchronized (lock) {
			System.out.println(" screen " + e.getX() + "," + e.getY() + " map Location " + camera.veiwToMap(new EuVector(e.getX(), e.getY())));

			player.click(camera.veiwToMap(new EuVector(e.getX(), e.getY())));
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO do nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		delta = new EuVector(0, 0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO do nothing

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		this.mouseClicked(arg0);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		synchronized (lock) {
			camera.setZoom(camera.getZoom() + -.1 * e.getPreciseWheelRotation());
		}

		this.renderCreator.requestCameraChange(camera);
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		player.keyPressed(keyEvent.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double deltaX = 0;
		double deltaY = 0;
		double zoom = camera.getZoom();

		if (e.getX() < windowWidth * scrollBound) {
			deltaX = scrollRate / zoom;
		}
		else if (e.getX() > windowWidth * (1 - scrollBound)) {
			deltaX = -scrollRate / zoom;
		}

		if (e.getY() < windowHeight * scrollBound) {
			deltaY = scrollRate / zoom;
		}
		else if (e.getY() > windowHeight * (1 - scrollBound)) {
			deltaY = -scrollRate / zoom;
		}

		this.delta = new EuVector(deltaX, deltaY);

	}

	public boolean scrollRequired() {
		return delta.getX() != 0 || delta.getY() != 0;
	}

	public void requestScroll() {
		synchronized (lock) {
			camera.setMapX(camera.getMapX() + delta.getX());
			camera.setMapY(camera.getMapY() + delta.getY());
		}
		this.renderCreator.requestCameraChange(camera);
	}

	public void requestStop() {
		this.scrollListener.requestStop();
	}
}