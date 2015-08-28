package euclid.two.dim.render;

import java.awt.Color;
import java.awt.Graphics2D;

import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.team.Team;

public class TeamCheckRender implements Renderable {
	private int radius;
	private int x, y;

	private double theta;
	private Team team;

	public TeamCheckRender(GameSpaceObject worker) {
		this.x = (int) worker.getPosition().getX();
		this.y = (int) worker.getPosition().getY();
		this.radius = (int) worker.getRadius();
		this.theta = worker.getTheta() + Math.toRadians(215);
		this.team = worker.getTeam();
	}

	@Override
	public void draw(Graphics2D g) {
		switch (team) {
		case Black:
			g.setColor(Color.BLACK);
			break;
		case Blue:
			g.setColor(Color.BLUE);
			break;
		case Green:
			g.setColor(Color.GREEN);
			break;
		case Neutral:
			g.setColor(Color.LIGHT_GRAY);
			break;
		case Red:
			g.setColor(Color.RED);
			break;
		case White:
			g.setColor(Color.WHITE);
			break;
		case Yellow:
			g.setColor(Color.YELLOW);
			break;
		default:
			break;

		}

		g.drawArc(x - radius, y - radius, radius * 2, radius * 2, 0, 360);

	}

}
