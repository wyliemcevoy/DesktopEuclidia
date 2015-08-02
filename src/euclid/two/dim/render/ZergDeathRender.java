package euclid.two.dim.render;

import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.etherial.ZergDeath;

public class ZergDeathRender implements Renderable
{
	private int x, y, radius;
	private int frame;
	private Image image;
	
	public ZergDeathRender(ZergDeath zergDeath)
	{
		this.x = (int) zergDeath.getLocation().getX();
		this.y = (int) zergDeath.getLocation().getY();
		this.radius = (int) zergDeath.getRadius();
		frame = 6 - (int) ((zergDeath.getExpireTime() / 1000) * 7);
		this.image = SpriteFlyWeight.getInstance().getZergDeathImage(frame);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		radius = (int) (radius * 1.8);
		
		g.drawImage(image, x - 1 * radius, y - 1 * radius, radius * 2, radius * 2, null);
	}
	
}
