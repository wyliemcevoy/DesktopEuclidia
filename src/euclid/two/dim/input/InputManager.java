package euclid.two.dim.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.Configuration;
import euclid.two.dim.input.event.InputEvent;
import euclid.two.dim.input.event.KeyPressedEvent;
import euclid.two.dim.input.event.KeyTypedEvent;
import euclid.two.dim.input.event.MouseClickedEvent;
import euclid.two.dim.input.event.MouseDraggedEvent;
import euclid.two.dim.input.event.MousePressedEvent;
import euclid.two.dim.input.event.MouseReleasedEvent;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.render.RenderCreator;
import euclid.two.dim.world.Camera;

public class InputManager implements MouseListener, MouseWheelListener, KeyListener, MouseMotionListener {

	private static final Object cameraChangeLock = new Object();
	private RenderCreator renderCreator;
	private static final int windowWidth = Configuration.width;
	private static final int windowHeight = Configuration.height;
	private static final double scrollBound = .2;
	private static final double scrollRate = 6;
	private EuVector delta;
	private ScrollListener scrollListener;
	private Camera camera;

	private ArrayBlockingQueue<InputEvent> inputEvents;

	public InputManager(ArrayBlockingQueue<InputEvent> inputEvents) {

		this.renderCreator = RenderCreator.getInstance();
		this.camera = renderCreator.requestCamera();
		this.delta = new EuVector(0, 0);
		this.inputEvents = inputEvents;
		startScrollListener();
	}

	public void startScrollListener() {
		scrollListener = new ScrollListener(this);
		scrollListener.start();
	}

	private EuVector getMapLocation(double x, double y) {
		synchronized (cameraChangeLock) {
			return camera.veiwToMap(new EuVector(x, y));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.inputEvents.add(new MouseClickedEvent(e.getButton(), getMapLocation(e.getX(), e.getY())));
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
	public void mousePressed(MouseEvent e) {

		this.inputEvents.add(new MousePressedEvent(e.getButton(), getMapLocation(e.getX(), e.getY())));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.inputEvents.add(new MouseReleasedEvent(e.getButton(), getMapLocation(e.getX(), e.getY())));
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		synchronized (cameraChangeLock) {
			camera.setZoom(camera.getZoom() + -.1 * e.getPreciseWheelRotation());
			this.renderCreator.requestCameraChange(camera);
		}
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		int selectedAbility = -1;

		switch (keyEvent.getKeyChar()) {
		case 'q':
			selectedAbility = 0;
			break;
		case 'w':
			selectedAbility = 1;
			break;
		case 'e':
			selectedAbility = 2;
			break;
		case 'r':
			selectedAbility = 3;
			break;
		case 'a':
			selectedAbility = -2;
			break;
		default:
			selectedAbility = -1;
		}
		this.inputEvents.add(new KeyPressedEvent(selectedAbility));
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		int selectedAbility = -1;

		switch (keyEvent.getKeyChar()) {
		case 'q':
			selectedAbility = 0;
			break;
		case 'w':
			selectedAbility = 1;
			break;
		case 'e':
			selectedAbility = 2;
			break;
		case 'r':
			selectedAbility = 3;
			break;
		case 'a':
			selectedAbility = -2;
			break;
		default:
			selectedAbility = -1;
		}

		this.inputEvents.add(new KeyTypedEvent(selectedAbility));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.inputEvents.add(new MouseDraggedEvent(e.getButton(), getMapLocation(e.getX(), e.getY())));
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
		synchronized (cameraChangeLock) {
			camera.setMapX(camera.getMapX() + delta.getX());
			camera.setMapY(camera.getMapY() + delta.getY());
		}
		this.renderCreator.requestCameraChange(camera);
	}

	public void requestStop() {
		this.scrollListener.requestStop();
	}
}