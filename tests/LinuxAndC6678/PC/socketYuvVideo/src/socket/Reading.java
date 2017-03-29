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

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Reading implements Runnable {
	private ServerSocket server;
	private Socket socket;
	private int port;
	private int frameSize;
	private int read;
	private int available;
	private byte[] buffer;
	private Window fen;
	private Pannel pan;
	private String title;
	private int HEIGHT, WIDTH;
	private BufferedImage picture;
	private FileOutputStream result;
	private boolean resetConnection = false;

	public Reading(ServerSocket server, Socket socket, int port, int width,
			int height, byte[] buffer) {
		this.server = server;
		this.socket = socket;
		this.port = port;
		this.buffer = buffer;
		this.read = 0;
		this.available = 0;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.frameSize = (int) (this.HEIGHT * this.WIDTH * 1.5);
		this.title = null;
		this.result = null;
		this.fen = null;
		this.pan = null;
		this.picture = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
	}

	public void resetConnection() {
		resetConnection = true;
	}

	class ResetException extends Exception {

		public ResetException(String string) {
			super(string);
		}
	}

	public void run() {
		fen = new Window();
		pan = new Pannel();

		for (;;) {
			try {
				server = new ServerSocket(port, 5);
				System.out.println("Server is listening on port "
						+ server.getLocalPort());
				socket = server.accept();
				System.out.println("A client is connected on port "
						+ server.getLocalPort());
				DataInputStream dis = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));

				for (;;) {
					read = 0;

					// DataInputStream dis1 = new DataInputStream(new
					// BufferedInputStream(socket.getInputStream()));

					/* Communication on port PORT_OUT */

					// nbProc = dis.read(); dis.read(); dis.read(); dis.read();
					title = "Result with 7 Cores";

					/* Receiving processed image */
					while (read < frameSize) {
						available = dis.available();
						if (available != 0) {
							dis.read(buffer, read, available); // reading faster
																// than sending
																// so
																// read block by
																// block
							read += available;
						}
						if (resetConnection) {
							resetConnection = false;
							throw new ResetException("Reset Connection");
						}
						// System.out.println("received "+read+"/"+frameSize
						// +"\r");
					}
					System.out.println("Image received");

					result = new FileOutputStream(
							new File("uncoded_frame.data"));
					/* Display processed image */

					fen.setAttributes(title, WIDTH, HEIGHT);
					pan.setAttributes(picture, buffer, WIDTH, HEIGHT);
					fen.init();
					fen.setContentPane(pan);
					fen.setVisible(true);

					/* Store processed image in a file */
					result.write(buffer, 0, (int) (WIDTH * HEIGHT * 1.5));
				}

				/* Socket2 closing Commented because code is never reached */
				// socket.close();
				// server.close();
				// System.out.println("Communication on port "+server.getLocalPort()+" is closed");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ResetException e) {
				/* Socket2 closing */
				try {
					socket.close();
					server.close();
					System.out.println("Communication on port "
							+ server.getLocalPort() + " is closed");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}
}
