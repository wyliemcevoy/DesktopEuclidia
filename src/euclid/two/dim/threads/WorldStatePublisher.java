package euclid.two.dim.threads;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.world.WorldState;

public class WorldStatePublisher implements Runnable {

	private boolean stopRequested;
	private ArrayBlockingQueue<WorldState> queue;
	private ArrayList<WorldStateObserver> observers;

	public WorldStatePublisher(ArrayBlockingQueue<WorldState> queue) {
		this.queue = queue;
		this.observers = new ArrayList<WorldStateObserver>();
	}

	@Override
	public void run() {

		while (!stopRequested) {
			try {
				WorldState currentState = queue.take();

				for (WorldStateObserver observer : observers) {
					observer.notify(currentState);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addListener(WorldStateObserver observer) {
		this.observers.add(observer);
	}

	public void requestStop() {
		stopRequested = true;
	}
}
