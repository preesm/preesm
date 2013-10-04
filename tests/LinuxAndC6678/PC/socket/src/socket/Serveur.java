package socket;
import java.net.ServerSocket;
import java.net.Socket;
public class Serveur {
	public static final int WIDTH = 3000;
	public static final int HEIGHT = 1500;
    public static final int PORT_IN = 2013;
    public static final int PORT_OUT = 567;
    public static boolean ready = false;
    public static byte[] buffer = new byte[WIDTH*HEIGHT*3];		//Buffer to store data of input file
    public static byte[] buffer1 = new byte[WIDTH*HEIGHT*3];	//Buffer to store data of output file
     
    public static void main(String[] zero) throws InterruptedException {
    	
    	/* Variables initialization */	
    	ServerSocket socketServer1 = null;
        Socket socketOfServer1 = null;
    	ServerSocket socketServer2 = null;
        Socket socketOfServer2 = null;
        
        Writing w = new Writing(socketServer1,socketOfServer1,PORT_IN,WIDTH*HEIGHT*3,buffer);
        Thread t1 = new Thread(w);        
        t1.start();
        
        Reading r = new Reading(socketServer2,socketOfServer2,PORT_OUT,WIDTH,HEIGHT,buffer1);
        Thread t2 = new Thread(r);
        t2.start();
        
    }
}
 

