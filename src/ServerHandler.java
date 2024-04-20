import java.io.*;
import java.net.*;

public class ServerHandler implements Runnable
{
	Server server; // obj of Server
    Socket socket; // obj of Socket
    DataInputStream dataIn; //obj of DataInputStream 
    DataOutputStream dataOut; //obj of DataOutputStream
    boolean isRun = true; //To check if the connection is running
    int direction,speed,playerID; //To update the direction of kart images


    //A default constructor that invoke the value of socket, server, and playerID from Server class
    public ServerHandler(Socket socket, Server server, int playerID)
    {
        this.socket = socket;
        this.server = server;
        this.playerID = playerID;
    }

    //Method that send data to a client by using the writeInt()
    public void sendDataToClient(int clientID_, int direction_, int speed_)
    {
        try  
        {
            dataOut.writeInt(clientID_);
            dataOut.writeInt(direction_);
            dataOut.writeInt(speed_);

            dataOut.flush();
        } 
        catch (IOException ex) 
        {
            System.out.println("Error sending message to client " + playerID + ": " + ex.getMessage());
        	ex.printStackTrace(); 
        }
    }

    //Method that send data to both client
    public void sendDataToMultiple(int clientID_, int direction_, int speed_) throws InterruptedException
    {
        for (int client = 0; client < server.connections.size(); client++) 
        {
            ServerHandler sc = server.connections.get(client);
            sc.sendDataToClient(clientID_, direction_, speed_);
        }
    }

    public void run() 
    {
        try  
        {
        	// dataIn obj used to read primitive Java datatypes from an underlying input stream
            dataIn = new DataInputStream(socket.getInputStream());
            // dataOut obj used to write primitive Java datatypes to an output stream
            dataOut = new DataOutputStream(socket.getOutputStream());
            
            // send player ID
            dataOut.writeInt(playerID);
            dataOut.flush(); //Flush the stream

            //receiving car data from client side by using readInt()
            while (isRun)  
            {
                playerID = dataIn.readInt();                
                direction = dataIn.readInt();
                speed = dataIn.readInt();

                sendDataToMultiple(playerID, direction, speed); //Send data to Both Client
            }   
        } catch (IOException ex) {
        	ex.printStackTrace(); 
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
