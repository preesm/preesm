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
	public void paintComponent(Graphics graph){

		super.paintComponent(graph);
		int i,j;
		int uStartOff = Height*Width;
		int vStartOff = (int) (Height*Width*1.25);

		Graphics2D g2d = (Graphics2D)graph;

		for(i=0;i<Height;i++){
			for(j=0;j<Width;j++){
				int r,g,b;
				int y,u,v;
				int c,d,e;
				y = buffer[i*Width+j]&0xFF;
				u = buffer[uStartOff+(i/2)*Width/2+(j/2)]&0xFF;
				v = buffer[vStartOff+(i/2)*Width/2+(j/2)]&0xFF;
				
				u = u-128;
				v = v-128;
				
				r=y+(int)(1.402*u);
				r=(r>255)?255:((r<0)?0:r);
				
				g=y-(int)(1.344*v+0.714*u);;
				g=(g>255)?255:((g<0)?0:g);
				
				b=y+(int)(1.772*v);;
				b=(b>255)?255:((b<0)?0:b);
				
				Color col = new Color(b, g, r);
				pictureOut.setRGB(j, i, col.getRGB());
				}
			}
	  g2d.drawImage(pictureOut, 0, 0, this.getWidth(), this.getHeight(), this);
  }
}              

