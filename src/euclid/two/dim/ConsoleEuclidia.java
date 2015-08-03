package euclid.two.dim;

import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.input.InputManager;
import euclid.two.dim.model.Hero;
import euclid.two.dim.render.ConsoleRenderer;
import euclid.two.dim.team.Agent;
import euclid.two.dim.team.Game;
import euclid.two.dim.team.Team;
import euclid.two.dim.world.WorldState;
import euclid.two.dim.world.WorldStateFactory;

public class ConsoleEuclidia {
	private ConsoleRenderer consoleRenderer;
	private ArrayBlockingQueue<WorldState> rendererQueue;
	private UpdateEngineThread updateEngineThread;

	ConsoleEuclidia() {

		rendererQueue = new ArrayBlockingQueue<WorldState>(5);

		WorldStateFactory f = new WorldStateFactory();
		WorldState worldState = f.createVsWorldState(Team.Red);

		Hero hero = f.createHero(Team.Blue);
		HumanMobaPlayer human = new HumanMobaPlayer(Team.Blue, hero.getId());
		InputManager inputManager = new InputManager(human);
		worldState.addObject(hero);

		Game game = new Game();
		game.addPlayer(human);
		game.addPlayer(new Agent(Team.Red));

		updateEngineThread = new UpdateEngineThread(worldState, game);
		updateEngineThread.setRendererQueue(rendererQueue);
		inputManager.setUpdateEngine(updateEngineThread.getUpdateEngine());

		rendererQueue.add(worldState);

		consoleRenderer = new ConsoleRenderer(rendererQueue, inputManager);
		consoleRenderer.start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		updateEngineThread.start();
	}
}