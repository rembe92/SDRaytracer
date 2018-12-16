package de.unitrier.st.fst18_public.rembe92.SDRaytracer;

class IPoint {
	static final float EPSILON = 0.0001f;
	Triangle triangle;
	Vec3D ipointVector;
	float dist;

	IPoint(Triangle tt, Vec3D ip, float d) {
		triangle = tt;
		ipointVector = ip;
		dist = d;
	}

}
