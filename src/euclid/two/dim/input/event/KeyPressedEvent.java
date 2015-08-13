package euclid.two.dim.input.event;

public class KeyPressedEvent extends InputEvent {
	private int index;

	public int getIndex() {
		return index;
	}

	public KeyPressedEvent(int index) {
		this.index = index;
	}

	@Override
	public void accept(InputEventVisitor visitor) {
		visitor.visit(this);
	}

}
