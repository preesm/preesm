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
