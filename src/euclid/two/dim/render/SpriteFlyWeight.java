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
		ImageIcon ii = new ImageIcon("C:\\Users\\Wylie\\Pictures\\Game\\ZergFixed.png");
		img = ii.getImage();
		System.out.println(" width " + img.getWidth(null));
		// img = ImageIO.read(new File("images/Zergling.png"));
		/*
		 * sub = copy.getSubimage( // takes each part of the character sprite and breaks it up into 9 different sprites i * width, j * height, width, height
		 */

		this.hatchery = getImage("C:\\Users\\Wylie\\Pictures\\Game\\hatchery.png");

		zergDeath = new Image[7];
		explosion = new Image[7];

		zergDeath[0] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\zerglingDeath1.png");
		zergDeath[1] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\zerglingDeath2.png");
		zergDeath[2] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\zerglingDeath3.png");
		zergDeath[3] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\zerglingDeath4.png");
		zergDeath[4] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\zerglingDeath5.png");
		zergDeath[5] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\zerglingDeath6.png");
		zergDeath[6] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\zerglingDeath7.png");

		explosion[0] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\Ex1.png");
		explosion[1] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\Ex2.png");
		explosion[2] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\Ex3.png");
		explosion[3] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\Ex4.png");
		explosion[4] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\Ex5.png");
		explosion[5] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\Ex6.png");
		explosion[6] = getImage("C:\\Users\\Wylie\\Pictures\\Game\\Ex7.png");
	}

	public Image getImage(String path) {
		ImageIcon ii = new ImageIcon(path);
		return ii.getImage();
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
