import java.io.IOException; //throw an I/O exception message when the program reading a sound file that does not exist.
//The javax.sound.sampled are necessary to import for processing and playback of all the kart sound effects. 
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundEffect {
	private final static String LOWSPEED = "SoundEffects/speed_low_sound.wav";
	//private final static String Breaking = "sound/speed_medium_sound.wav";
	private final static String HighSPEED = "SoundEffects/bmw_m5.wav";



	private AudioInputStream kart1_audioStream_low;
	private AudioInputStream kart1_audioStream_max;

	private Clip kart1_clip_low;
	private Clip kart1_clip_break;
	private Clip kart1_clip_max;


	public void addingSoundEffect() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		kart1_audioStream_low = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResourceAsStream(LOWSPEED));
		kart1_clip_low = AudioSystem.getClip(); // get the low speed sound effect references from AudioSystem.
		kart1_clip_low.open(kart1_audioStream_low); // open the clip for low speed sound effect

		kart1_audioStream_max = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResourceAsStream(HighSPEED));
		kart1_clip_max = AudioSystem.getClip(); // get the max speed sound effect references from AudioSystem.
		kart1_clip_max.open(kart1_audioStream_max); // open the clip for max speed sound effect


	}

	public void LowSpeedSound()
	{
		if (kart1_clip_low.isRunning())
		{
			kart1_clip_low.stop();   // Stop the player if it is still running
		}
		kart1_clip_low.start();     // Start playing
		kart1_clip_low.loop(Clip.LOOP_CONTINUOUSLY); //to make the sound continues indefinitely.
	}

	public void BreakingSoundEffects()
	{
		if (kart1_clip_break.isRunning())
		{
			stopBreakingSound();
		}
		kart1_clip_break.start();
		kart1_clip_break.loop(Clip.LOOP_CONTINUOUSLY);
	}
	public void stopBreakingSound() //stop playing and rewind to be played again from the beginning
	{
		kart1_clip_break.stop();
		kart1_clip_break.setFramePosition(0);
	}

	public void highSpeedSound()
	{
		if (kart1_clip_max.isRunning())
		{
			kart1_clip_max.stop();   // Stop the player if it is still running
		}
		kart1_clip_max.start();     // Start playing
		kart1_clip_max.loop(Clip.LOOP_CONTINUOUSLY); //to make the sound continues indefinitely.
	}

	public void stopLowSpeedSound() //stop playing and rewind to be played again from the beginning
	{
		kart1_clip_low.stop();
		kart1_clip_low.setFramePosition(0);
	}


	public void stopHighSpeedSound() //stop playing and rewind to be played again from the beginning
	{
		kart1_clip_max.stop();
		kart1_clip_max.setFramePosition(0);
	}


}
