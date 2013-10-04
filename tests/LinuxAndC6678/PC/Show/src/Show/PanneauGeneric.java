package Show;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
  
@SuppressWarnings("serial")

public class PanneauGeneric extends JPanel{
	private BufferedImage pictureOut;
	
	public void getValue(BufferedImage picture){
		pictureOut = picture;
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(pictureOut, 0, 0, this.getWidth(), this.getHeight(), this);
  }
}              
