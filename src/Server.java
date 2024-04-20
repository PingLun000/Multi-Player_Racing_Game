import java.io.*;
import java.net.*;
import java.util.*;

public class Server 
{
	boolean isRun = true; //To check if server running is true
    private ServerSocket server; //obj of ServerSocket
    //declare the ServeConnection class as new connections of ArrayList class
    ArrayList<ServerHandler> connections = new ArrayList<ServerHandler>();
    private int client = 0;  //Number of Client

    public static void main(String[] args) throws InterruptedException 
    {
        new Server(); // New Server connection
    }

    public Server() throws InterruptedException {
        System.out.println("=====Server is Ready to Accept Client=====");
        try 
        {
            server = new ServerSocket(5000); //Server socket bind to port 5000
            while (client < 2)   // a while loop while the client is less than 2
            {
                Socket s = server.accept(); // to accept a connection request from a client.
                client++;  // Number of client increment by 1
                // Print the messages on console
                System.out.println("Client" + client + "{Player " + client + "}" + " has connected to the game");

                //declare an obj for ServerHandler class and pass the socket and server and number of client to
                //ServerHandler default constructor
                ServerHandler sc = new ServerHandler(s, this, client);
                connections.add(sc); // Use the ArrayList of ServerHandler class to add this ServerHandler object
                
                Thread thread = new Thread(sc); //Declare a thread and initialize the thread to ServerHandler's obj
                thread.start(); //Start the thread obj
            }  
            // Once number of client is equal or more than 2, print a message to inform the user that the client slot is full
            System.out.println("Server will not accept player anymore");     
        } // End Server default Constructor
        catch (IOException ex) 
        {
        	ex.printStackTrace(); 
        }
    }
}
