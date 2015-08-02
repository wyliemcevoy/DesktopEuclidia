package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.model.EuVector;

public class StringRender implements Renderable
{
	private String message;
	private EuVector location;
	
	public StringRender(String message, EuVector location)
	{
		this.message = message;
		this.location = location;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		
		g.setColor(Color.WHITE);
		g.drawString(message, (int) location.getX(), (int) location.getY());
	}
}