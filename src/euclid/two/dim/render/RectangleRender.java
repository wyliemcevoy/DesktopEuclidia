package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.model.EuVector;

public class RectangleRender implements Renderable {

	private int x, y, width, height;
	private Color color;

	public RectangleRender(EuVector one, EuVector two, Color color) {
		this(one, two);
		this.color = color;
	}

	public RectangleRender(EuVector one, EuVector two) {
		// figure out if one or two is the top left
		double minX = two.getX();
		double maxX = one.getX();
		if (one.getX() < two.getX()) {
			minX = one.getX();
			maxX = two.getX();
		}

		double minY = two.getY();
		double maxY = one.getY();

		if (one.getY() < two.getY()) {
			minY = one.getY();
			maxY = two.getY();
		}

		this.x = (int) minX;
		this.y = (int) minY;
		this.width = (int) (maxX - minX);
		this.height = (int) (maxY - minY);
	}

	public RectangleRender(Box box) {
		this.x = box.getX();
		this.y = box.getY();
		this.width = box.getWidth();
		this.height = box.getHeight();
	}

	@Override
	public void draw(Graphics2D g) {
		Color border = new Color(1, 1, 1, .25f);
		if (color == null) {
			color = new Color(128, 255, 0, 35);
			g.setColor(color);
			g.fillRect(x, y, width, height);
			border = new Color(0, 255, 0);
		}

		g.setColor(border);
		g.drawRect(x, y, width, height);

	}

}
