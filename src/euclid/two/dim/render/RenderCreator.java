package euclid.two.dim.render;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import euclid.two.dim.datastructure.AABBNode;
import euclid.two.dim.etherial.CircleGraphic;
import euclid.two.dim.etherial.Etherial;
import euclid.two.dim.etherial.Explosion;
import euclid.two.dim.etherial.ExplosiveProjectile;
import euclid.two.dim.etherial.Projectile;
import euclid.two.dim.etherial.Slash;
import euclid.two.dim.etherial.ZergDeath;
import euclid.two.dim.model.Building;
import euclid.two.dim.model.ConvexPoly;
import euclid.two.dim.model.EuVector;
import euclid.two.dim.model.GameSpaceObject;
import euclid.two.dim.model.Hero;
import euclid.two.dim.model.Minion;
import euclid.two.dim.model.Obstacle;
import euclid.two.dim.model.Unit;
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
	private static final boolean showAABBTree = true;
	private ConsoleOverlays boxDrawer;

	private RenderCreator() {
		this.renderables = new ArrayList<Renderable>();
		this.camera = new Camera();
		this.cameraChangeRequests = new ArrayBlockingQueue<Camera>(10);
		this.boxDrawer = ConsoleOverlays.getInstance();
	}

	public void requestCameraChange(Camera camera) {
		try {
			cameraChangeRequests.add(camera);

		} catch (Exception e) {
			System.out.println("Requesting camera change");
			e.printStackTrace();
		}
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
		this.renderables = new ArrayList<Renderable>();

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
		// this.renderables.add(new CirlceRender());
	}

	public ArrayList<Renderable> getRenderables() {

		for (UUID id : boxDrawer.getSelectedUnits()) {
			Unit unit = worldState.getUnit(id);
			EuVector location = unit.getPosition();
			double radius = unit.getRadius();
			renderables.add(new CircleRender(new EuVector(location.getX(), location.getY() + radius * .5), radius * .75, radius / 3, new Color(0, 255, 0, 150)));
		}

		for (GameSpaceObject gso : worldState.getGameSpaceObjects()) {
			gso.acceptUpdateVisitor(this);
		}

		for (Etherial etherial : worldState.getEtherials()) {
			etherial.accept(this);
		}

		renderables.addAll(boxDrawer.getOverlays());

		if (showNavMesh) {
			for (ConvexPoly poly : worldState.getGameMap().getAllPolygons()) {
				renderables.add(new PolyRender(poly));
				renderables.add(new StringRender("" + poly.getId(), poly.getCenter()));
			}
		}

		if (showAABBTree) {
			worldState.recalculateAABBTree();
			AABBNode root = worldState.getAABBRoot();
			// addTreeToRenderables(root);
			// root.printArea();
		}

		return renderables;
	}

	private void addTreeToRenderables(AABBNode node) {
		if (node != null) {
			renderables.add(new RectangleRender(node.getAabb().getTopLeft(), node.getAabb().getBottomRight(), new Color(1, 1, 1, .02f)));
			addTreeToRenderables(node.getLeft());
			addTreeToRenderables(node.getRight());
		}

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

	@Override
	public void accept(Building building) {
		if (showNavMesh) {
			this.renderables.add(new CircleRender(building.getPosition(), building.getRadius()));
		}

		this.renderables.add(new BuildingRender(building));
	}
}
