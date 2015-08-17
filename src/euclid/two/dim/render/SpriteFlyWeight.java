package euclid.two.dim.render;

import java.awt.Image;

import javax.swing.ImageIcon;

public class SpriteFlyWeight {
	private Image img, sub;
	private static SpriteFlyWeight instance;
	private Image[] zergDeath;
	private Image[] explosion;
	private static final Object lock = new Object();
	private Image hatchery;

	public static SpriteFlyWeight getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SpriteFlyWeight();
				}
			}
		}
		return instance;
	}

	private SpriteFlyWeight() {
		ImageIcon ii = (new ImageIcon(this.getClass().getResource("/imgs/ZergFixed.png")));
		img = ii.getImage();

		this.hatchery = getImage("hatchery.png");

		zergDeath = new Image[7];
		explosion = new Image[7];

		zergDeath[0] = getImage("zerglingDeath1.png");
		zergDeath[1] = getImage("zerglingDeath2.png");
		zergDeath[2] = getImage("zerglingDeath3.png");
		zergDeath[3] = getImage("zerglingDeath4.png");
		zergDeath[4] = getImage("zerglingDeath5.png");
		zergDeath[5] = getImage("zerglingDeath6.png");
		zergDeath[6] = getImage("zerglingDeath7.png");

		explosion[0] = getImage("Ex1.png");
		explosion[1] = getImage("Ex2.png");
		explosion[2] = getImage("Ex3.png");
		explosion[3] = getImage("Ex4.png");
		explosion[4] = getImage("Ex5.png");
		explosion[5] = getImage("Ex6.png");
		explosion[6] = getImage("Ex7.png");
	}

	public Image getImage(String path) {
		return (new ImageIcon(this.getClass().getResource("/imgs/" + path))).getImage();
	}

	public Image getHatchery() {
		return hatchery;
	}

	public Image getZergImage() {
		return img;
	}

	public Image getZergDeathImage(int i) {
		if (i < 0 || i > 6)
			i = 0;
		return zergDeath[i];
	}

	public Image getExplosionImage(int i) {
		if (i < 0 || i > 6)
			i = 0;
		return explosion[i];
	}

}
