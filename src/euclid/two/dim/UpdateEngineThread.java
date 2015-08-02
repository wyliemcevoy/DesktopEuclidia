package euclid.two.dim;

import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.team.Game;
import euclid.two.dim.updater.UpdateEngine;
import euclid.two.dim.world.WorldState;

public class UpdateEngineThread extends Thread {
	private ArrayBlockingQueue<WorldState> rendererQueue;
	private long now, then, timeStep;
	private boolean stopRequested;
	private UpdateEngine updateEngine;
	private Game game;

	public UpdateEngineThread(WorldState worldState, Game game) {
		this.updateEngine = new UpdateEngine(game);
		this.updateEngine.setWorldState(worldState);
		this.stopRequested = false;
		this.game = game;
	}

	public void setRendererQueue(ArrayBlockingQueue<WorldState> rendererQueue) {
		this.rendererQueue = rendererQueue;
	}

	public void run() {
		now = System.currentTimeMillis();
		then = System.currentTimeMillis();

		while (!stopRequested) {
			// Update time step
			then = now;
			now = System.currentTimeMillis();
			timeStep = now - then;

			WorldState nPlusOne = updateEngine.update(timeStep / 2);

			WorldState playerCopy = nPlusOne.deepCopy();

			game.updatePlayers(playerCopy);

			try {
				rendererQueue.put(nPlusOne);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void requestStop() {
		this.stopRequested = true;
	}

	public UpdateEngine getUpdateEngine() {
		// TODO Redesign so there is no need to expose update engine
		return updateEngine;
	}
}
