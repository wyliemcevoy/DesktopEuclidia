package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.etherial.Slash;

public class SlashRender implements Renderable {

	private Color color;
	private int rad;
	private int toX;
	private int toY;
	private int fromX;
	private int fromY;

	public SlashRender(Slash slash) {
		this.color = new Color(255, 0, 0, 155);
		this.rad = (int) slash.getExpireTime() / 500;
		this.toX = (int) slash.getTo().getX();
		this.toY = (int) slash.getTo().getY();
		this.fromX = (int) slash.getFrom().getX();
		this.fromY = (int) slash.getFrom().getY();
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.drawLine(toX, toY, fromX, fromY);
	}
}
