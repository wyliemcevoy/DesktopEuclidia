package euclid.two.dim.render;

import java.awt.Graphics2D;

import euclid.two.dim.etherial.CircleGraphic;

public class CircleRender implements Renderable
{
	private int x, y, radius;
	
	public CircleRender(CircleGraphic circle)
	{
		this.x = (int) circle.getLocation().getX();
		this.y = (int) circle.getLocation().getY();
		this.radius = circle.getRadius();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawArc(x - radius, y - radius, radius * 2, radius * 2, 0, 360);
		
	}
	
}
