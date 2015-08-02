package euclid.two.dim.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import euclid.two.dim.HumanPlayer;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.updater.UpdateEngine;

public class InputManager implements MouseListener, MouseWheelListener, KeyListener {

	private ArrayList<InputCommand> inputCommands;
	private static Object lock = new Object();
	private UpdateEngine updateEngine;
	private HumanPlayer player;

	public InputManager(HumanPlayer player) {
		this.player = player;
		this.inputCommands = new ArrayList<InputCommand>();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		player.click(new EuVector(e.getX(), e.getY()));

		synchronized (lock) {
			inputCommands.add(new ClickEvent(e.getX(), e.getY(), updateEngine));
		}

		System.out.println(e.getX() + " " + e.getY() + " mouse ");
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
		return inputCommands.size() > 0;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		ZoomCommand zoomCommand = new ZoomCommand(this.updateEngine);
		inputCommands.add(zoomCommand);
	}

	public ArrayList<InputCommand> getInputCommands() {
		ArrayList<InputCommand> build = new ArrayList<InputCommand>();
		synchronized (lock) {
			for (InputCommand inputCommand : inputCommands) {
				build.add(inputCommand);
			}
			inputCommands = new ArrayList<InputCommand>();
		}
		return build;
	}

	public void setUpdateEngine(UpdateEngine updateEngine) {
		this.updateEngine = updateEngine;
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {

		player.keyPressed(keyEvent.getKeyChar());

		System.out.println(keyEvent.getKeyChar());
		inputCommands.add(new KeyInput(updateEngine, keyEvent.getKeyChar()));
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		System.out.println(keyEvent.getKeyChar());

	}

}