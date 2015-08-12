package euclid.two.dim;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.command.Command;
import euclid.two.dim.team.Game;
import euclid.two.dim.updater.UpdateEngine;
import euclid.two.dim.world.WorldState;

public class UpdateEngineThread extends Thread {
	private ArrayBlockingQueue<WorldState> worldStateQueue;
	private long now, then, timeStep;
	private boolean stopRequested;
	private UpdateEngine updateEngine;
	private Game game;
	private CommandQueue commandQueue;

	public UpdateEngineThread(WorldState worldState, Game game) {
		this.commandQueue = CommandQueue.getInstance();
		this.updateEngine = new UpdateEngine(game, worldState);
		this.updateEngine.setWorldState(worldState);
		this.stopRequested = false;
		this.game = game;
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

			WorldState playerCopy = nPlusOne.deepCopy();

			game.updatePlayers(playerCopy);

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
