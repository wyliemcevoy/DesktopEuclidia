package euclid.two.dim.input;

import euclid.two.dim.etherial.Explosion;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.model.Minion;
import euclid.two.dim.model.Unit;
import euclid.two.dim.updater.UpdateEngine;
import euclid.two.dim.world.WorldState;

public class ClickEvent implements InputCommand {
	private int x, y;
	private UpdateEngine updateEngine;

	public ClickEvent(int x, int y, UpdateEngine updateEngine) {
		this.x = x;
		this.y = y;
		this.updateEngine = updateEngine;
	}

	public ClickEvent(ClickEvent clone) {
		this.x = clone.getX();
		this.y = clone.getY();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void execute() {
		WorldState worldState = updateEngine.getCurrentWorldState();
		EuVector adjustedTarget = new EuVector(x / worldState.getCamera().getZoom(), y / worldState.getCamera().getZoom());
		worldState.addEtherial(new Explosion(new EuVector(adjustedTarget)));

		Unit unit = (Unit) worldState.getSelected().get(0);
		Minion target = null;

		for (GameSpaceObject gso : worldState.getGameSpaceObjects()) {
			if (adjustedTarget.subtract(gso.getPosition()).getMagnitudeSquared() < gso.getRadius() * gso.getRadius()) {
				unit.setTarget(gso.getId());
			}
		}
	}
}
