package de.unitrier.st.fst18_public.rembe92.SDRaytracer;
//Vektor in der 3 Dimension
class Vec3D {
	  float x;
	  float y;
	  float z;
	  float w=1;
	  Vec3D(float xx, float yy, float zz) { x=xx; y=yy; z=zz; }
	  Vec3D(float xx, float yy, float zz, float ww) { x=xx; y=yy; z=zz; w=ww; }
	  Vec3D add(Vec3D v)
	    { return new Vec3D(x+v.x, y+v.y, z+v.z); }
	  Vec3D minus(Vec3D v)
	    { return new Vec3D(x-v.x, y-v.y, z-v.z); }
	  Vec3D mult(float a)
	    { return new Vec3D(a*x, a*y, a*z); }

	  /**
	   * normalisiere den Vektor
	   */
	  void normalize()
	    {  float dist = (float) Math.sqrt( (x * x)+(y * y)+(z * z) );
	       x = x / dist;
	       y = y / dist;
	       z = z / dist;
	   }
	   
	  float dot(Vec3D v) { return x*v.x+y*v.y+z*v.z; }
	  
	  Vec3D cross(Vec3D v) {
	    return new Vec3D( y*v.z-z*v.y, z*v.x-x*v.z, x*v.y-y*v.x);
	  }
	}
