package euclid.two.dim.render;

import euclid.two.dim.model.EuVector;

public class Box {
	private int x, y, width, height;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Box(EuVector one, EuVector two) {
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

}
