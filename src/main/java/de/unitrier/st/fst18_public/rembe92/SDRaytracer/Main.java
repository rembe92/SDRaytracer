package de.unitrier.st.fst18_public.rembe92.SDRaytracer;

public class Main {

	public static void main(String argv[]) {
		long start = System.currentTimeMillis();
		SDRaytracer sdr = new SDRaytracer();
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("time: " + time + " ms");
		System.out.println("nrprocs=" + sdr.nrOfProcessors);
	}

}
