package euclid.two.dim.input.event;

public class KeyTypedEvent extends InputEvent {

	@Override
	public void accept(InputEventVisitor visitor) {
		visitor.visit(this);
	}

}
