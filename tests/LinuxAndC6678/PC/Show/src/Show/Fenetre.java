package Show;

import javax.swing.JFrame;

@SuppressWarnings("serial")

public class Fenetre extends JFrame{

	String TITLE = new String("");
	int WIDTH;
	int HEIGHT;
	
	public void getValue(String titre, int Width, int Height){
		TITLE = titre;
		WIDTH = Width;
		HEIGHT = Height;
	}
	
	public void init(){
    	this.setTitle(TITLE);
        this.setSize(WIDTH,HEIGHT);
        this.setLocationRelativeTo(null);              
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
	}
}
