package socket;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
	public static final int WIDTH = 352;
	public static final int HEIGHT = 288;
	public static final int NB_FRAMES = 300;
    public static final int PORT_IN = 2013;
    public static final int PORT_OUT = 567;
    public static final int FRAME_SIZE = (int) (WIDTH*HEIGHT*1.5);
    public static final int FILE_SIZE = (int) (FRAME_SIZE*NB_FRAMES);
    public static boolean ready = false;
    public static byte[] buffer = new byte[FILE_SIZE];		//Buffer to store data of input file
    public static byte[] buffer1 = new byte[FILE_SIZE];	//Buffer to store data of output file
     
    public static void main(String[] zero) throws InterruptedException {
    	
    	/* Variables initialization */	
    	ServerSocket socketServer1 = null;
        Socket socketOfServer1 = null;
    	ServerSocket socketServer2 = null;
        Socket socketOfServer2 = null;
        
        Reading r = new Reading(socketServer2,socketOfServer2,PORT_OUT,WIDTH,HEIGHT,buffer1);
        Thread t2 = new Thread(r);
        t2.start();
        
        Writing w = new Writing(socketServer1,socketOfServer1,PORT_IN,FRAME_SIZE,NB_FRAMES, buffer, r);
        Thread t1 = new Thread(w);        
        t1.start();
        
    
        
    }
}
 

