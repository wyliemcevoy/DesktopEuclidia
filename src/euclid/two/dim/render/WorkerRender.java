package euclid.two.dim.render;

import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.model.Worker;
import euclid.two.dim.team.Team;

public class WorkerRender implements Renderable {
	private int radius;
	private int x, y;
	private Image drone;
	private Image resource;
	private double theta;
	private Team team;

	public WorkerRender(Worker worker) {
		this.x = (int) worker.getPosition().getX();
		this.y = (int) worker.getPosition().getY();
		this.radius = (int) worker.getRadius();
		this.drone = SpriteFlyWeight.getInstance().getMineralDrone();
		this.theta = worker.getTheta() + Math.toRadians(215);
		this.team = worker.getTeam();

		if (worker.getCarryAmount() > 0) {

			switch (worker.getCarryType()) {
			case GAS:
				this.resource = SpriteFlyWeight.getInstance().getCollectedGas();
				break;
			case MINERALS:
				this.resource = SpriteFlyWeight.getInstance().getCollectedMinerals();
				break;
			default:
				break;

			}

		}
	}

	@Override
	public void draw(Graphics2D g) {

		g.rotate(theta, x, y);
		g.drawImage(drone, x - radius, y - radius, 2 * radius, 2 * radius, null);
		if (resource != null) {
			g.drawImage(resource, x + 1, y + 1, 2 * 3, 2 * 3, null);
		}
		g.rotate(-1 * theta, x, y);
	}
}
