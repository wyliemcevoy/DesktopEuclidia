package euclid.two.dim.render;

import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.model.EuVector;

public class ImageRender implements Renderable {
	private int x, y;
	private Image image;

	public ImageRender(String imageName, EuVector position) {
		this.x = (int) position.getX();
		this.y = (int) position.getY();
		this.image = SpriteFlyWeight.getInstance().getImage(imageName);
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(image, x, y, null);
	}

}
