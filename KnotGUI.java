import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.util.*;
import java.io.*;

import javax.imageio.ImageIO;
// import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;


public class KnotGUI extends JFrame implements ActionListener {
	private JLabel picture = null;
	// private JLabel pictureA = null;


	private JTextField gaussInputField;

	public KnotGUI()
	{
		super("KnotGUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		gaussInputField = makeText();

		try
		{
			picture = makePic();
			// pictureA = makePic();
		}
		catch(IOException e)
		{
			System.out.println("Image file not found");
		}


		pack();
		setVisible(true);
	}

	public JLabel makePic() throws IOException
	{
		BufferedImage myPicture = ImageIO.read(new File("planarTrefoil.jpg"));
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		add(picLabel, BorderLayout.SOUTH);
		return picLabel;
	}

	private JTextField makeText()
	{
		JTextField a = new JTextField(30);
		add(a, BorderLayout.NORTH);
		return a;
	}

	public void actionPerformed(ActionEvent e)
	{
		//stuff in time
	}

	public static void main(String[] args)
	{
		new KnotGUI();
	}
}