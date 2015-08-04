package euclid.two.dim.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import euclid.two.dim.HumanPlayer;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.render.RenderCreator;
import euclid.two.dim.world.Camera;

public class InputManager implements MouseListener, MouseWheelListener, KeyListener {

	private static Object lock = new Object();
	private HumanPlayer player;
	private RenderCreator renderCreator;
	private Camera camera;

	public InputManager(HumanPlayer player) {
		this.player = player;
		this.renderCreator = RenderCreator.getInstance();
		this.camera = renderCreator.requestCamera();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		player.click(camera.veiwToMap(new EuVector(e.getX(), e.getY())));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		this.mouseClicked(arg0);
	}

	public boolean hasUnprocessedEvents() {
		return false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		camera.setZoom(camera.getZoom() + -.1 * e.getPreciseWheelRotation());
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

}