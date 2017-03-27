/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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
 

