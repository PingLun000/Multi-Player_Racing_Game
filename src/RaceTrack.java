import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class RaceTrack extends JPanel implements KeyListener, ActionListener
{

	private final static String player1 = "Images/Player1.png"; //bg file path
	private final static String player2 = "Images/Player2.png"; //bg file path
	private final static String finishLine = "Images/finishLine.jpg"; //finish line file path
	private final static String KARTA_NAME = "Kart_A/kartA_"; // kart A file path
	private final static String KARTB_NAME = "Kart_B/kartB_"; // kart B file path

	BufferedImage p1 = ImageIO.read(this.getClass().getResource(player1));//read the bg file path as a bufferedImage data type
	BufferedImage p2 = ImageIO.read(this.getClass().getResource(player2));//read the bg file path as a bufferedImage data type
	BufferedImage finish_line = ImageIO.read(this.getClass().getResource(finishLine)); //read the finish line file path as a bufferedImage data type

	private KartActionPerformed kart1, kart2; // Create KartActionPerformed objects for both karts
	private Timer kartRotation, starttimer; // Timers for kart image animation and countdown timer
	private boolean start = false, End = false, restart = false, exit = false, player1_IsReady = false, player2_IsReady = false, playersAreReady = false, startRace = false, runOnce = false; // game flags
	private JLabel p1_title, p2_title, p1_speed, p2_speed, p1_position, p2_position, p1_label, p2_label, p1_status, p2_status, p1_isReady_lbl, p1_isReady, p2_isReady_lbl, p2_isReady, start_counter, lb; // labels for current kart information and countdown timer
	private int playerID, opponentID, d = 0, s = 0, second; // player and opponent IDs, opponent's kart direction and speed, and countdown timer variables
	private DecimalFormat dFormat = new DecimalFormat("00"); // decimal format for countdown timer
	private ClientSideConnection clientConnection; // ClientSideConnection object for network communication
	private String ddSecond; // To store the value of second in decimal format

	// RaceTrack default constructor
	public RaceTrack() throws UnsupportedAudioFileException, IOException, LineUnavailableException 
	{
		setLayout(null); //Set the JPanel layout to null
		
		// Passing Value to KartActionPerformed's default constructor through the object of kartActionPerformed-
		kart1 = new KartActionPerformed(KARTA_NAME, 370, 498); // (image file path, x position, y position)
		kart2 = new KartActionPerformed(KARTB_NAME, 370, 548); // (image file path, x position, y position)
				
		
		addKeyListener(this); //Add key listener to this (JPanel)RaceTrack class
		setFocusable(true); //lets the JPanel able to get focused when players pressing the key.
		
		// Timer for repaint the kart images and the animation delay of the timer is 100
		kartRotation = new Timer(100, this);
		kartRotation.start();
		
		// Timer for repaint the count down timer
		second = 3; //set the seconds of count down to 6
		countDownTimer(); //A method to start count down
		
		// Update kart current info
		DisplayMessage(); 
		
		// Connect to Server
		connectToServer();
		// To start getting data from Server
		startGetDataFromServer();
	}


	@Override
	public void keyTyped(KeyEvent e)
	{
		//keyTyped = Invoked when a key is typed. Uses KeyChar, char output
		try
		{System.out.println(e);}
		catch (UnsupportedOperationException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		try {System.out.println(e);}
		catch (UnsupportedOperationException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Draw background color to black
		g2d.setPaint(Color.WHITE);
		g2d.fillRect(0, 0, 870, 700);

		// Draw the road of race track
		g2d.setColor(Color.GRAY);
		g2d.fillRect(50, 100, 750, 500);

		// Draw center grass
		g2d.setPaint(Color.WHITE);
		g2d.fillRect(150, 200, 550, 300);

		// Draw mid-lane on race track
		g2d.setPaint(Color.BLACK);
		g2d.setStroke(new BasicStroke(4));
		g2d.drawRect(100, 150, 650, 400);

		// Draw left-outer and right-outer line for finish line
		g2d.setColor(Color.BLACK);
		g2d.drawLine(303, 502, 303, 597); // left-outer finish line
		g2d.drawLine(364, 502, 364, 597); // right-outer finish line

		// Draw finish line
		g2d.drawImage(finish_line, 305, 500, 57, 100, null);

		// Draw the inner lane for race track
		g2d.setPaint(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		g2d.drawRect(150, 200, 550, 300);

		// Draw the outer lane for race track
		g2d.drawRect(50, 100, 750, 500);

		// Draw background images
		g2d.drawImage(p1, 0, 0, 100, 100, null);
		g2d.drawImage(p2, 740, 0, 100, 100, null);

		// Draw kart 1 and kart 2
		g2d.drawImage(kart1.getCurrentKartImg(), kart1.getX(), kart1.getY(), null);
		g2d.drawImage(kart2.getCurrentKartImg(), kart2.getX(), kart2.getY(), null);

		// Send changed information from the client itself to the server
 	  	clientConnection.sendResponseToServer();
	}
	
    @Override
	public void actionPerformed(ActionEvent ae)
	{
		try
		{
			// Call kart control logic and collision detection for each kart
			kart1.kartControl();
			ifCollide(kart1);
			WinnerChecker(kart1);
			kart1.SoundEffects();

			kart2.kartControl();
			ifCollide(kart2);
			WinnerChecker(kart2);
			kart2.SoundEffects();

			// Update kart information and repaint
			GetCurrentInfo();
			repaint();
		}
		catch (Exception ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}


	//37 39 38 40 left right top down
	@Override
	public void keyPressed(KeyEvent e)
	{
		try
		{
			if(playersAreReady) //if both players are ready, then only can start
			{	//playerID set to 1 so it will recognize this set of 4 key only allow client 1 to use.
				if(!kart1.getCollide() && !kart1.getCheckStatus() && !kart2.getCheckStatus() && !kart1.getCheatAction() && startRace && playerID == 1)
				{
					switch (e.getKeyCode())
					{
					    case 65: //left key code {a}
						    kart1.setDirection(kart1.getDirection() - 1);
						    break;

					    case 68: //right key code {d}
						    kart1.setDirection(kart1.getDirection() + 1);
						    break;

					    case 87: //up key code {w}
						    kart1.setCurrentSpeed(kart1.getCurrentSpeed() + 1);
						    break;

					    case 83: //down key code {w}
						    kart1.setCurrentSpeed(kart1.getCurrentSpeed() - 1);
						    break;

					    default:
					    	break;
					}
				}
				//playerID set to 2 so it will recognize this set of 4 key only allow client 2 to use.
				if(!kart2.getCollide() && !kart1.getCheckStatus() && !kart2.getCheckStatus() && !kart2.getCheatAction() && startRace && playerID == 2)
				{
					//switch (e.getKeyCode()) {
//						case KeyEvent.VK_LEFT:
//							kart2.setDirection(kart2.getDirection() - 1);
//							break;
//
//						case KeyEvent.VK_RIGHT:
//							kart2.setDirection(kart2.getDirection() + 1);
//							break;
//
//						case KeyEvent.VK_UP:
//							kart2.setCurrentSpeed(kart2.getCurrentSpeed() + 1);
//							break;
//
//						case KeyEvent.VK_DOWN:
//							kart2.setCurrentSpeed(kart2.getCurrentSpeed() - 1);
//							break;
//
//						default:
//							break;

					switch (e.getKeyCode())
					{
					    case 37: //left key code {left arrow key}
						    kart2.setDirection(kart2.getDirection() - 1);
						    break;

					    case 39: //right key code {right arrow key}
						    kart2.setDirection(kart2.getDirection() + 1);
						    break;

					    case 38: //up key code {up arrow key}
						    kart2.setCurrentSpeed(kart2.getCurrentSpeed() + 1);
						    break;

					    case 40: //down key code {down arrow key}
						    kart2.setCurrentSpeed(kart2.getCurrentSpeed() - 1);
						    break;

					    default:
					    	break;
					}
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Please wait other player to get ready");
			}
		}
		catch (UnsupportedOperationException ex)
    	{
    		throw new UnsupportedOperationException(ex);
    	}
	}



	 public void ifCollide(KartActionPerformed kart)
	 {
		 // Create a Rectangle object to following the kart1 and kart2 current location
	     Rectangle kart1Field = new Rectangle(kart1.getX(), kart1.getY(), kart1.getWidth()-12, kart1.getHeight()-15);
	     Rectangle kart2Field = new Rectangle(kart2.getX(), kart2.getY(), kart2.getWidth()-12, kart2.getHeight()-15);

		 // Check if the kart has collided with the inner/outer lane of race track
		 if (kart.getX() >= 115 && kart.getX() <= 683) {
			 if (kart.getY() >= 165 && kart.getY() <= 483) {
				 kart.setCollide(true); //Update the kart collides is true
				 kart.setCurrentSpeed(0); // Update the current speed to 0
				 CheckKartStatus(); // Update the kart status
			 }
		 }

		 if ((kart.getX() <= 35 || kart.getX() >= 770)||(kart.getY() <= 82 || kart.getY() >= 566)
		 	||(kart1Field.intersects(kart2Field) || kart2Field.intersects(kart1Field))) {
			 kart.setCollide(true); //Update the kart collides is true
			 kart.setCurrentSpeed(0); // Update the current speed to 0
			 CheckKartStatus(); // Update the kart status
		 }
	 }


	 public void WinnerChecker(KartActionPerformed kart)
	 {
		 if (kart.getX() >= 300 && kart.getX() <= 320) 
	     { 
	    	 if (kart.getY() >= 491 && kart.getY() <= 558) 
	    	 {
	    		 if (kart.getDirection() >= 0 && kart.getDirection() <= 8 )
	    		 {
	    			 // Check if the player has reach the finish line without cheating
		    		 kart.CheckStatus(true);  //Update the kart wins is true
		    		 kart1.setCurrentSpeed(0); // Update the current speed to 0 for kart 1
		    		 kart2.setCurrentSpeed(0); // Update the current speed to 0 for kart 2
		    		 CheckKartStatus(); // Update the kart status
	    		 }
	    		 else
	    		 {	 // Check if the players reach the finish line by cheating
	    			 kart.checkCheatAction(true); //Update the kart cheat action is true
	    			 kart.setCurrentSpeed(0); // Update the current speed to 0
		    		 CheckKartStatus(); // Update the kart status
	    		 }
	         }
	     }
	 }
	  
	 // Method that check the current kart status
	 public void CheckKartStatus() {
		 // Check if both karts have collided or cheated
		 if ((kart1.getCollide() && kart2.getCollide()) || (kart1.getCheatAction() && kart2.getCheatAction())) {
			 JOptionPane.showMessageDialog(null, "No player wins the race");
			 runOnce = true;
			 System.exit(0);
		 } else if (!runOnce) { // Check if the race has not ended yet
			 // Check for kart 1's status
			 if (kart1.getCheckStatus() && !kart1.getCollide() && !kart1.getCheatAction()) {
				 p1_status.setText("Player1 win the race!");
				 p2_status.setText("Player2 lost the race.");
				 JOptionPane.showMessageDialog(null, "Player1 win the race");
				 runOnce = true;
				 System.exit(0);
			 } else if (kart1.getCollide()) {
				 p1_status.setText("Player1 lost the race.");
			 } else if (kart1.getCheatAction()) {
				 p1_status.setText("Player 1 cheated, so he loses.");
			 }

			 // Check for kart 2's status
			 if (kart2.getCheckStatus() && !kart2.getCollide() && !kart2.getCheatAction()) {
				 p2_status.setText("Player2 win the race!");
				 p1_status.setText("Player1 lost the race.");
				 JOptionPane.showMessageDialog(null, "Player2 win the race");
				 runOnce = true;
				 System.exit(0);
			 } else if (kart2.getCollide()) {
				 p2_status.setText("Player2 lost the race.");
			 } else if (kart2.getCheatAction()) {
				 p2_status.setText("Player 2 cheated, so he loses.");
			 }
		 }
		 repaint();
	 }


	public void checkReadyStatus() {
		if (player1_IsReady) {
			p1_isReady.setText("Player 1 is Ready");
		} else {
			p2_isReady.setText("Player 2 is not Ready");
		}

		if (player2_IsReady) {
			p2_isReady.setText("Player 2 is Ready");
		} else {
			p1_isReady.setText("Player 1 is not Ready");
		}

		if (playersAreReady) {
			p1_isReady.setText("Player 1 is Ready");
			p2_isReady.setText("Player 2 is Ready");
		}

		repaint();
	}

	// Method that update the current speed level and position for the kart 1 and kart 2
	public void GetCurrentInfo() {
		// For each kart, if it doesn't collide and doesn't win, get its current speed level and position
		if (!kart1.getCollide() && !kart1.getCheckStatus() && !kart2.getCheckStatus()) {
			p1_speed.setText("Speed Level: " + kart1.getCurrentSpeed());
			p1_position.setText("Current Position-> x: " + kart1.getX() + " y: " + kart1.getY());
		}
		if (!kart2.getCollide() && !kart2.getCheckStatus() && !kart1.getCheckStatus()) {
			p2_speed.setText("Speed Level: " + kart2.getCurrentSpeed());
			p2_position.setText("Current Position: x: " + kart2.getX() + " y: " + kart2.getY());
		}
	}


	public void DisplayMessage()
	 {
		 	p1_title = new JLabel("Player 1 -RED Kart");
			p1_title.setFont(new Font("Calibri", Font.BOLD, 16));
			p1_title.setForeground(Color.black);
			p1_title.setBounds(240, 0, 150, 30);
			add(p1_title);

			p2_title = new JLabel("Player 2-BLUE Kart");
			p2_title.setFont(new Font("Calibri", Font.BOLD, 16));
			p2_title.setForeground(Color.black);
			p2_title.setBounds(475, 0, 150, 30);
			add(p2_title);

			p1_speed = new JLabel();
			p1_speed.setFont(new Font("Calibri", Font.PLAIN, 14));
			p1_speed.setForeground(Color.black);
			p1_speed.setBounds(240, 23, 150,30);
			add(p1_speed);

			p2_speed = new JLabel();
			p2_speed.setFont(new Font("Calibri", Font.PLAIN, 14));
			p2_speed.setForeground(Color.black);
			p2_speed.setBounds(475, 23, 150,30);
			add(p2_speed);

			p1_position = new JLabel();
			p1_position.setFont(new Font("Calibri", Font.PLAIN, 14));
			p1_position.setForeground(Color.black);
			p1_position.setBounds(240, 47, 200,30);
			add(p1_position);

			p2_position = new JLabel();
			p2_position.setFont(new Font("Calibri", Font.PLAIN, 14));
			p2_position.setForeground(Color.black);
			p2_position.setBounds(475, 47, 200,30);
			add(p2_position);

			p1_label = new JLabel("Status: ");
			p1_label.setFont(new Font("Calibri", Font.PLAIN, 14));
			p1_label.setForeground(Color.black);
			p1_label.setBounds(240, 72, 200,30);
			add(p1_label);

			p2_label = new JLabel("Status: ");
			p2_label.setFont(new Font("Calibri", Font.PLAIN, 14));
			p2_label.setForeground(Color.black);
			p2_label.setBounds(475, 72, 200,30);
			add(p2_label);

			p1_status = new JLabel();
			p1_status.setFont(new Font("Calibri", Font.PLAIN, 14));
			p1_status.setForeground(Color.black);
			p1_status.setBounds(285, 72, 200,30);
			add(p1_status);

			p2_status = new JLabel();
			p2_status.setFont(new Font("Calibri", Font.PLAIN, 14));
			p2_status.setForeground(Color.black);
			p2_status.setBounds(520, 72, 200,30);
			add(p2_status);

			// Ready status label for client 1
			p1_isReady_lbl = new JLabel("Ready Status:");
			p1_isReady_lbl.setFont(new Font("Calibri", Font.BOLD, 14));
			p1_isReady_lbl.setForeground(Color.black);
			p1_isReady_lbl.setBounds(90, 10, 200,30);
			add(p1_isReady_lbl);

			p1_isReady = new JLabel();
			p1_isReady.setFont(new Font("Calibri", Font.BOLD, 18));
			p1_isReady.setForeground(Color.black);
			p1_isReady.setBounds(70, 36, 200,30);
			add(p1_isReady);

			// Ready status label for client 2
			p2_isReady_lbl = new JLabel("Ready Status:");
			p2_isReady_lbl.setFont(new Font("Calibri", Font.BOLD, 14));
			p2_isReady_lbl.setForeground(Color.black);
			p2_isReady_lbl.setBounds(690, 10, 200,30);
			add(p2_isReady_lbl);

			p2_isReady = new JLabel();
			p2_isReady.setFont(new Font("Calibri", Font.BOLD, 18));
			p2_isReady.setForeground(Color.black);
			p2_isReady.setBounds(670, 35, 200,30);
			add(p2_isReady);

			// Count down timer label
			start_counter = new JLabel();
			start_counter.setFont(new Font("Calibri", Font.BOLD, 22));
			start_counter.setForeground(Color.black);
			start_counter.setHorizontalAlignment(JLabel.CENTER);
			start_counter.setBounds(280, 620, 300,35);
			add(start_counter);
	 }


	 // Method that start count down the timer
	 public void countDownTimer() 
     {
		 starttimer = new Timer(1000, new ActionListener() //set the timer delay in 1000 miliseconds
		 {	
			 @Override // if the countDownTimer() is called, the actionPerformed will start running
			 public void actionPerformed(ActionEvent e) 
			 {	
				second--; //second values decrement by 1
				//convert the decremented second to a decimal format and store in ddSecond variable
				ddSecond = dFormat.format(second); 

				// Keep updating the decremented second to the label
				start_counter.setText("Race will start in "+ ddSecond +" seconds");
				
				// if the second become 0 then update the label to Race Start!
				if(second==0) 
				{
					second = 0; //Maintain second in 0
					start_counter.setText("Race Start!");
					starttimer.stop(); //Stop the starttimer 
					startRace = true; //Set the startRace to true
				}
			 }
		 });		
     }
	 
	 // Method that create new object for clientSideConnection
	 public void connectToServer() 
	 {
		 clientConnection = new ClientSideConnection();	 
	 }

	 // Method that start getting data from server
	 public void startGetDataFromServer() 
	 {
		 Thread thread = new Thread(() -> {
			 while (true) {
				 clientConnection.getResponseFromServer();
			 }
		 });
		 thread.start();
	 }
	 
	 // Method that start the countdown timer when both players are ready
	 public void setPlayerReady() 
	 {
		 if (playersAreReady && !startRace)
		 {
			 starttimer.start(); //start the count down timer
		 }
	 }
	 
	 // Method that used to reduce the delay between 2 thread of client for 
	 // sending and receiving data to/from server.
	 public void reduceDelay()
	 {
		 try {
				Thread.sleep(0); //To achieve synchronization of two threads, set the Thread.sleep to 0
			} catch (InterruptedException e) {e.printStackTrace();}
	 }
	 
	 public class ClientSideConnection 
	 {
		 private Socket socket;// obj of Socke
		 private DataInputStream dataIn;   //obj of DataInputStream 
		 private DataOutputStream dataOut; //obj of DataOutputStream
		    
		 public ClientSideConnection() 
		 {
			 System.out.println("=====Client=====");
		     try 
		     {
		    	 //client access connection to server
		         socket = new Socket("localhost", 5000);
		         //to read primitive Java datatypes from an underlying input stream
		         dataIn = new DataInputStream(socket.getInputStream());
		         //to write primitive Java datatypes to an output stream
		         dataOut = new DataOutputStream(socket.getOutputStream());

		         playerID = dataIn.readInt(); // get the playerID from server
		         System.out.println("Player " + playerID + " is Ready and connected to the Server");
		     } 
		     catch (IOException ex) 
		     {
		    	 ex.printStackTrace(); 
		     }
		 }

		 public void sendResponseToServer() {
			 checkReadyStatus();
			 int direction, speed;
			 if (playerID == 1) {
				 player1_IsReady = true;
				 direction = kart1.getDirection();
				 speed = kart1.getCurrentSpeed();
			 } else if (playerID == 2) {
				 player2_IsReady = true;
				 direction = kart2.getDirection();
				 speed = kart2.getCurrentSpeed();
			 } else {
				 return;
			 }
			 try {
				 dataOut.writeInt(playerID);
				 dataOut.writeInt(direction);
				 dataOut.writeInt(speed);
				 reduceDelay();
			 } catch (IOException e) {
				 // handle exception
			 }
		 }



		 // Method that Reading data from the server
		 public void getResponseFromServer() {
			 try {
				 opponentID = dataIn.readInt();
				 d = dataIn.readInt();
				 s = dataIn.readInt();
				 setPlayerReady();

				 KartActionPerformed opponentKart = (playerID == 1 && opponentID == 2) ? kart2 :
						 (playerID == 2 && opponentID == 1) ? kart1 :
								 null;

				 if (opponentKart != null) {
					 playersAreReady = true;
					 opponentKart.setDirection(d);
					 opponentKart.setCurrentSpeed(s);
					 reduceDelay();
				 }
			 } catch (IOException ex) {
				 ex.printStackTrace();
			 }
		 }

	 }
} 

