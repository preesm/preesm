package socket;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Reading implements Runnable{
	private ServerSocket server;
	private Socket socket;
	private int port;
	private int size;
	private int read;
	private int nbProc;
	private int available;
	private byte[] buffer;
	private Window fen;
	private Pannel pan;
	private String title;
	private int HEIGHT, WIDTH;
	private BufferedImage picture;
	private FileOutputStream result;
	

	public Reading(ServerSocket server, Socket socket, int port, int width, int height, byte[] buffer){
		this.server = server;
		this.socket = socket;
		this.port = port;
		this.buffer = buffer;
		this.read = 0;
		this.available = 0;
		this.nbProc = 0;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.size = this.HEIGHT*this.WIDTH*3;
		this.title = null;
		this.result = null;
		this.fen = null;
		this.pan = null;
		this.picture = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	public void run(){
		fen = new Window();
        pan = new Pannel();
        
		try{
			for(;;){
				server = new ServerSocket(port, 5);
		        System.out.println("\nServer is listening on port "+server.getLocalPort());
		        socket = server.accept();
		        System.out.println("A client is connected on port "+server.getLocalPort());
		        DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		        //DataInputStream dis1 = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

		        /* Communication on port PORT_OUT */
		        
		        //nbProc = dis.read(); dis.read(); dis.read(); dis.read();
		        title ="Result with 7 Cores";
		        		        		        
		        	/* Receiving processed image */
		        while(read < size){
		        	available = dis.available();
		        	if(available != 0){
		            	dis.read(buffer, read, available);		//reading faster than sending so read block by block
		            	read += available;
		        	}
		            System.out.println("received "+read+"/"+size +"\r");
		        }
		        System.out.println("\nImage received");
		        
		        /* Socket2 closing */
		        socket.close();
		        server.close();
		        System.out.println("Communication on port "+server.getLocalPort()+" is closed");
		        
		        result = new FileOutputStream(new File("uncoded.data"));
				/* Display processed image */
		        
		    	fen.setAttributes(title, WIDTH, HEIGHT);
		    	pan.setAttributes(picture, buffer, WIDTH, HEIGHT);
		    	fen.init();
		    	fen.setContentPane(pan);
		    	fen.setVisible(true);		         
		    	
		        /* Store processed image in a file */
		        result.write(buffer, 0, WIDTH*HEIGHT*3);
			}
		}catch(IOException e) {
    		e.printStackTrace();
    	}
	}
}
