package socket;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Writing implements Runnable{
	private ServerSocket server;
	private Socket socket;
	private int port;
	private int size;
	private byte[] buffer;
	private FileInputStream image;
	
	public Writing(ServerSocket server, Socket socket, int port, int size, byte[] buffer){
		this.server = server;
		this.socket = socket;
		this.port = port;
		this.size = size;
		this.buffer = buffer;
		this.image = null;
	}
	
	public void run()
	{
		try{
			for(;;){
	        	/* Opening files */
	            image = new FileInputStream(new File("coded.data"));
	            
	            /* Reading file */
	            while (image.read(buffer)>=0){
	            }
				/* Socket1 Configuration */
		    	server = new ServerSocket(port, 5);
		        System.out.println("\nServer is listening on port "+server.getLocalPort());
		        socket = server.accept();
		        System.out.println("A client is connected on port "+server.getLocalPort());
		        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	        
		        /* Communication on port port */
		                    
		        	/* Sending image size */
		        dos.writeInt(size);
		        dos.flush();
		        System.out.println("Image size sent");
		                                
		        	/* Sending image */
		        dos.write(buffer,0,size);
		        dos.flush();
		        System.out.println("Image sent");
		        
		        /* Socket1 closing */
		        socket.close();
		        server.close();
		        System.out.println("Communication on port "+server.getLocalPort()+" is closed");
			}
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
			}
	}
}
