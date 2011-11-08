
package raytracing2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * Not used, the frame rate is too low due to lack of hardware acceleration in shadow algorithm
 * @author Robinerd
 */
public class Player {

	BufferedImage graphics;
	double x = 420, y = 100;
	double yVel = 0;
	double gravity = 300;
	public Player() {
		try {
			graphics = ImageIO.read(new File("blob.png"));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "blob.png not found!");
			System.exit(-1);
		}
	}

	public void update(double dt) {
		yVel += gravity * dt;
		y += yVel * dt;
	}

	public void paint(Graphics2D g2d) {
		g2d.drawImage(graphics, (int)x, (int)y, null);
	}

}
