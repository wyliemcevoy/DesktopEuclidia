package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.map.Segment;
import euclid.two.dim.model.ConvexPoly;
import euclid.two.dim.model.Door;

public class PolyRender implements Renderable {

	private ConvexPoly poly;

	public PolyRender(ConvexPoly poly) {
		this.poly = poly;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(new Color(0, 0, 255, 100));
		for (Segment segment : poly.getSegments()) {
			int x1 = (int) segment.getOne().getX();
			int x2 = (int) segment.getTwo().getX();
			int y1 = (int) segment.getOne().getY();
			int y2 = (int) segment.getTwo().getY();
			g.drawLine(x1, y1, x2, y2);
		}
		g.setColor(new Color(255, 0, 0, 100));
		for (Door door : poly.getDoors()) {
			int x1 = (int) door.getPointOne().getX();
			int x2 = (int) door.getPointTwo().getX();
			int y1 = (int) door.getPointOne().getY();
			int y2 = (int) door.getPointTwo().getY();
			g.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);
		}
	}
}
