package euclid.two.dim.input.event;

public class KeyTypedEvent extends InputEvent {
	private int index;

	public KeyTypedEvent(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void accept(InputEventVisitor visitor) {
		visitor.visit(this);
	}

}
