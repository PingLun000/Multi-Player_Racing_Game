import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class Client2 extends JFrame implements MouseListener {

	private JTextField textField;

	public Client2() {
		this.setTitle("Kart Racing Game");
		this.setBounds(300, 50, 870, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addMouseListener(this);

		// A container object to retrieve the content pane layer
		Container contentPane = this.getContentPane();

		try {
			// Add the race track panel to the content pane layer
			contentPane.add(new RaceTrack());
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}

		textField = new JTextField();
		contentPane.add(textField, BorderLayout.SOUTH);
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		textField.setText("X:" + x + " Y:" + y);
	}

	// Empty implementations of the other MouseListener methods
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public static void main(String[] args) {
		Client2 game = new Client2();
		game.setVisible(true);
	}
}
