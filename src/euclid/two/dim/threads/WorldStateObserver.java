package euclid.two.dim.threads;

import euclid.two.dim.world.WorldState;

public interface WorldStateObserver {
	public void notify(WorldState worldState);
}
