package euclid.two.dim;

import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.input.AgentManager;
import euclid.two.dim.input.InputManager;
import euclid.two.dim.input.PlayerManager;
import euclid.two.dim.input.event.InputEvent;
import euclid.two.dim.model.Hero;
import euclid.two.dim.team.Agent;
import euclid.two.dim.team.Game;
import euclid.two.dim.team.Team;
import euclid.two.dim.threads.WorldStatePublisher;
import euclid.two.dim.world.WorldState;
import euclid.two.dim.world.WorldStateFactory;

public class ConsoleEuclidia {
	private ConsoleRenderer consoleRenderer;
	private ArrayBlockingQueue<WorldState> worldStateQueue;
	private UpdateEngineThread updateEngineThread;
	private WorldStatePublisher worldStatePublisher;
	private PlayerManager playerManager;
	private AgentManager agentManager;

	ConsoleEuclidia() {

		worldStateQueue = new ArrayBlockingQueue<WorldState>(5);

		WorldStateFactory f = new WorldStateFactory();
		WorldState worldState = f.createVsWorldState(Team.Red);

		ArrayBlockingQueue<InputEvent> inputEvents = new ArrayBlockingQueue<InputEvent>(20);

		Hero hero = f.createHero(Team.Blue);
		HumanRtsPlayer human = new HumanRtsPlayer(Team.Blue);
		human.acceptWorldState(worldState.deepCopy());

		worldStatePublisher = new WorldStatePublisher(worldStateQueue);

		InputManager inputManager = new InputManager(human, inputEvents);
		worldState.addObject(hero);
		playerManager = new PlayerManager(human, inputEvents);
		Game game = new Game();
		game.addPlayer(human);

		Agent agent = new Agent(Team.Red);
		game.addPlayer(agent);

		this.agentManager = new AgentManager(agent);

		updateEngineThread = new UpdateEngineThread(worldState, game);
		updateEngineThread.setWorldStateQueue(worldStateQueue);

		worldStateQueue.add(worldState);

		consoleRenderer = new ConsoleRenderer(inputManager);

		registerObservers();
		launchThreads();

	}

	private void registerObservers() {
		worldStatePublisher.addListener(consoleRenderer);
		worldStatePublisher.addListener(playerManager);
		worldStatePublisher.addListener(agentManager);
	}

	private void launchThreads() {
		(new Thread(worldStatePublisher)).start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		(new Thread(agentManager)).start();
		(new Thread(playerManager)).start();
		consoleRenderer.start();

		updateEngineThread.start();
	}
}
