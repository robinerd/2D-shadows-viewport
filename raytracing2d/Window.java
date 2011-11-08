
package raytracing2d;

import javax.swing.JFrame;
/**
 *
 * @author Robinerd
 */
public class Window extends JFrame{

	GamePanel screen = new GamePanel();

	public Window() {
		getContentPane().add(screen);
		setSize(screen.getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
