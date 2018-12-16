package de.unitrier.st.fst18_public.rembe92.SDRaytracer;

import java.util.List;

//Ein Würfel, bestehend aus Dreiecken
class Cube {
	/**
	 * erzeugt ein Würfel und fügt in in die Liste von Dreiecken hinzu.
	 * 
	 * @param triangles
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @param h
	 * @param d
	 * @param c
	 * @param sh
	 */

	private Cube() {
		throw new IllegalStateException("Utility class");
	}

	public static void addCube(List<Triangle> triangles, int x, int y, int z, int w, int h, int d, RGB c, float sh) { // front
		triangles.add(new Triangle(new Vec3D(x, y, z), new Vec3D(x + w, y, z), new Vec3D(x, y + h, z), c, sh));
		triangles.add(new Triangle(new Vec3D(x + w, y, z), new Vec3D(x + w, y + h, z), new Vec3D(x, y + h, z), c, sh));
		// left
		triangles.add(new Triangle(new Vec3D(x, y, z + d), new Vec3D(x, y, z), new Vec3D(x, y + h, z), c, sh));
		triangles.add(new Triangle(new Vec3D(x, y + h, z), new Vec3D(x, y + h, z + d), new Vec3D(x, y, z + d), c, sh));
		// right
		triangles.add(
				new Triangle(new Vec3D(x + w, y, z), new Vec3D(x + w, y, z + d), new Vec3D(x + w, y + h, z), c, sh));
		triangles.add(new Triangle(new Vec3D(x + w, y + h, z), new Vec3D(x + w, y, z + d),
				new Vec3D(x + w, y + h, z + d), c, sh));
		// top
		triangles.add(new Triangle(new Vec3D(x + w, y + h, z), new Vec3D(x + w, y + h, z + d), new Vec3D(x, y + h, z),
				c, sh));
		triangles.add(new Triangle(new Vec3D(x, y + h, z), new Vec3D(x + w, y + h, z + d), new Vec3D(x, y + h, z + d),
				c, sh));
		// bottom
		triangles.add(new Triangle(new Vec3D(x + w, y, z), new Vec3D(x, y, z), new Vec3D(x, y, z + d), c, sh));
		triangles.add(new Triangle(new Vec3D(x, y, z + d), new Vec3D(x + w, y, z + d), new Vec3D(x + w, y, z), c, sh));
		// back
		triangles.add(
				new Triangle(new Vec3D(x, y, z + d), new Vec3D(x, y + h, z + d), new Vec3D(x + w, y, z + d), c, sh));
		triangles.add(new Triangle(new Vec3D(x + w, y, z + d), new Vec3D(x, y + h, z + d),
				new Vec3D(x + w, y + h, z + d), c, sh));

	}
}
