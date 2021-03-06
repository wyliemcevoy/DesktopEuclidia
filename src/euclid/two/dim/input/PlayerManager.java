package euclid.two.dim.input;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.CommandQueue;
import euclid.two.dim.ability.internal.Ability;
import euclid.two.dim.ability.request.AbilityRequest;
import euclid.two.dim.command.AbilityCommand;
import euclid.two.dim.command.AttackCommand;
import euclid.two.dim.command.GatherCommand;
import euclid.two.dim.command.MoveCommand;
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
import euclid.two.dim.model.CasterUnit;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.model.Hero;
import euclid.two.dim.render.Box;
import euclid.two.dim.render.ConsoleOverlays;
import euclid.two.dim.team.Team;
import euclid.two.dim.threads.WorldStateObserver;
import euclid.two.dim.visitor.TypedSelection;
import euclid.two.dim.world.WorldState;

public class PlayerManager implements Runnable, InputEventVisitor, WorldStateObserver {
	private final Team team;
	private CommandQueue commandQueue;
	private ArrayList<UUID> selectedUnits;
	private ArrayList<UUID> selectedCasters;
	private ArrayBlockingQueue<InputEvent> inputEvents;
	private boolean stopRequested;
	private WorldState worldState, nextWorldState;
	private static final Object worldStateChangeLock = new Object();
	private AbilitySelectedState abilitySelected;
	private SelectingState selecting;
	private UnitsSelectedState unitsSelected;
	private InputState currentState;
	private ConsoleOverlays consoleOverlays;
	private long leftDownStart;

	public PlayerManager(Team player, ArrayBlockingQueue<InputEvent> inputEvents) {
		this.team = player;
		this.commandQueue = CommandQueue.getInstance();
		this.inputEvents = inputEvents;
		this.stopRequested = false;
		this.selectedUnits = new ArrayList<UUID>();
		this.selectedCasters = new ArrayList<UUID>();
		this.leftDownStart = -1;
		this.consoleOverlays = ConsoleOverlays.getInstance();

		// Only create one instance of each states.
		this.abilitySelected = new AbilitySelectedState();
		this.selecting = new SelectingState();
		this.unitsSelected = new UnitsSelectedState();
		this.currentState = unitsSelected;
	}

	@Override
	public void run() {

		while (!stopRequested) {

			try {
				InputEvent inputEvent = inputEvents.take();
				synchronized (worldStateChangeLock) {
					worldState = nextWorldState;
				}

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
	public void visit(KeyTypedEvent event) {
		if (event.getIndex() != -1) {
			currentState.abilitySelected(event.getIndex());
		}
	}

	@Override
	public void visit(KeyPressedEvent event) {
		if (event.getIndex() != -1) {
			currentState.abilitySelected(event.getIndex());
		}
	}

	@Override
	public void visit(KeyReleasedEvent keyReleasedEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(MouseClickedEvent mouseClickedEvent) {
		if (mouseClickedEvent.getButton() == 3) {
			currentState.rightDown(mouseClickedEvent.getLocation());
			currentState.rightUp(mouseClickedEvent.getLocation());
		}
		else if (mouseClickedEvent.getButton() == 1) {
			currentState.leftDown(mouseClickedEvent.getLocation());
			currentState.leftUp(mouseClickedEvent.getLocation());
		}
	}

	@Override
	public void visit(MousePressedEvent mousePressedEvent) {
		if (mousePressedEvent.getButton() == 3) {
			currentState.rightDown(mousePressedEvent.getLocation());
		}
		else if (mousePressedEvent.getButton() == 1) {
			currentState.leftDown(mousePressedEvent.getLocation());
		}
	}

	@Override
	public void visit(MouseReleasedEvent mouseReleasedEvent) {
		if (mouseReleasedEvent.getButton() == 3) {
			currentState.rightUp(mouseReleasedEvent.getLocation());
		}
		else if (mouseReleasedEvent.getButton() == 1) {
			currentState.leftUp(mouseReleasedEvent.getLocation());
		}
	}

	@Override
	public void visit(MouseMovedEvent mouseMovedEvent) {
		currentState.mouseMove(mouseMovedEvent.getLocation());
	}

	@Override
	public void visit(MouseDraggedEvent mouseDragedEvent) {
		currentState.mouseMove(mouseDragedEvent.getLocation());
	}

	@Override
	public void notify(WorldState nextWorldState) {
		synchronized (worldStateChangeLock) {
			this.nextWorldState = nextWorldState;
		}
	}

	// Inner classes
	// These are not defined outside the file to maintain encapsulation
	// To avoid large logic tables for mapping behavior to mouse events,
	// use the state design pattern.

	private abstract class InputState {
		public abstract void leftDown(EuVector location);

		public abstract void leftUp(EuVector location);

		public abstract void rightDown(EuVector location);

		public abstract void rightUp(EuVector location);

		public abstract void mouseMove(EuVector location);

		public abstract void abilitySelected(int i);
	}

	private class SelectingState extends InputState {
		private EuVector downLocation;

		public void setDownLocation(EuVector downLocation) {
			this.downLocation = downLocation;
		}

		@Override
		public void leftDown(EuVector location) {
			// do nothing
		}

		@Override
		public void leftUp(EuVector location) {
			// Finished Selecting (see what is selected and update state)
			TypedSelection selection = worldState.getTypedSelectionInRect(team, downLocation, location);
			ArrayList<UUID> updatedSelectedUnits = selection.getIds();

			if (updatedSelectedUnits.size() > 0) {

				ArrayList<Hero> heros = selection.getHeros();
				ArrayList<UUID> updatedCasterIds = new ArrayList<UUID>();
				for (Hero hero : heros) {
					updatedCasterIds.add(hero.getId());
				}

				selectedUnits = updatedSelectedUnits;

				selectedCasters = selection.getHeroIds();

			}
			else if (selection.getBuildings().size() > 0) {
				selectedUnits = selection.getBuildingIds();
				selectedCasters = selectedUnits;
			}

			ArrayList<UUID> leakedCopy = new ArrayList<UUID>();
			for (UUID id : selectedUnits) {
				leakedCopy.add(id);
			}

			consoleOverlays.updateSelectedUnits(leakedCopy);

			consoleOverlays.stopSelectionBox();
			currentState = unitsSelected;
		}

		@Override
		public void rightDown(EuVector location) {
			// Do nothing
		}

		@Override
		public void rightUp(EuVector location) {
			// Do nothing
		}

		@Override
		public void mouseMove(EuVector location) {
			// Update drawing of selection rectangle
			consoleOverlays.addSelectionBox(new Box(downLocation, location));
		}

		@Override
		public void abilitySelected(int i) {
			// Do nothing
		}

	}

	private class UnitsSelectedState extends InputState {

		@Override
		public void leftDown(EuVector location) {
			// change state
			currentState = selecting;
			leftDownStart = System.currentTimeMillis();
			selecting.setDownLocation(location);
		}

		@Override
		public void leftUp(EuVector location) {
			// do nothing
		}

		@Override
		public void rightDown(EuVector location) {

			// Fire move command

			GameSpaceObject gso = worldState.getGsoAt(location);
			if (gso != null) {
				if (gso.getTeam() == Team.Neutral) {

					ArrayList<UUID> workerIds = (new TypedSelection(worldState.getGsos(selectedUnits))).getWorkerIds();
					if (workerIds.size() > 0) {
						commandQueue.add(new GatherCommand(workerIds, gso.getId()));
					}
				}
				else if (gso.getTeam() != team) {
					commandQueue.add(new AttackCommand(selectedUnits, gso.getId()));
				}
			}
			else {
				commandQueue.add(new MoveCommand(selectedUnits, location.deepCopy()));
			}

		}

		@Override
		public void rightUp(EuVector location) {
			// do nothing
		}

		@Override
		public void mouseMove(EuVector location) {
			// do nothing
		}

		@Override
		public void abilitySelected(int i) {
			// Change state to ability selected
			boolean shouldChangeState = false;

			if (i == -2) {
				shouldChangeState = true;
			}
			else if (i != -2 && selectedCasters.size() > 0) {

				// determine if it is an instantaneous action or a target action

				for (UUID id : selectedCasters) {

					CasterUnit hero = worldState.getCaster(id);
					if (hero != null) {
						List<Ability> abilities = hero.getAbilities();
						if (abilities.size() > i) {
							Ability ability = hero.getAbilities().get(i);
							if (ability.isImediate()) {
								AbilityRequest request = ability.toRequest(id, worldState, new EuVector(0, 0));

								commandQueue.add(new AbilityCommand(request));
							}
							else {
								shouldChangeState = true;
							}
						}
					}
				}
			}
			if (shouldChangeState) {
				currentState = abilitySelected;
				abilitySelected.setAbility(i);
			}
		}

	}

	public class AbilitySelectedState extends InputState {

		private int index;

		public void setAbility(int i) {
			this.index = i;
		}

		@Override
		public void leftDown(EuVector location) {

			// Fire ability
			if (index >= 0) {
				for (UUID id : selectedCasters) {
					CasterUnit hero = worldState.getCaster(id);

					List<Ability> abilities = hero.getAbilities();
					if (abilities.size() > index) {
						Ability ability = hero.getAbilities().get(index);

						AbilityRequest request = ability.toRequest(id, worldState, location);

						commandQueue.add(new AbilityCommand(request));
					}
				}
			}
			else if (index == -2) {
				// Fire attack move command

				MoveCommand attackMove = new MoveCommand(selectedUnits, location.deepCopy());
				attackMove.setAttackWhileMoving(true);
				commandQueue.add(attackMove);

			}

			currentState = unitsSelected;
			index = -1;
		}

		@Override
		public void leftUp(EuVector location) {
			// Do nothing
		}

		@Override
		public void rightDown(EuVector location) {
			// Cancel ability and change state
			currentState = unitsSelected;
		}

		@Override
		public void rightUp(EuVector location) {
			// do nothing
		}

		@Override
		public void mouseMove(EuVector location) {
			// do nothing
		}

		@Override
		public void abilitySelected(int i) {
			// update ability
			index = i;
		}

	}

}
