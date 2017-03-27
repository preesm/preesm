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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Writing implements Runnable {
	private ServerSocket server;
	private Socket socket;
	private int port;
	private int frameSize;
	private byte[] buffer;
	private FileInputStream image;
	private int nbFrames;
	private Reading r;

	public Writing(ServerSocket server, Socket socket, int port, int size,
			int nbFrames, byte[] buffer, Reading r) {
		this.server = server;
		this.socket = socket;
		this.port = port;
		this.frameSize = size;
		this.buffer = buffer;
		this.image = null;
		this.nbFrames = nbFrames;
		this.r = r;
	}

	public void run() {
		int currentFrame = 0;
		for (;;) {
			try {
				/* Socket1 Configuration */
				server = new ServerSocket(port, 5);
				System.out.println("Server is listening on port "
						+ server.getLocalPort());
				socket = server.accept();
				System.out.println("A client is connected on port "
						+ server.getLocalPort());
				DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(socket.getOutputStream()));

				for (;;) {
					/* Opening files */
					image = new FileInputStream(new File("akiyo_cif.yuv"));

					/* Reading file */
					while (image.read(buffer) >= 0) {
					}

					/* Communication on port port */

					/* Sending image size */
					dos.writeInt(frameSize);
					dos.flush();
					// System.out.println("Image size sent");

					/* Sending image */
					dos.write(buffer, currentFrame * frameSize, frameSize);
					dos.flush();
					System.out.println("Image sent");

					currentFrame = (currentFrame + 1) % nbFrames;
				}
				/* Socket1 closing Commented because code is never reached */
				// socket.close();
				// server.close();
				// System.out.println("Communication on port "+server.getLocalPort()+" is closed");
			} catch (SocketException e) {
				/* Socket1 closing Commented because code is never reached */
				try {
					r.resetConnection();
					socket.close();
					server.close();
					System.out.println("Communication on port "
							+ server.getLocalPort() + " is closed");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				try {
					if (image != null)
						image.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
