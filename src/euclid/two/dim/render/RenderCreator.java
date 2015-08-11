package euclid.two.dim.render;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.etherial.CircleGraphic;
import euclid.two.dim.etherial.Etherial;
import euclid.two.dim.etherial.Explosion;
import euclid.two.dim.etherial.ExplosiveProjectile;
import euclid.two.dim.etherial.Projectile;
import euclid.two.dim.etherial.Slash;
import euclid.two.dim.etherial.ZergDeath;
import euclid.two.dim.model.ConvexPoly;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.model.Hero;
import euclid.two.dim.model.Minion;
import euclid.two.dim.model.Obstacle;
import euclid.two.dim.updater.UpdateVisitor;
import euclid.two.dim.visitor.EtherialVisitor;
import euclid.two.dim.world.Camera;
import euclid.two.dim.world.WorldState;

public class RenderCreator implements UpdateVisitor, EtherialVisitor {
	private ArrayList<Renderable> renderables;
	private WorldState worldState;
	private Camera camera;
	private static final Object lock = new Object();
	private ArrayBlockingQueue<Camera> cameraChangeRequests;
	private static RenderCreator instance;
	private static final boolean showNavMesh = true;

	private RenderCreator() {
		this.renderables = new ArrayList<Renderable>();
		this.camera = new Camera();
		this.cameraChangeRequests = new ArrayBlockingQueue<Camera>(10);
	}

	public void requestCameraChange(Camera camera) {
		cameraChangeRequests.add(camera);
	}

	public static RenderCreator getInstance() {
		if (instance == null) {
			synchronized (RenderCreator.class) {
				// Double check locking singleton
				if (instance == null) {
					instance = new RenderCreator();
				}
			}
		}
		return instance;
	}

	public void setWorldState(WorldState worldState) {
		this.worldState = worldState;
		this.renderables = new ArrayList<Renderable>(); // Comment this line out for weird graphics

		synchronized (lock) {
			while (!cameraChangeRequests.isEmpty()) {
				try {
					camera = cameraChangeRequests.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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
		if (showNavMesh) {
			for (ConvexPoly poly : worldState.getGameMap().getAllPolygons()) {

				renderables.add(new PolyRender(poly));
				renderables.add(new StringRender("" + poly.getId(), poly.getCenter()));
			}
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
		if (showNavMesh) {
			ConvexPoly heroPoly = worldState.getGameMap().getNavMesh().getPoly(hero.getPosition());
			String id = "";
			if (heroPoly == null) {
				id = "OoB";
			}
			else {
				for (ConvexPoly poly : worldState.getGameMap().getNavMesh().getAllPolygons()) {
					if (poly.contains(hero.getPosition())) {
						id += poly.getId() + " ";
					}
				}
			}
			renderables.add(new StringRender(id, hero.getPosition().add(new EuVector(15, 15))));
		}
	}

	@Override
	public void visit(ExplosiveProjectile explosiveProjectile) {
		this.renderables.add(new StringRender("*", explosiveProjectile.getLocation()));
	}

	@Override
	public void visit(CircleGraphic circleGraphic) {
		this.renderables.add(new CircleRender(circleGraphic));

	}

	public AffineTransform buildTransform() {
		AffineTransform aTransform = new AffineTransform();

		synchronized (lock) {
			aTransform.setToTranslation(camera.getMapX(), camera.getMapY());
			aTransform.rotate(camera.getRotation());
			aTransform.scale(camera.getZoom(), camera.getZoom());
		}
		return aTransform;
	}

	public Camera requestCamera() {
		return camera.deepCopy();
	}
}
