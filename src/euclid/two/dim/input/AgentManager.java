package euclid.two.dim.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import euclid.two.dim.CommandQueue;
import euclid.two.dim.Player;
import euclid.two.dim.ability.internal.AbilityType;
import euclid.two.dim.command.AbilityCommand;
import euclid.two.dim.command.GatherCommand;
import euclid.two.dim.command.MoveCommand;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.model.ResourcePatch;
import euclid.two.dim.model.Worker;
import euclid.two.dim.team.Team;
import euclid.two.dim.threads.WorldStateObserver;
import euclid.two.dim.visitor.TypedSelection;
import euclid.two.dim.world.WorldState;

public class AgentManager implements Runnable, WorldStateObserver {

	private static final Object worldStateChangeLock = new Object();
	private WorldState worldState, nextWorldState;
	private CommandQueue commandQueue;
	private Team team;
	private boolean stopRequested;
	private UUID homeBase;
	private EuVector enemyBaseLocation;

	public AgentManager(Team team) {
		this.team = team;
		this.commandQueue = CommandQueue.getInstance();
	}

	@Override
	public void run() {
		while (!stopRequested) {
			synchronized (worldStateChangeLock) {
				worldState = nextWorldState;
			}

			ArrayList<GameSpaceObject> friendlies = worldState.getFriendlyUnits(team);
			ArrayList<UUID> ids = new ArrayList<UUID>();
			Player self = worldState.getPlayer(team);
			TypedSelection selection = new TypedSelection(worldState.getFriendlyUnits(team));
			ArrayList<Worker> workers = selection.getWorkers();

			TypedSelection enemySelection = new TypedSelection(worldState.getFriendlyUnits(Team.Blue));

			if (homeBase == null) {
				homeBase = selection.getBuildings().get(0).getId();
				enemyBaseLocation = enemySelection.getBuildings().get(0).getPosition();
			}

			if (workers.size() < 20 && self.getMinerals() > 100) {
				commandQueue.add(new AbilityCommand(selection.getBuildings().get(0).getAbility(AbilityType.buildWorker).toRequest(homeBase, worldState, new EuVector(100, 100))));
			}
			else {
				if (self.getMinerals() > 100) {
					commandQueue.add(new AbilityCommand(selection.getBuildings().get(0).getAbility(AbilityType.buildMinion).toRequest(homeBase, worldState, new EuVector(100, 100))));
				}
			}

			ArrayList<UUID> idleWorkerIds = new ArrayList<UUID>();

			for (Worker worker : selection.getWorkers()) {

				if (!worker.isGathering()) {
					idleWorkerIds.add(worker.getId());
				}
			}
			ArrayList<ResourcePatch> patches = worldState.getResourcePatches();

			Iterator<ResourcePatch> it = patches.iterator();

			while (it.hasNext()) {
				ResourcePatch patch = it.next();
				if (patch.getPosition().subtract(selection.getBuildings().get(0).getPosition()).getMagnitude() > 1000) {
					it.remove();
				}
			}

			Random rand = new Random();
			for (UUID id : idleWorkerIds) {
				commandQueue.add(new GatherCommand(id, patches.get(rand.nextInt(patches.size())).getId()));
			}

			sendAttackMove(selection.getMinionIds(), enemyBaseLocation);

			for (GameSpaceObject gso : friendlies) {
				ids.add(gso.getId());
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendAttackMove(ArrayList<UUID> ids, EuVector target) {
		if (ids.size() > 0) {
			MoveCommand attackMove = new MoveCommand(ids, target);
			attackMove.setAttackWhileMoving(true);
			commandQueue.add(attackMove);
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
