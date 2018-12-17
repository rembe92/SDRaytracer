package de.unitrier.st.fst18_public.rembe92.SDRaytracer;

import java.util.concurrent.Callable;

class RaytraceTask implements Callable {
	SDRaytracer tracer;
	int i;
	RGB black = new RGB(0.0f, 0.0f, 0.0f);
	RGB ambientColor = new RGB(0.01f, 0.01f, 0.01f);

	RaytraceTask(SDRaytracer tracer, int i) {
		this.tracer = tracer;
		this.i = i;
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
				Ray eyeRay = new Ray();
				eyeRay.setStart(tracer.startX, tracer.startY, tracer.startZ); // ro
				eyeRay.setDir((float) (((0.5 + di) * tracer.tanFovx * 2.0) / tracer.width - tracer.tanFovx),
						(float) (((0.5 + dj) * tracer.tanFovy * 2.0) / tracer.height - tracer.tanFovy), (float) 1f); // rd
				eyeRay.normalize();
				col[j] = addColors(tracer.image[i][j], rayTrace(eyeRay, 0), 1.0f / tracer.rayPerPixel);
			}
		}
		return col;
	}


	
	RGB rayTrace(Ray ray, int rec) {
		if (rec > tracer.maxRec)
			return black;
		IPoint ip = ray.hitObject(tracer.triangles); // (ray, p, n, triangle);
		if (ip.dist > IPoint.EPSILON)
			return lighting(ray, ip, rec);
		else
			return black;
	}

	RGB lighting(Ray ray, IPoint ip, int rec) {
		Vec3D point = ip.ipointVector;
		Triangle triangle = ip.triangle;
		RGB color = addColors(triangle.color, ambientColor, 1);
		Ray shadowRay = new Ray();
		for (Light light : tracer.getLights()) {
			shadowRay.start = point;
			shadowRay.dir = light.position.minus(point).mult(-1);
			shadowRay.dir.normalize();
			IPoint ip2 = shadowRay.hitObject(tracer.triangles);
			if (ip2.dist < IPoint.EPSILON) {
				float ratio = Math.max(0, shadowRay.dir.dot(triangle.normal));
				color = addColors(color, light.color, ratio);
			}
		}
		Ray reflection = new Ray();
		// R = 2N(N*L)-L) L ausgehender Vektor
		Vec3D lVector = ray.dir.mult(-1);
		reflection.start = point;
		reflection.dir = triangle.normal.mult(2 * triangle.normal.dot(lVector)).minus(lVector);
		reflection.dir.normalize();
		RGB rcolor = rayTrace(reflection, rec + 1);
		float ratio = (float) Math.pow(Math.max(0, reflection.dir.dot(lVector)), triangle.shininess);
		color = addColors(color, rcolor, ratio);
		return (color);
	}
	
	RGB addColors(RGB c1, RGB c2, float ratio) {
		return c1.addColor(c2, ratio);
	}

}
