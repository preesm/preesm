/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
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

