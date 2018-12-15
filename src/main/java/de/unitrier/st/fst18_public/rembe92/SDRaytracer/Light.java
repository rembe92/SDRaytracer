package de.unitrier.st.fst18_public.rembe92.SDRaytracer;
//Licht mit Farbe und Position
class Light {
	 RGB color;
	 Vec3D position;
	 Light(Vec3D pos, RGB c) { position=pos; color=c; }
	}