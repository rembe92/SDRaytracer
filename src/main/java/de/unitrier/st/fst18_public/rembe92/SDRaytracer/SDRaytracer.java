package de.unitrier.st.fst18_public.rembe92.SDRaytracer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;




/* Implementation of a very simple Raytracer
   Stephan Diehl, Universitaet Trier, 2010-2016
*/

public class SDRaytracer extends JFrame {
	
	private static Logger log = Logger.getLogger(SDRaytracer.class);
	
	private static final long serialVersionUID = 1L;
	boolean profiling = false;
	int width = 1000;
	int height = 1000;

	Future[] futureList = new Future[width];
	int nrOfProcessors = Runtime.getRuntime().availableProcessors();
	ExecutorService eservice = Executors.newFixedThreadPool(nrOfProcessors);

	int maxRec = 3;
	int rayPerPixel = 1;
	int startX;
	int startY; 
	int startZ;
	
	double tanFovx;
	double tanFovy;

	List<Triangle> triangles;
	
	Light mainLight = new Light(new Vec3D(0, 100, 0), new RGB(0.1f, 0.1f, 0.1f));

	Light[] lights = new Light[] { mainLight, new Light(new Vec3D(100, 200, 300), new RGB(0.5f, 0, 0.0f)),
			new Light(new Vec3D(-100, 200, 300), new RGB(0.0f, 0, 0.5f))
			// ,new Light(new Vec3D(-100,0,0), new RGB(0.0f,0.8f,0.0f))
	};
	
	public Light[] getLights (){
		return lights;
	}

	RGB[][] image = new RGB[width][height];

	float fovx = (float) 0.628;
	float fovy = (float) 0.628;
	RGB backgroundColor = new RGB(0.05f, 0.05f, 0.05f);
	int yAngleFactor = 4;
	int xAngleFactor = -4;

	void profileRenderImage() {
		long end;
		long start;
		long time;

		renderImage(); // initialisiere Datenstrukturen, erster Lauf verf�lscht sonst Messungen

		for (int procs = 1; procs < 6; procs++) {
			StringBuilder out = new StringBuilder();
			maxRec = procs - 1;
			out.append(procs);
			for (int i = 0; i < 10; i++) {
				start = System.currentTimeMillis();

				renderImage();

				end = System.currentTimeMillis();
				time = end - start;
				out.append(";" + time);
			}
			log.debug(out.toString());
		}
	}

	SDRaytracer() {
		createScene();

		if (!profiling)
			renderImage();
		else
			profileRenderImage();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		JPanel area = new JPanel() {
			@Override
			public void paint(Graphics g) {
				log.debug("fovx=" + fovx + ", fovy=" + fovy + ", xangle=" + xAngleFactor + ", yangle="
						+ yAngleFactor);
				if (image == null)
					return;
				for (int i = 0; i < width; i++)
					for (int j = 0; j < height; j++) {
						g.setColor(image[i][j].getColor());
						// zeichne einzelnen Pixel
						g.drawLine(i, height - j, i, height - j);
					}
			}
		};
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				boolean redraw = false;
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					xAngleFactor--;
					// mainLight.position.y-=10;
					// fovx=fovx+0.1f;
					// fovy=fovx;
					// maxRec--; if (maxRec<0) maxRec=0;
					redraw = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					xAngleFactor++;
					// mainLight.position.y+=10;
					// fovx=fovx-0.1f;
					// fovy=fovx;
					// maxRec++;if (maxRec>10) return;
					redraw = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					yAngleFactor--;
					// mainLight.position.x-=10;
					// startX-=10;
					// fovx=fovx+0.1f;
					// fovy=fovx;
					redraw = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					yAngleFactor++;
					// mainLight.position.x+=10;
					// startX+=10;
					// fovx=fovx-0.1f;
					// fovy=fovx;
					redraw = true;
				}
				if (redraw) {
					createScene();
					renderImage();
					repaint();
				}
			}
		});

		area.setPreferredSize(new Dimension(width, height));
		contentPane.add(area);
		this.pack();
		this.setVisible(true);
	}

	void renderImage() {
		tanFovx = Math.tan(fovx);
		tanFovy = Math.tan(fovy);
		for (int i = 0; i < width; i++) {
			futureList[i] = eservice.submit(new RaytraceTask(this, i));
		}

		for (int i = 0; i < width; i++) {
			try {
				RGB[] col = (RGB[]) futureList[i].get();
				for (int j = 0; j < height; j++)
					image[i][j] = col[j];
			} catch (InterruptedException e) {
				log.warn("Interrupted! ", e);
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				log.warn("Interrupted! ", e);
				Thread.currentThread().interrupt();
			}
		}
	}

	void createScene() {
		triangles = new ArrayList<Triangle>();

		Cube.addCube(triangles, 0, 35, 0, 10, 10, 10, new RGB(0.3f, 0, 0), 0.4f); // rot, klein
		Cube.addCube(triangles, -70, -20, -20, 20, 100, 100, new RGB(0f, 0, 0.3f), .4f);
		Cube.addCube(triangles, -30, 30, 40, 20, 20, 20, new RGB(0, 0.4f, 0), 0.2f); // gr�n, klein
		Cube.addCube(triangles, 50, -20, -40, 10, 80, 100, new RGB(.5f, .5f, .5f), 0.2f);
		Cube.addCube(triangles, -70, -26, -40, 130, 3, 40, new RGB(.5f, .5f, .5f), 0.2f);

		Matrix mRx = Matrix.createXRotation((float) (xAngleFactor * Math.PI / 16));
		Matrix mRy = Matrix.createYRotation((float) (yAngleFactor * Math.PI / 16));
		Matrix mT = Matrix.createTranslation(0, 0, 200);
		Matrix m = mT.mult(mRx).mult(mRy);
		m.print();
		m.apply(triangles);
	}

}
