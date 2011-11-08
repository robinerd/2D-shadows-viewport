package raytracing2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Robinerd
 */
public class GamePanel extends JPanel implements MouseMotionListener {

	private static final int obstacleColor = -16777216;

	/**
	 * 0 - no shadows
	 * 1 - exploration mode
	 * 2 - eye range mode
	 * 3 - full range mode
	 */
	protected int mode = 3; //not used any more
	protected Point cursorPos;
	BufferedImage obstacleMap;
	ArrayList<Vector> obstaclePoints;
	boolean debugFrameTime = true; //set to true to test maximum repaint frequence with output of time per frame

	public GamePanel() {
		cursorPos = new Point(0, 0);
		try {
			obstacleMap = ImageIO.read(new File("obstacles.png"));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "obstacles.png not found!");
			System.exit(-1);
		}
		setSize(obstacleMap.getWidth(), obstacleMap.getHeight());
		addMouseMotionListener(this);

		obstaclePoints = new ArrayList<Vector>(5000);
		for (int x = 1; x < obstacleMap.getWidth() - 1; x++) {
			for (int y = 1; y < obstacleMap.getHeight() - 1; y++) {
			if (obstacleMap.getRGB(x, y) == obstacleColor
						&& (obstacleMap.getRGB(x + 1, y) != obstacleColor
						|| obstacleMap.getRGB(x - 1, y) != obstacleColor
						|| obstacleMap.getRGB(x, y + 1) != obstacleColor
						|| obstacleMap.getRGB(x, y - 1) != obstacleColor)) {
					obstaclePoints.add(new Vector(x,y));
				}
			}
		}
//		rayImage = new BufferedImage(1000, 1, BufferedImage.TYPE_INT_ARGB);
//		rayGfx = (Graphics2D) rayImage.getGraphics();
	}

//	private void traceRayTo(int x, int y, Graphics2D g2d) {
//		double angle = Math.atan2(y - cursorPos.y, x - cursorPos.x);
//		AffineTransform toRaySpace = AffineTransform.getRotateInstance(-angle);
//		toRaySpace.translate(-cursorPos.x, -cursorPos.y);
//		rayGfx.setColor(Color.black);
//		rayGfx.fillRect(0, 0, 1000, 1);
//		rayGfx.drawImage(obstacleMap, toRaySpace, this);
//
//		int intersectionIndex = -1;
//		int absorbed = 0;
//		for (int i = 0; i < 1000; i++) {
//			if (mode == 1) {
//				if (rayImage.getRGB(i, 0) != -16777216) {
//					intersectionIndex = i + 10;
//					break;
//				}
//			} else if (mode == 2) {
//				if (rayImage.getRGB(i, 0) != -16777216) {
//					absorbed++;
//				} else if (absorbed > 5) {
//					intersectionIndex = i;
//					break;
//				}
//			} else {
//				if (rayImage.getRGB(i, 0) != -16777216) {
//					absorbed++;
//				} else if (absorbed > 1) {
//					intersectionIndex = i;
//					break;
//				}
//			}
//		}
//		if (intersectionIndex == -1) {
//			return;
//		}
//		int intersectionX = cursorPos.x + (int) (intersectionIndex * Math.cos(angle));
//		int intersectionY = cursorPos.y + (int) (intersectionIndex * Math.sin(angle));
//		g2d.drawLine(intersectionX, intersectionY, x, y);
//	}
//
	
	
	//declared and reused for performance reasons:
	int[] xValues = new int[4];
	int[] yValues = new int[4];
	
	//debugging variables
	int frameCounter = 0;
	long frameTimerStartTime = System.currentTimeMillis();
	/*
	 * For each obstacle pixel, a very sharp polygon is drawn pointing away from the user.
	 * The polygon is shaped almost like a narrow line, 2 pixels wide in one end and wider further from the pointer.
	 * Polygon sharpness varies depending on obstacle pixel's distance to the pointer,
	 * The line/polygon must be wider at the far end to make sure the shadow gets completely filled, and not appears stripy.
	 * For performance reasons the exact correct measures of the polygon is not computed,
	 * instead a few levels are provided to make a fair approximation. 
	 * In a worst case scenario the shadow might appear stripy or too wide spreading,
	 * but this is mostly not noticable at all.
	 * Drawing all these polygons is a slow process and is the main cause for low frame rates in this implementation.
	 * This is still the fastest method I've found so far that is possible to implement in java, though.
	 */
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		//clear
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setColor(Color.black);
		
		for(Vector v : obstaclePoints) {
			Vector delta = v.copy();
			delta.subtract(cursorPos.x, cursorPos.y);
			float deltaLength = delta.length();
			delta.normalize();

			int offset = 70;
			if(deltaLength < 100 && deltaLength > 5) {
				offset = (int)(5000 / deltaLength);
			} else if( deltaLength > 0.5f) {
				offset = (int)(3000 / deltaLength);
			} else {
				offset = -1;
			}

			if(offset > 0) {
				if(delta.x <= 0 && delta.y <= 0) {
					g2d.fillPolygon(
						new int[]{(int)v.x + 1, (int)v.x - 1, (int) (v.x + delta.x * 1500 - offset), (int) (v.x + delta.x * 1500 + offset)},
						new int[]{(int)v.y - 1, (int)v.y + 1, (int) (v.y + delta.y * 1500 + offset), (int) (v.y + delta.y * 1500 - offset)},
						4);
				} else if(delta.x >= 0 && delta.y <= 0) {
					g2d.fillPolygon(
						new int[]{(int)v.x - 1, (int)v.x + 1, (int) (v.x + delta.x * 1500 + offset), (int) (v.x + delta.x * 1500 - offset)},
						new int[]{(int)v.y - 1, (int)v.y + 1, (int) (v.y + delta.y * 1500 + offset), (int) (v.y + delta.y * 1500 - offset)},
						4);
				} else if(delta.x >= 0 && delta.y >= 0) {
					g2d.fillPolygon(
						new int[]{(int)v.x - 1, (int)v.x + 1, (int) (v.x + delta.x * 1500 + offset), (int) (v.x + delta.x * 1500 - offset)},
						new int[]{(int)v.y + 1, (int)v.y - 1, (int) (v.y + delta.y * 1500 - offset), (int) (v.y + delta.y * 1500 + offset)},
						4);
				} else if(delta.x <= 0 && delta.y >= 0) {
					g2d.fillPolygon(
						new int[]{(int)v.x + 1, (int)v.x - 1, (int) (v.x + delta.x * 1500 - offset), (int) (v.x + delta.x * 1500 + offset)},
						new int[]{(int)v.y + 1, (int)v.y - 1, (int) (v.y + delta.y * 1500 - offset), (int) (v.y + delta.y * 1500 + offset)},
						4);
				}
			}
		}
//		if (mode != 3) {
//			//obstacles
//			g2d.drawImage(obstacleMap, 0, 0, this);
//		}
//
//		if (mode != 0) {
//			//obscuration
//			BasicStroke stroke = new BasicStroke(2);
//			g2d.setStroke(stroke);
//			g2d.setColor(Color.black);
//			for (int x = 0; x < getWidth(); x++) {
//				traceRayTo(x, getHeight() - 1, g2d);
//				traceRayTo(x, 0, g2d);
//			}
//			for (int y = 0; y < getHeight(); y++) {
//				traceRayTo(0, y, g2d);
//				traceRayTo(getWidth() - 1, y, g2d);
//			}
//		}
//		if (mode == 3) {
//			//obstacles
//			g2d.drawImage(obstacleMap, 0, 0, this);
//		}

		//lamp
		g2d.setColor(Color.orange);
		g2d.fillOval(cursorPos.x - 3, cursorPos.y - 3, 7, 7);
		if(debugFrameTime) {
			repaint();
			frameCounter++;
			if(frameCounter > 50)
			{
				long currentTime = System.currentTimeMillis();
				System.out.println("Time per frame (ms): "+(currentTime - frameTimerStartTime)/frameCounter);
				frameCounter = 0;
				frameTimerStartTime = currentTime;
			}
			
		}
	}
	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		cursorPos.x = e.getX();
		cursorPos.y = e.getY();
		repaint();
	}
}
