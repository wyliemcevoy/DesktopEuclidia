package euclid.two.dim.render;

import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.model.Building;

public class BuildingRender implements Renderable {
	private int x, y;
	private int radius;
	private Image hatchery;

	public BuildingRender(Building building) {
		this.x = (int) building.getPosition().getX();
		this.y = (int) building.getPosition().getY();
		this.radius = (int) building.getRadius();
		this.hatchery = SpriteFlyWeight.getInstance().getHatchery();
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(hatchery, x - radius, y - radius, 2 * radius, 2 * radius, null);
	}
}
