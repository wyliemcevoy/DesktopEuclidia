package euclid.two.dim.input.event;

public class MouseMovedEvent extends InputEvent {
	@Override
	public void accept(InputEventVisitor visitor) {
		visitor.visit(this);
	}
}
