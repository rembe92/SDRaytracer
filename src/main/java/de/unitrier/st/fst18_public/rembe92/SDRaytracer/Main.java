package de.unitrier.st.fst18_public.rembe92.SDRaytracer;

import org.apache.log4j.Logger;

public class Main {

	private static Logger log = Logger.getLogger(Main.class);
	
	public static void main(String argv[]) {
		long start = System.currentTimeMillis();
		SDRaytracer sdr = new SDRaytracer();
		long end = System.currentTimeMillis();
		long time = end - start;
		log.debug("time: " + time + " ms");
		log.debug("nrprocs=" + sdr.nrOfProcessors);
	}

}
