package euclid.two.dim.render;

import java.util.ArrayList;

import euclid.two.dim.etherial.CircleGraphic;
import euclid.two.dim.etherial.Etherial;
import euclid.two.dim.etherial.Explosion;
import euclid.two.dim.etherial.ExplosiveProjectile;
import euclid.two.dim.etherial.Projectile;
import euclid.two.dim.etherial.Slash;
import euclid.two.dim.etherial.ZergDeath;
import euclid.two.dim.map.ConvexPoly;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.model.Hero;
import euclid.two.dim.model.Minion;
import euclid.two.dim.model.Obstacle;
import euclid.two.dim.updater.UpdateVisitor;
import euclid.two.dim.visitor.EtherialVisitor;
import euclid.two.dim.world.WorldState;

public class RenderCreator implements UpdateVisitor, EtherialVisitor {
	private ArrayList<Renderable> renderables;
	private WorldState worldState;

	public RenderCreator(WorldState worldState) {
		this.worldState = worldState;
		this.renderables = new ArrayList<Renderable>();

	}

	public RenderCreator() {
		this(null);
	}

	public void setWorldState(WorldState worldState) {
		this.worldState = worldState;
		this.renderables = new ArrayList<Renderable>();
	}

	public void add(GameSpaceObject gso) {
		gso.acceptUpdateVisitor(this);
	}

	public void add(Etherial etherial) {
		etherial.accept(this);
	}

	@Override
	public void visit(Projectile projectile) {

		this.renderables.add(new ProjectileRender(projectile));
	}

	@Override
	public void visit(Explosion explosion) {
		this.renderables.add(new ExplosionRender(explosion));
	}

	@Override
	public void visit(Minion unit) {
		this.renderables.add(new UnitRender(unit));

	}

	@Override
	public void visit(Obstacle obstacle) {
		// TODO Auto-generated method stub

	}

	public ArrayList<Renderable> getRenderables() {

		for (GameSpaceObject gso : worldState.getGameSpaceObjects()) {

			gso.acceptUpdateVisitor(this);

		}

		for (Etherial etherial : worldState.getEtherials()) {
			etherial.accept(this);
		}

		for (ConvexPoly poly : worldState.getGameMap().getPolygons()) {
			renderables.add(new PolyRender(poly));
		}

		return renderables;
	}

	@Override
	public void visit(Slash slash) {
		this.renderables.add(new SlashRender(slash));
	}

	@Override
	public void visit(ZergDeath zergDeath) {
		this.renderables.add(new ZergDeathRender(zergDeath));

	}

	@Override
	public void visit(Hero hero) {
		this.renderables.add(new HeroRender(hero));

	}

	@Override
	public void visit(ExplosiveProjectile explosiveProjectile) {
		this.renderables.add(new StringRender("*", explosiveProjectile.getLocation()));
	}

	@Override
	public void visit(CircleGraphic circleGraphic) {
		this.renderables.add(new CircleRender(circleGraphic));

	}
}
