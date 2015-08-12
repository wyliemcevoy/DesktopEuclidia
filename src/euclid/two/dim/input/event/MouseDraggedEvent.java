package euclid.two.dim.input.event;

import euclid.two.dim.model.EuVector;

public class MouseDraggedEvent extends MouseButtonEvent {

	public MouseDraggedEvent(int button, EuVector location) {
		super(button, location);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept(InputEventVisitor visitor) {
		visitor.visit(this);
	}

}
