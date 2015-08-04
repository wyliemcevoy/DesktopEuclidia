package euclid.two.dim.input;

public class ScrollListener extends Thread {

	private boolean stopRequested;
	private InputManager inputManager;

	public ScrollListener(InputManager inputManager) {
		this.stopRequested = false;
		this.inputManager = inputManager;
	}

	public void run() {
		while (!stopRequested) {

			try {
				Thread.sleep(10);

				if (inputManager.scrollRequired()) {
					inputManager.requestScroll();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void requestStop() {
		this.stopRequested = true;
	}

}
