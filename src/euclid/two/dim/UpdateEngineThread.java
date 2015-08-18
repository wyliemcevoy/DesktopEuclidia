package euclid.two.dim;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.command.Command;
import euclid.two.dim.updater.UpdateEngine;
import euclid.two.dim.world.WorldState;

public class UpdateEngineThread extends Thread {
	private ArrayBlockingQueue<WorldState> worldStateQueue;
	private long now, then, timeStep;
	private boolean stopRequested;
	private UpdateEngine updateEngine;
	private CommandQueue commandQueue;

	public UpdateEngineThread(WorldState worldState) {
		this.commandQueue = CommandQueue.getInstance();
		this.updateEngine = new UpdateEngine(worldState);
		this.updateEngine.setWorldState(worldState);
		this.stopRequested = false;
	}

	public void setWorldStateQueue(ArrayBlockingQueue<WorldState> rendererQueue) {
		this.worldStateQueue = rendererQueue;
	}

	public void run() {
		now = System.currentTimeMillis();
		then = System.currentTimeMillis();

		while (!stopRequested) {
			// Update time step
			then = now;
			now = System.currentTimeMillis();
			timeStep = now - then;

			ArrayList<Command> commands = commandQueue.getAllCommands();

			WorldState nPlusOne = updateEngine.update(timeStep / 2, commands);

			try {
				worldStateQueue.put(nPlusOne);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void requestStop() {
		this.stopRequested = true;
	}

}
