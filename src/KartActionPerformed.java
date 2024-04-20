import java.awt.*;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class KartActionPerformed extends JPanel
{
	//declare the images as ImageIcon data type and stored into an array variable
	protected ImageIcon kart_Images[];
    private int total_KartImages = 16; // total number of images
    public Image kart_Image;	// declare as Image data type to support the png format
    private int width,height,currentDirection,speed;

    private int position_X, position_Y; //x position and y position of kart

    private boolean Collision = false; //To check if the kart is crash
    private boolean Disqualify = false; //To check if the kart is cheating
    private boolean CheckStatus = false; //To check if the kart is wins
    private SoundEffect sound;	// To call the method from SoundEffect class

    public KartActionPerformed (String IMAGE_NAME, int position_X, int yPos) throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        kart_Images = new ImageIcon[total_KartImages];

        for (int count = 0; count < kart_Images.length; count++) // if count < 16
        {
            kart_Images[count] = new ImageIcon(getClass().getResource(IMAGE_NAME + count + ".png"));
        }
        width = kart_Images[0].getIconWidth(); // get icon width
        height = kart_Images[0].getIconHeight(); // get icon height

        this.position_X = position_X;
        this.position_Y = yPos;
        this.currentDirection = 4; // set the current direction of kart to 4
        this.speed = 0; //set the default speed to 0
        sound = new SoundEffect(); // create object fo SoundEffect class
        sound.addingSoundEffect(); // read and open the sound effect file
    }
    
    //Getters and setters allow to control how important variables are accessed and updated in your code. 
    //update the current kart images
    public void setCurrentKart(Image kart) 
    {
        this.kart_Image = kart;
    }
    
    //return the current kart images
    public Image getCurrentKartImg() 
    {
        return kart_Image;
    }
    
    //return the width of kart
    public int getWidth() 
    {
        return width;
    }
    
    //return the height of kart
    public int getHeight() 
    {
        return height;
    }
    
    //return the x position of kart
    public int getX() 
    {
        return position_X;
    }
    
    //update the x position of kart
    public void setX(int xDir) 
    {
        this.position_X = xDir;
    }
    
    //return the y position of kart
    public int getY() 
    {
        return position_Y;
    }

    //update the y position of kart
    public void setY(int yDir) 
    {
        this.position_Y = yDir;
    }
    
    //return the current direction of kart
    public int getDirection() 
    {
        return currentDirection;
    }
    
    //update the current direction of kart
    public void setDirection(int arraySize) 
    {
        arraySize = (arraySize <= -1 || arraySize > 15) ? (arraySize <= -1 ? 15 : 0) : arraySize;
        currentDirection = arraySize;

    }
    
    //return the current speed of kart
    public int getCurrentSpeed()
    {
        return speed;
    }
    
    //update the current speed of kart
    public void setCurrentSpeed(int currentSpeed)
    {
        currentSpeed= (currentSpeed > 10)?   10:currentSpeed;
        currentSpeed =  (currentSpeed < 0)?  0:currentSpeed;

        this.speed = currentSpeed;
    }
    
    // Basic kart control logic
    public void kartControl() {
        // Array to store the x and y increments for each direction
        int[][] directions = {
                {0, -2},   // 0
                {1, -2},   // 1
                {2, -2},   // 2
                {2, -1},   // 3
                {2, 0},    // 4
                {2, 1},    // 5
                {2, 2},    // 6
                {1, 2},    // 7
                {0, 2},    // 8
                {-1, 2},   // 9
                {-2, 2},   // 10
                {-2, 1},   // 11
                {-2, 0},   // 12
                {-2, -1},  // 13
                {-2, -2},  // 14
                {-1, -2}   // 15
        };

        // Update the position of the kart based on its current direction
        position_X += speed * directions[currentDirection][0];
        position_Y += speed * directions[currentDirection][1];

        // Update the kart image based on its current direction
        kart_Image = kart_Images[currentDirection].getImage();
    }


    // return true/false for kart collides
    public boolean getCollide() 
    {
        return Collision;
    }
  
    // update true/false for kart collides
    public void setCollide(boolean isCrash) 
    {
    	this.Collision = isCrash;
    }
    
    // return true/false for kart cheating
    public boolean getCheatAction() 
    {
        return Disqualify;
    }
    
    // update true/false for kart cheating
    public void checkCheatAction(boolean Disqualify)
    {
    	this.Disqualify = Disqualify;
    }
    
    // return true/false for kart wins
	public boolean getCheckStatus()
	{
		return CheckStatus;
	}
	
	// update true/false for kart wins
	public void CheckStatus(boolean isWin)
	{
		this.CheckStatus = isWin;
	}
	
	// Kart 1
	// Setup different sound effect for different speed
    public void SoundEffects() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        if(getCurrentSpeed() == 0)
        {
            sound.stopLowSpeedSound();
            sound.stopHighSpeedSound();
        }
        else if((getCurrentSpeed() > 0 && getCurrentSpeed() <= 5))
        {
            sound.LowSpeedSound();
            sound.stopHighSpeedSound();
        }

        else if((getCurrentSpeed() > 5 && getCurrentSpeed() <= 10))
        {
            sound.stopLowSpeedSound();
            sound.highSpeedSound();
        }
    }

}
