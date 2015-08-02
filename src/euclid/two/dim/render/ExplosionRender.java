package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import euclid.two.dim.etherial.Explosion;

public class ExplosionRender implements Renderable
{
	private Color color;
	private int radius;
	private int x;
	private int y;
	
	private int frame;
	private Image image;
	
	public ExplosionRender(Explosion explosion)
	{
		this.x = (int) explosion.getLocation().getX() - radius;
		this.y = (int) explosion.getLocation().getY() - radius;
		this.radius = (int) explosion.getRadius();
		frame = 6 - (int) ((explosion.getExpireTime() / 500) * 7);
		this.image = SpriteFlyWeight.getInstance().getExplosionImage(frame);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		radius = (int) (radius * 2);
		
		g.drawImage(image, x - 1 * radius, y - 1 * radius, radius * 2, radius * 2, null);
	}
	
}
