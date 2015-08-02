package euclid.two.dim.input;

import euclid.two.dim.updater.UpdateEngine;

public class KeyInput implements InputCommand {
	private char character;
	private UpdateEngine updateEngine;

	public KeyInput(UpdateEngine updateEngine, char character) {
		this.character = character;
		this.updateEngine = updateEngine;
	}

	@Override
	public void execute() {

	}

}