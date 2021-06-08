package clientServerApp;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer extends Thread {
	
//private ServerSocket ss;
	 private final Socket clientSocket;
	 public int port;
	
	public FileServer(Socket clientSocket, int port) {
		this.clientSocket = clientSocket;
		this.port = port;
	}
	
	public void run() {
		while (true) {
			try {
				//Socket clientSock = ss.accept();
				saveFile(clientSocket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveFile(Socket clientSocket) throws IOException {
		DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
		FileOutputStream fos = new FileOutputStream("E:\\testfile.jpg");
		byte[] buffer = new byte[4096];
		
		int filesize = 15123; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
		
		fos.close();
		dis.close();
	}
	
	//main method
	/*
	public static void main(String[] args) {
		FileServer fs = new FileServer();
		fs.start();
	}
	*/

}
