package clientServerApp;

import java.io.IOException;

public class ServerMain {
	public static void main(String[] args) throws IOException {
		int port = 8818; //The port number of which communication is to occur on.
		//String host = "localhost";
 		Server server = new Server(port); // instance of a server class
		server.start(); // This will invoke the run method
	}

}
