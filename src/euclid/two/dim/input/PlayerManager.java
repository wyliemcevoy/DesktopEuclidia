package euclid.two.dim.input;

import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.CommandQueue;
import euclid.two.dim.HumanPlayer;
import euclid.two.dim.input.event.InputEvent;
import euclid.two.dim.input.event.InputEventVisitor;
import euclid.two.dim.input.event.KeyPressedEvent;
import euclid.two.dim.input.event.KeyReleasedEvent;
import euclid.two.dim.input.event.KeyTypedEvent;
import euclid.two.dim.input.event.MouseClickedEvent;
import euclid.two.dim.input.event.MouseDraggedEvent;
import euclid.two.dim.input.event.MouseMovedEvent;
import euclid.two.dim.input.event.MousePressedEvent;
import euclid.two.dim.input.event.MouseReleasedEvent;
import euclid.two.dim.threads.WorldStateObserver;
import euclid.two.dim.world.WorldState;

public class PlayerManager extends Thread implements InputEventVisitor, WorldStateObserver {
	private final HumanPlayer player;
	private CommandQueue commandQueue;
	private ArrayBlockingQueue<InputEvent> inputEvents;
	private boolean stopRequested;
	private WorldState worldState;
	private static final Object worldStateChangeLock = new Object();

	public PlayerManager(HumanPlayer player, ArrayBlockingQueue<InputEvent> inputEvents) {
		this.player = player;
		this.commandQueue = CommandQueue.getInstance();
		this.inputEvents = inputEvents;
		this.stopRequested = false;
	}

	@Override
	public void start() {

		while (!stopRequested) {
			try {
				InputEvent inputEvent = inputEvents.take();
				inputEvent.accept(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void requestStop() {
		this.stopRequested = true;
	}

	@Override
	public void visit(KeyTypedEvent keyDownEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(KeyPressedEvent keyPressedEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(KeyReleasedEvent keyReleasedEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(MouseClickedEvent mouseDownEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(MousePressedEvent mousePressedEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(MouseReleasedEvent mouseReleasedEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(MouseMovedEvent mouseMovedEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(MouseDraggedEvent mouseDragedEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notify(WorldState worldState) {
		synchronized (worldStateChangeLock) {
			this.worldState = worldState;
		}
	}

}
