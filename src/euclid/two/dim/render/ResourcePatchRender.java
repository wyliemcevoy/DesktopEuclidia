package euclid.two.dim.render;

import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.model.ResourcePatch;

public class ResourcePatchRender implements Renderable {

	private int radius;
	private int x, y;
	private Image resourceImage;

	public ResourcePatchRender(ResourcePatch resourcePatch) {
		this.x = (int) resourcePatch.getPosition().getX();
		this.y = (int) resourcePatch.getPosition().getY();
		this.radius = (int) resourcePatch.getRadius();

		switch (resourcePatch.getType()) {
		case GAS:
			this.resourceImage = SpriteFlyWeight.getInstance().getVespene();
			break;
		case MINERALS:
			this.resourceImage = SpriteFlyWeight.getInstance().getMineralPatch();
			break;
		default:
			this.resourceImage = SpriteFlyWeight.getInstance().getVespene();

			break;

		}

	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(resourceImage, x - radius, y - radius, 2 * radius, 2 * radius, null);
	}

}
