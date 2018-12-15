package de.unitrier.st.fst18_public.rembe92.SDRaytracer;

import java.util.concurrent.Callable;

class RaytraceTask implements Callable {
	SDRaytracer tracer;
	int i;

	RaytraceTask(SDRaytracer t, int ii) {
		tracer = t;
		i = ii;
	}

	public RGB[] call() {
		RGB[] col = new RGB[tracer.height];
		for (int j = 0; j < tracer.height; j++) {
			tracer.image[i][j] = new RGB(0, 0, 0);
			for (int k = 0; k < tracer.rayPerPixel; k++) {
				double di = i + (Math.random() / 2 - 0.25);
				double dj = j + (Math.random() / 2 - 0.25);
				if (tracer.rayPerPixel == 1) {
					di = i;
					dj = j;
				}
				Ray eye_ray = new Ray();
				eye_ray.setStart(tracer.startX, tracer.startY, tracer.startZ); // ro
				eye_ray.setDir((float) (((0.5 + di) * tracer.tan_fovx * 2.0) / tracer.width - tracer.tan_fovx),
						(float) (((0.5 + dj) * tracer.tan_fovy * 2.0) / tracer.height - tracer.tan_fovy), (float) 1f); // rd
				eye_ray.normalize();
				col[j] = addColors(tracer.image[i][j], rayTrace(eye_ray, 0), 1.0f / tracer.rayPerPixel);
			}
		}
		return col;
	}

	int maxRec = 3;
	RGB black = new RGB(0.0f, 0.0f, 0.0f);
	RGB ambient_color = new RGB(0.01f, 0.01f, 0.01f);
	
	
	RGB rayTrace(Ray ray, int rec) {
		if (rec > maxRec)
			return black;
		IPoint ip = ray.hitObject(tracer.getTriangles()); // (ray, p, n, triangle);
		if (ip.dist > IPoint.epsilon)
			return lighting(ray, ip, rec);
		else
			return black;
	}

	RGB lighting(Ray ray, IPoint ip, int rec) {
		Vec3D point = ip.ipoint;
		Triangle triangle = ip.triangle;
		RGB color = addColors(triangle.color, ambient_color, 1);
		Ray shadow_ray = new Ray();
		for (Light light : tracer.getLights()) {
			shadow_ray.start = point;
			shadow_ray.dir = light.position.minus(point).mult(-1);
			shadow_ray.dir.normalize();
			IPoint ip2 = shadow_ray.hitObject(tracer.getTriangles());
			if (ip2.dist < IPoint.epsilon) {
				float ratio = Math.max(0, shadow_ray.dir.dot(triangle.normal));
				color = addColors(color, light.color, ratio);
			}
		}
		Ray reflection = new Ray();
		// R = 2N(N*L)-L) L ausgehender Vektor
		Vec3D L = ray.dir.mult(-1);
		reflection.start = point;
		reflection.dir = triangle.normal.mult(2 * triangle.normal.dot(L)).minus(L);
		reflection.dir.normalize();
		RGB rcolor = rayTrace(reflection, rec + 1);
		float ratio = (float) Math.pow(Math.max(0, reflection.dir.dot(L)), triangle.shininess);
		color = addColors(color, rcolor, ratio);
		return (color);
	}
	
	RGB addColors(RGB c1, RGB c2, float ratio) {
		return c1.addColor(c2, ratio);
	}

}
