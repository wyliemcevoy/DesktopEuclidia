package euclid.two.dim.input;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import euclid.two.dim.CommandQueue;
import euclid.two.dim.command.MoveCommand;
import euclid.two.dim.model.ConvexPoly;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.team.Agent;
import euclid.two.dim.threads.WorldStateObserver;
import euclid.two.dim.world.WorldState;

public class AgentManager implements Runnable, WorldStateObserver {

	private static final Object worldStateChangeLock = new Object();
	private WorldState worldState, nextWorldState;
	private CommandQueue commandQueue;
	private Agent agent;
	private boolean stopRequested;
	private Random rand;

	public AgentManager(Agent agent) {
		this.agent = agent;
		this.commandQueue = CommandQueue.getInstance();
		this.rand = new Random();
	}

	@Override
	public void run() {
		while (!stopRequested) {
			synchronized (worldStateChangeLock) {
				worldState = nextWorldState;
			}

			ArrayList<GameSpaceObject> friendlies = worldState.getFriendlyUnits(agent);
			ArrayList<UUID> ids = new ArrayList<UUID>();

			for (GameSpaceObject gso : friendlies) {
				ids.add(gso.getId());
			}

			ArrayList<ConvexPoly> polys = worldState.getGameMap().getNavMesh().getAllPolygons();
			ConvexPoly target = polys.get(rand.nextInt(polys.size()));
			commandQueue.add(new MoveCommand(ids, target.getCenter()));

			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void notify(WorldState nextWorldState) {
		synchronized (worldStateChangeLock) {
			this.nextWorldState = nextWorldState;
		}
	}

	public void requestStop() {
		this.stopRequested = true;
	}

}
