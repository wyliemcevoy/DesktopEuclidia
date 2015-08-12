package euclid.two.dim.render;

import java.util.ArrayList;

public class ConsoleOverlays {

	private static final Object lock = new Object();
	private boolean drawSelectionBox;
	private Box box;
	private static ConsoleOverlays instance;

	private ConsoleOverlays() {
		this.drawSelectionBox = false;
	}

	public static ConsoleOverlays getInstance() {
		if (instance == null) {
			synchronized (ConsoleOverlays.class) {
				if (instance == null) {
					instance = new ConsoleOverlays();
				}
			}
		}

		return instance;
	}

	public void addSelectionBox(Box box) {
		synchronized (lock) {
			this.drawSelectionBox = true;
			this.box = box;
		}
	}

	public void stopSelectionBox() {
		synchronized (lock) {
			this.drawSelectionBox = false;
		}
	}

	public ArrayList<Renderable> getOverlays() {
		ArrayList<Renderable> overlays = new ArrayList<Renderable>();

		synchronized (lock) {
			if (drawSelectionBox) {
				overlays.add(new RectangleRender(box));
			}
		}
		return overlays;

	}
}
