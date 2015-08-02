package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.map.ConvexPoly;
import euclid.two.dim.map.Segment;

public class PolyRender implements Renderable
{
	
	private ConvexPoly poly;
	
	public PolyRender(ConvexPoly poly)
	{
		this.poly = poly;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		for (Segment segment : poly.getSegments())
		{
			int x1 = (int) segment.getOne().getX() / 5;
			int x2 = (int) segment.getTwo().getX() / 5;
			int y1 = (int) segment.getOne().getY() / 5;
			int y2 = (int) segment.getTwo().getY() / 5;
			
			g.setColor(Color.BLUE);
			
			g.drawLine(x1, y1, x2, y2);
		}
		
	}
	
}
