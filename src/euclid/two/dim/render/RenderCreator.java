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
import euclid.two.dim.model.ResourcePatch;
import euclid.two.dim.model.Unit;
import euclid.two.dim.model.Worker;
import euclid.two.dim.team.Team;
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
	private static final boolean showNavMesh = false;
	private static final boolean showAABBTree = false;
	private ConsoleOverlays boxDrawer;

	private RenderCreator() {
		this.renderables = new ArrayList<Renderable>();
		this.camera = new Camera();
		this.cameraChangeRequests = new ArrayBlockingQueue<Camera>(30);
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
	public void visit(ResourcePatch resourcePatch) {
		this.renderables.add(new ResourcePatchRender(resourcePatch));
	}

	public ArrayList<Renderable> getRenderables() {

		for (UUID id : boxDrawer.getSelectedUnits()) {
			Unit unit = worldState.getUnit(id);
			if (unit != null) {
				EuVector location = unit.getPosition();
				double radius = unit.getRadius();
				renderables.add(new CircleRender(new EuVector(location.getX(), location.getY() + radius * .5), radius * .75, radius / 3, new Color(0, 255, 0, 75)));
			}
		}

		for (GameSpaceObject gso : worldState.getGsos()) {
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
			addTreeToRenderables(root);
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
		AffineTransform t1 = new AffineTransform();
		AffineTransform t2 = new AffineTransform();
		synchronized (lock) {

			t1.scale(camera.getScale(), camera.getScale());
			t2.setToTranslation(-camera.getMapX(), -camera.getMapY());
			t1.concatenate(t2);

		}
		return t1;
	}

	public Camera requestCamera() {
		return camera.deepCopy();
	}

	@Override
	public void visit(Building building) {
		if (showNavMesh) {
			// this.renderables.add(new CircleRender(building.getPosition(), building.getRadius()));
		}

		this.renderables.add(new BuildingRender(building));
	}

	@Override
	public void visit(Worker worker) {
		this.renderables.add(new WorkerRender(worker));

	}

	public ArrayList<Renderable> getOverlays() {

		ArrayList<Renderable> overlays = new ArrayList<Renderable>();

		double width;
		synchronized (lock) {
			width = camera.getWidth();
		}
		overlays.add(new ImageRender("mineralIcon.png", new EuVector(width - 115, 3)));
		overlays.add(new StringRender("" + worldState.getPlayer(Team.Blue).getMinerals(), new EuVector(width - 100, 15)));
		overlays.add(new ImageRender("gasIcon.png", new EuVector(width - 65, 3)));
		overlays.add(new StringRender("" + worldState.getPlayer(Team.Blue).getGas(), new EuVector(width - 50, 15)));

		return overlays;
	}
}
