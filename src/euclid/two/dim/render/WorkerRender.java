package euclid.two.dim.render;

import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.model.Worker;

public class WorkerRender implements Renderable {
	private int radius;
	private int x, y;
	private Image drone;

	public WorkerRender(Worker worker) {
		this.x = (int) worker.getPosition().getX();
		this.y = (int) worker.getPosition().getY();
		this.radius = (int) worker.getRadius();
		this.drone = SpriteFlyWeight.getInstance().getMineralDrone();

	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(drone, x - radius, y - radius, 2 * radius, 2 * radius, null);
	}
}
