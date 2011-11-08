/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package raytracing2d;

/**
 *
 * @author Robin
 */
public class Vector {
	public float x,y;

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void add (float dx, float dy) {
		x += dx;
		y += dy;
	}

	public void subtract(float dx, float dy) {
		x -= dx;
		y -= dy;
	}

	public void mult(float scalar) {
		x *= scalar;
		y *= scalar;
	}

	public float length() {
		return (float) Math.sqrt(x*x + y*y);
	}

	public void normalize() {
		mult(1f/length());
	}

	protected Vector copy() {
		return new Vector(x,y);
	}
}
