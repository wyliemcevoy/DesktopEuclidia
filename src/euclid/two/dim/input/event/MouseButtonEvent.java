package euclid.two.dim.input.event;

import euclid.two.dim.model.EuVector;

public abstract class MouseButtonEvent extends InputEvent {
	protected int button;
	protected EuVector location;

	public MouseButtonEvent(int button, EuVector location) {
		this.button = button;
		this.location = location;
	}

	public int getButton() {
		return button;
	}

	public EuVector getLocation() {
		return location.deepCopy();
	}
}
