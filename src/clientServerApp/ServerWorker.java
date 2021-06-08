package clientServerApp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.lang3.StringUtils;


	 //serverWorker extends a thread class to continue listening to requests from multiple clients
	 public class ServerWorker extends Thread { 
	
		 
	 private final Socket clientSocket;
	 //public final Socket clientSocket;
	 private String login = null;
	 private final Server server;
	 private OutputStream outputStream;
	 
	 //new
	 public int port = 8818;
	  
	 //passing each server instance to each server worker
	 public ServerWorker(Server server, Socket clientSocket) {
		 this.server = server;
		 this.clientSocket = clientSocket; //client instantiates a Socket object
	 }
	 
   //run method is responsible for for executing the sequence of code that a thread will execute	 
	 @Override
	 public void run() {
		try {
			handleClientSocket();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		
	} 
	
	//hnadleClientSocket method is used to create a bi-direction communication between client and server by using streams
	private void handleClientSocket() throws IOException, InterruptedException{
		
		//Creating a bidirectional communication by using stream
		//communication will occur using I/O streams
		//The client's OutputStream is connected to the server's InputStream
		//client's InputStream is connected to the server's OutputStream.
		
		InputStream inputStream = clientSocket.getInputStream(); //Reading Input data from the client
		this.outputStream = clientSocket.getOutputStream(); //Writing Out put data from the client
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // read data line by line the 
		String line; 
		
		//1.Handle Login
		//2.Handle Logout
		//3.Handle Direct messaging
		
		while((line = reader.readLine()) != null) { //Client enters a message which will be stored in line variable
			
			//FileServer fileServer = new FileServer(server, clientSocket);
			//FileClient fileClient = new FileClient(server, clientSocket);
			
			//new
			FileServer fs = new FileServer(clientSocket, port);
			
			String[] tokens = StringUtils.split(line);//split the entered string into tokens based on white space characters
			if(tokens != null && tokens.length > 0) {			
				String cmd = tokens[0];
				//this block will handle the user login and logout 
				if(("logoff").equalsIgnoreCase(cmd) || ("quit".equalsIgnoreCase(cmd))) { // if user enters quit this terminate the system
					handleLogoff();
					break;
				}
				else if("msg".equalsIgnoreCase(cmd)) {
					String[] tokensMsg = StringUtils.split(line, null, 3); //it will split the string into three tokens
					handleMessage(tokensMsg);
				}
				
				else if("login".equalsIgnoreCase(cmd)) {
					  handleLogin(outputStream, tokens);
				}
				
				//file transfer
				else if("file".equalsIgnoreCase(cmd)) {
					String[] tokensFile = StringUtils.split(line, null, 3); //it will split the string into three tokens
					//Server.fileServer();
					//fileClient.start();
					fs.start();
					FileClient.fileMain();
				}
				//file transfer
				
				else {
					String msg = "unknown " + cmd + "\n";
					outputStream.write(msg.getBytes());
				}
			}
		}
	clientSocket.close();
  }
	
	
	//this method will handle a direct messaging between clients
	//format of the message: ["msg" + login + body of the message]
	private void handleMessage(String[] tokens) throws IOException {
			String sendTo = tokens[1]; //To whom the message should be sent to
			String body = tokens[2]; // the body of the message
			
			List<ServerWorker> workerList = server.getWorkerList(); //get a list of all connected clients
			for(ServerWorker worker : workerList) {
				//check whether the user to chat with is loged in in the system
				if(sendTo.equalsIgnoreCase(worker.getLogin())) {
					//login is the sender of the message
					String outMsg = "msg " + login + " " + body + "\n";
					worker.send(outMsg);
				}
			}
		}
	
	
	
		//This method will handle file sharing
		private void handleFile(String[] tokens) throws IOException {
			String sendTo = tokens[1]; //To whom the file should be sent to
			//F:\\client.txt
			String directory = tokens[2]; // the body of the message
			
			List<ServerWorker> workerList = server.getWorkerList(); //get a list of all connected clients
			for(ServerWorker worker : workerList) {
				//check whether the user to chat with is loged in in the system
				if(sendTo.equalsIgnoreCase(worker.getLogin())) {
					// Get the file
			        File file = new File(directory);
			        // Check if the specified file
			        // Exists or not
			        if(file.exists()){
			        	FileInputStream fileInput =  new FileInputStream(directory);
			    		byte b[] = new byte[500000]; //size of the file   
			    		fileInput.read(b, 0, b.length);
			    		// convert a file into a stream
			    		OutputStream outputStream = clientSocket.getOutputStream();
			    		//send the file to the client
			    		outputStream.write(b, 0, b.length);
			    		//ChatClient.clientMain();
		
			        }
			        else {
			        	System.out.println("The file does not exist");
			        }
				}
			}
		}
	
	
	
	//this method will terminate the system if the user enters log off or quit
	//it will send message to other connected users that the current user has logged out
	private void handleLogoff() throws IOException {
		server.removeWorker(this);
		List<ServerWorker> workerList = server.getWorkerList(); //get a list of all connected clients
		// send message to online user that the current user has loged out 
		String onlineMsg = "offline " + login + "\n";
		for(ServerWorker worker : workerList) {
			if(!login.equals(worker.getLogin())) {
				worker.send(onlineMsg); // send an online message to every connected client	
			}	
		}	
		clientSocket.close(); //the system will terminate by closing the client socket
	}

	public String getLogin() {
		return login;		
	}

   //This method will handle user login and password
	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		System.out.println(tokens);
		if(tokens.length == 3) {
			String login = tokens[1]; //login or user name will be the first token at index 1
			String password = tokens[2]; //password will be at index 2
			
			if((login.equals("kiki") && password.equals("kiki")) || (login.equals("mimi") && password.equals("mimi"))) {
				String msg = "Login Successful\n"; 
				outputStream.write(msg.getBytes());
				this.login = login;
				System.out.println("User Logged in Successfully: " + login);
			
				
				List<ServerWorker> workerList = server.getWorkerList(); //get a list of all connected clients
				
				
				//send message to current user about other logins status 
				for(ServerWorker worker : workerList) {
						if(worker.getLogin() != null) {
							if(!login.equals(worker.getLogin())) {
								String msg2 = "online " + worker.getLogin() + "\n";
								send(msg2); // send an online message to every connected client				
							}		
						}			
					}
				
				// send message to online user about the current user status
				String onlineMsg = "online " + login + "\n";
				for(ServerWorker worker : workerList) {
					if(!login.equals(worker.getLogin())) {
						worker.send(onlineMsg); // send an online message to every connected client	
					}	
				}	
			}
				else {
					String msg = "Error Login\n";
					outputStream.write(msg.getBytes());
					System.err.println("Login failed for "+login);
				}
			}	
		}
	
		//send method will send message to connected client 
		private void send(String msg) throws IOException {
			if(login != null) {
				outputStream.write(msg.getBytes());
			}		
		}
	}
 