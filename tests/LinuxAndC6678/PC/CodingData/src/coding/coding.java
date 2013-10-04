package coding;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import coding.Fenetre;
import coding.Panneau;

public class coding{
	
	public static int WIDTH = 3000;
	public static int HEIGHT = 1500;
	public static byte[] in = new byte[WIDTH*HEIGHT*3];	
	public static byte[] out = new byte[WIDTH*HEIGHT*3];
	public static FileInputStream image;
	public static FileOutputStream result;

	public static void main(String[] zero){
		try{
			int i;
			int size = WIDTH*HEIGHT*3;
			BufferedImage picture1 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			BufferedImage picture2 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

			
			image = new FileInputStream(new File("saturne.data"));
			result = new FileOutputStream(new File("coded_saturne.data"));

	/* Reading file */
			while (image.read(in)>=0){}
			Fenetre fen1 = new Fenetre();
	        Panneau pan1 = new Panneau();
	    	fen1.getValue("Init", WIDTH, HEIGHT);
	    	pan1.getValue(picture1, in, WIDTH, HEIGHT);
	    	fen1.init();
	    	fen1.setContentPane(pan1);
	    	fen1.setVisible(true);
			
	        for(i=0; i<size; i++){
	                out[i] = (byte) (in[i] + 2*i);
//	                out[i] = (byte) (in[i] + 127);
	        }
			
			result.write(out, 0, WIDTH*HEIGHT*3);
			
			Fenetre fen2 = new Fenetre();
	        Panneau pan2 = new Panneau();
	    	fen2.getValue("Result", WIDTH, HEIGHT);
	    	pan2.getValue(picture2, out, WIDTH, HEIGHT);
	    	fen2.init();
	    	fen2.setContentPane(pan2);
	    	fen2.setVisible(true);
						
			}catch(IOException e){
				e.printStackTrace();
				}finally
				{
					try {
						if (image!= null)
							image.close();
						}catch (IOException e){
							e.printStackTrace();
							}
					try {
						if (result!= null)
							result.close();
						}catch (IOException e){
							e.printStackTrace();
							}
					}
		}
	}

 

