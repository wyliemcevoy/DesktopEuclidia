package euclid.two.dim.render;

import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.model.ResourcePatch;

public class ResourcePatchRender implements Renderable {

	private int radius;
	private int x, y;
	private Image mineralPatch;

	public ResourcePatchRender(ResourcePatch resourcePatch) {
		this.x = (int) resourcePatch.getPosition().getX();
		this.y = (int) resourcePatch.getPosition().getY();
		this.radius = (int) resourcePatch.getRadius();
		this.mineralPatch = SpriteFlyWeight.getInstance().getMineralPatch();

	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(mineralPatch, x - radius, y - radius, 2 * radius, 2 * radius, null);
	}

}
