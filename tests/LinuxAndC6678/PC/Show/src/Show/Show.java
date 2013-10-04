package Show;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Show.Fenetre;
import Show.Panneau;
import Show.Filtre;

public class Show implements Runnable{
	public static FileInputStream image;
	public static File file;
	public static FileOutputStream result;	
	
	public Show(){}		
	
	public void run(){
		try{
			/* Variable declaration */
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			int status ;
			int WIDTH;
			int HEIGHT;
//			final String str1, str2;
			Filtre filtre_raw;
//			final JTextField textZone1 = new JTextField(5);
//			JLabel width = new JLabel("Enter image Width");
//			JLabel height = new JLabel("Enter image Height");
//			final JTextField textZone2 = new JTextField(5);
//			
//			textZone1.addActionListener(new ActionListener() {			
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					str1 = textZone1.getText();
////					System.out.println(textZone1.getText());
//				}
//			});
//			textZone2.addActionListener(new ActionListener() {			
//				@Override
//				public void actionPerformed(ActionEvent arg1) {
//					str2 = textZone2.getText();
////					System.out.println(textZone2.getText());
//				}
//			});

			/* Open file */
			filtre_raw = new Filtre(new String[] {"data"}, "raw image");
			status = fc.showOpenDialog(null);
			if(status == JFileChooser.APPROVE_OPTION){
				file = fc.getSelectedFile();
				System.out.println("File opened");
			}else{
				System.out.println("File cannot be opened");
			}			
			if(filtre_raw.accept(file)){			
				/* Needs for displaying */
				@SuppressWarnings("resource")
				Scanner sc = new Scanner(System.in);
				System.out.println("Enter image Width: ");
				WIDTH = sc.nextInt();
				System.out.println("Enter image Height: ");
				HEIGHT = sc.nextInt();
				
//				Fenetre fen1 = new Fenetre();
//				Panneau pan1 = new Panneau();
//				pan1.add(width);
//				pan1.add(textZone1);
//				pan1.add(height);
//				pan1.add(textZone2);
//				fen1.setSize(300,150);
//				fen1.setTitle("Image dimension");
//		        fen1.setLocationRelativeTo(null); 
//				fen1.setContentPane(pan1);
//				fen1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				fen1.setVisible(true);	

				image = new FileInputStream(file);
				byte[] buffer = new byte[(int)file.length()];

				BufferedImage picture1 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

				/* Display image */
				while (image.read(buffer)>=0){}
				Fenetre fen = new Fenetre();
				Panneau pan = new Panneau();
				fen.getValue("Image", WIDTH, HEIGHT);
				pan.getValue(picture1, buffer, WIDTH, HEIGHT);
				fen.init();
				fen.setContentPane(pan);
				fen.setVisible(true);
			}else{						
				BufferedImage picture = ImageIO.read(file);
				WIDTH = picture.getWidth();
				HEIGHT = picture.getHeight();

				/* Display image */
				Fenetre fen = new Fenetre();
				PanneauGeneric pan = new PanneauGeneric();
				fen.getValue("Image", WIDTH, HEIGHT);
				pan.getValue(picture);
				fen.init();
				fen.setContentPane(pan);
				fen.setVisible(true);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{			
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