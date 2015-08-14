package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.etherial.CircleGraphic;
import euclid.two.dim.model.EuVector;

public class CircleRender implements Renderable {
	private int x, y, radiusX, radiusY;
	private Color color;

	public CircleRender(CircleGraphic circle) {
		this.x = (int) circle.getLocation().getX();
		this.y = (int) circle.getLocation().getY();
		this.radiusX = circle.getRadius();
		this.radiusY = circle.getRadius();
		this.color = new Color(1, 1, 1, .5f);
	}

	public CircleRender(EuVector location, double radius) {
		this.x = (int) location.getX();
		this.y = (int) location.getY();
		this.radiusX = (int) radius;
		this.radiusY = (int) radius;
		this.color = new Color(1, 1, 1, .5f);
	}

	public CircleRender(EuVector location, double radiusX, double radiusY, Color color) {
		this.x = (int) location.getX();
		this.y = (int) location.getY();
		this.radiusX = (int) radiusX;
		this.radiusY = (int) radiusY;
		this.color = color;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.drawArc(x - radiusX, y - radiusY, radiusX * 2, radiusY * 2, 0, 360);
	}
}
