package socket;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
  
@SuppressWarnings("serial")

public class Pannel extends JPanel{
	BufferedImage pictureOut;
	byte[] buffer;
	int Width;
	int Height;
	
	public void setAttributes(BufferedImage picture, byte[] buff, int w, int h){
		pictureOut=picture;
		buffer=buff;
		Width=w;
		Height=h;
	}
	public void paintComponent(Graphics g){

		super.paintComponent(g);
		int i,j;

		Graphics2D g2d = (Graphics2D)g;

		for(i=0;i<Height;i++){
			for(j=0;j<Width;j++){
				Color c = new Color(buffer[3*(i*Width+j)]&0xFF, buffer[1+3*(i*Width+j)]&0xFF, buffer[2+3*(i*Width+j)]&0xFF);
				pictureOut.setRGB(j, i, c.getRGB());
				}
			}
	  g2d.drawImage(pictureOut, 0, 0, this.getWidth(), this.getHeight(), this);
  }
}              

