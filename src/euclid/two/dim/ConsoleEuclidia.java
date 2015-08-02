package euclid.two.dim;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.input.ClickEvent;
import euclid.two.dim.input.InputManager;
import euclid.two.dim.model.Hero;
import euclid.two.dim.render.ConsoleRenderer;
import euclid.two.dim.team.Agent;
import euclid.two.dim.team.Game;
import euclid.two.dim.team.Team;
import euclid.two.dim.updater.UpdateEngine;
import euclid.two.dim.world.WorldState;
import euclid.two.dim.world.WorldStateFactory;

public class ConsoleEuclidia
{
	private ConsoleRenderer consoleRenderer;
	private ArrayBlockingQueue<WorldState> rendererQueue;
	private ArrayBlockingQueue<ClickEvent> clickQueue;
	private UpdateEngine updateEngine;
	
	ConsoleEuclidia()
	{
		
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
		
		updateEngine = new UpdateEngine(rendererQueue, inputManager, game);
		inputManager.setUpdateEngine(updateEngine);
		
		ArrayList<UUID> ids = new ArrayList<UUID>();
		Agent agent = new Agent(Team.Red);
		updateEngine.addAgent(agent);
		
		updateEngine.setWorldState(worldState);
		rendererQueue.add(worldState);
		
		consoleRenderer = new ConsoleRenderer(rendererQueue, inputManager);
		consoleRenderer.start();
		
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		updateEngine.start();
	}
}
