
package raytracing2d;

/**
 *
 * @author Robin Lindh Nilsson
 */
public class Box {

	public int xMin, xMax, yMin, yMax;

	public Box(int xMin, int xMax, int yMin, int yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public Box() {
		xMin = 0;
		xMax = 0;
		yMin = 0;
		yMax = 0;
	}
}
