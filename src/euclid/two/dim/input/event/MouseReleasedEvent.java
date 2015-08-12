package euclid.two.dim.input.event;

import euclid.two.dim.model.EuVector;

public class MouseReleasedEvent extends MouseButtonEvent {
	public MouseReleasedEvent(int button, EuVector location) {
		super(button, location);
	}

	@Override
	public void accept(InputEventVisitor visitor) {
		visitor.visit(this);
	}
}
