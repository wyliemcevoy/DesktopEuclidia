package euclid.two.dim;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.command.Command;
import euclid.two.dim.command.CommandManager;

public class CommandQueue implements CommandManager {
	private ArrayBlockingQueue<Command> queue;
	private static CommandQueue instance;

	public static CommandQueue getInstance() {
		if (instance == null) {
			synchronized (CommandQueue.class) {
				if (instance == null) {
					instance = new CommandQueue();
				}
			}
		}
		return instance;
	}

	private CommandQueue() {
		this.queue = new ArrayBlockingQueue<Command>(20);
	}

	@Override
	public void put(Command command) {
		queue.add(command);

	}

	@Override
	public ArrayList<Command> getAllCommands() {
		ArrayList<Command> result = new ArrayList<Command>();

		while (!queue.isEmpty()) {
			result.add(queue.poll());
		}

		return result;
	}

}
