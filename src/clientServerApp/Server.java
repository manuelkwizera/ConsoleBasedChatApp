package clientServerApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//The java.net.Socket class represents the socket that both the client and the server use to communicate with each other.    

public class Server extends Thread {
	private final int serverPort;
	private ArrayList<ServerWorker> workerList = new ArrayList<ServerWorker>(); // will hold a collection of connected clients

	
	public Server(int serverPort) {
		this.serverPort = serverPort;
		
	}
		
	
	//allows connected clients to access other clients connected on the server
	public List<ServerWorker> getWorkerList(){
		return workerList;
	}
	
	//run method will accept client Server Connections
	@Override
	public void run() {
		try {
			// the server socket object is used to establish communication with the clients.
			ServerSocket serverSocket = new ServerSocket(serverPort); // server instantiates a ServerSocket object
			while(true) { // infinite while loop which countinously accepts incoming client connections
				System.out.println("About to accept the client connection... ");
				Socket clientSocket = serverSocket.accept(); // The server invokes the accept() method of the ServerSocket class
				System.out.println("Accepted connection from " + clientSocket); //The accept method waits until a client connects to the server
				//ServerWorker worker = new ServerWorker(this, clientSocket); // server worker class which is a thread handles communications with the client
				ServerWorker worker = new ServerWorker(this, clientSocket);
				workerList.add(worker); //add the new connected client to the list 
				worker.start();		
			}
		}    
		catch(IOException e) { 
			e.printStackTrace();
		}	
	}

	public void removeWorker(ServerWorker serverWorker) {
		workerList.remove(serverWorker);
		
	}
	
}
