package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.etherial.CircleGraphic;
import euclid.two.dim.model.EuVector;

public class CircleRender implements Renderable {
	private int x, y, radius;

	public CircleRender(CircleGraphic circle) {
		this.x = (int) circle.getLocation().getX();
		this.y = (int) circle.getLocation().getY();
		this.radius = circle.getRadius();
	}

	public CircleRender(EuVector location, double radius) {
		this.x = (int) location.getX();
		this.y = (int) location.getY();
		this.radius = (int) radius;
	}

	@Override
	public void draw(Graphics2D g) {
		float alpha = (float) .5;
		g.setColor(new Color(1, 1, 1, alpha));
		g.drawArc(x - radius, y - radius, radius * 2, radius * 2, 0, 360);
	}
}
