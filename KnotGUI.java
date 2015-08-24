import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
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

	private JButton planarPicButton, arcPicButton	;
	private JButton genShadowButton, genAltButton, genAltPrimeButton;
	private JTextField gaussInputField;
	private PlanarPictureDraw picDraw;

	public KnotGUI()
	{
		super("KnotGUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		createMenuBar();
		

		layoutTop();

		layoutCenter();

		pack();
		setVisible(true);
	}

	public void createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		// create the high level menu list
        JMenu file = new JMenu("File");
        JMenu generate = new JMenu("Generate");
        JMenu save = new JMenu("Save");
        
        // create the file menu options 
        JMenuItem openGC = new JMenuItem("Open Gauss code from file");
        JMenuItem exit = new JMenuItem("Exit");

        openGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("openGC pressed");
            }
       	});

        exit.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        file.add(openGC);
        file.add(exit);

        // create the genertion menu options
        JMenuItem generateRandomGC = new JMenuItem("Generate random GC");
        JMenuItem generateAllGC = new JMenuItem("Generate all Gauss codes of size n");

        generateRandomGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("generateRandomGC pressed");
            }
        });

       	generateAllGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("generateAllGC pressed");
            }
        });

       	generate.add(generateRandomGC);
       	generate.add(generateAllGC);

       	// create the save menu optiongs
       	JMenuItem saveGC = new JMenuItem("Save Gauss code");
       	JMenuItem savePic = new JMenuItem("Save knot picture");

       	saveGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("saveGC pressed");
            }
        });

       	savePic.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("savePic pressed");
            }
        });

       	save.add(saveGC);
       	save.add(savePic);

       	menuBar.add(file);
       	menuBar.add(generate);
       	menuBar.add(save);

        setJMenuBar(menuBar);
	}

	public JLabel makePic() throws IOException
	{
		// JLabel picLabel = new JLabel();
		// setPreferredSize(

		// BufferedImage myPicture = ImageIO.read(new File("temp/planarPic.jpg"));
		JLabel picLabel = new JLabel();//new JLabel(new ImageIcon(myPicture));
		picLabel.setIcon( new ImageIcon(ImageIO.read( new File("temp/planarPic.jpg") ) ) );
		add(picLabel, BorderLayout.SOUTH);
		return picLabel;
	}

	private JTextField makeText()
	{
		JTextField a = new JTextField(30);
		a.setText("-1, 2, -3, 1, -2, 3");
		// add(a, BorderLayout.NORTH);
		return a;
	}

	private JButton makeButton(String label)
	{
		JButton a = new JButton(label); 
		return a;
	}

	public void layoutTop() {
		JPanel top = new JPanel();
		JLabel gaussLabel = new JLabel();
		gaussLabel.setText("Gauss code: ");
		gaussInputField = makeText();
		top.add(gaussLabel);
		top.add(gaussInputField);

		// genShadowButton = new JButton("genShadowButton");
		// genShadowButton.addActionListener(this);
		// top.add(genShadowButton, BorderLayout.WEST);

		// genAltButton  = new JButton("genAltButton");
		// genAltButton.addActionListener(this);
		// top.add(genAltButton, BorderLayout.EAST);

		// genAltPrimeButton = new JButton("genAltPrimeButton");
		// genAltPrimeButton.addActionListener(this);
		// top.add(genAltPrimeButton, BorderLayout.SOUTH);

		// attendanceButton = new JButton("View Attendances");
		// attendanceButton.addActionListener(this);
		// top.add(attendanceButton);
		add(top, BorderLayout.NORTH);
	}

	/**
	 * adds labels, text fields and buttons to bottom of GUI
	 */
	public void layoutCenter() 
	{
		// instantiate panel for bottom of display
		JTabbedPane jTabs = new JTabbedPane();

		JPanel d = layoutDrawingPane();
		JPanel invar = layoutInvariantsPane();

		jTabs.add("Drawing", d);
		jTabs.add("Invariants", invar);

		add(jTabs);
		
	}

	public JPanel layoutDrawingPane()
	{
		JPanel drawing = new JPanel(new BorderLayout());

		JPanel drawingButtons = new JPanel(new GridBagLayout());
		// add upper label, text field and button
		planarPicButton = makeButton("Draw knot diagram");
		planarPicButton.addActionListener(this);

		arcPicButton = makeButton("Draw arc diagram");
		arcPicButton.addActionListener(this);

		drawingButtons.add(planarPicButton);
		drawingButtons.add(arcPicButton);

		try
		{
			picture = makePic();
			// pictureA = makePic();
		}
		catch(IOException e)
		{
			System.out.println("Image file not found");
		}

		drawing.add(drawingButtons, BorderLayout.NORTH);
		drawing.add(picture, BorderLayout.CENTER);

		return drawing;
	}

	public JPanel layoutInvariantsPane()
	{
		JPanel invariants = new JPanel(new BorderLayout());
		JLabel temp = new JLabel("colouring will go here");

		invariants.add(temp);

		return invariants;
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == planarPicButton)
		{
			(picDraw = new PlanarPictureDraw()).execute();
		}
	}

	// This is the object that does the drawing
	// The two arguments in the <> are the return type
	// of doInBackground() and the type that you want to pass
	// to publish()
	// private class DrawTask extends SwingWorker<Void,Integer>{
	// 	protected Void doInBackground() {
	// 		try {
	// 				Integer count = Integer.parseInt(countText.getText());
	// 				while(!isCancelled()) {
	// 					count++;
	// 					Thread.sleep(100);
	// 					// Publish is a method provided by SwingWorker that stores
	// 					// the count variables in a list that can be accessed by process
	// 					publish(count); 
	// 				}
	// 			}catch(InterruptedException e) {}
	// 		return null;
	// 	}
	// 	// Every now and then the event dispatch thread will call process
	// 	// In this example, I get it to set the count text value
	// 	protected void process(List<Integer> counts) {
	// 		int lastVal = counts.get(counts.size()-1);
	// 		countText.setText(String.format("%d",lastVal));
	// 	}
	// }

   	private class PlanarPictureDraw extends SwingWorker<Void, Void> {
       	@Override
       	public Void doInBackground() {
			MathematicaAdapter ma = new MathematicaAdapter();

			String gaussString = gaussInputField.getText();

			try
			{
				ma.drawPlanarDiagram(gaussString);
			}
			catch (Exception e)
			{
				System.out.println("Knot diagram picture not found");
			}
       	    return null;
       	}	

      	@Override
       	protected void done() {
        		try {
        		System.out.println("Got in the done method	");
        		// ImageIcon icon = new ImageIcon("/temp/planarPic.jpg");
        		// icon.getImage().flush();
          //      	picture.setIcon(icon);
        		// resetPicture();
        		picture.setIcon(null);
				picture.setIcon( new ImageIcon(ImageIO.read( new File("temp/planarPic.jpg") ) ) );
				picture.revalidate();
				System.out.println("We've reset the picture");
           	} catch (Exception ignore) {
           	}
       	}
   	}

   	public void resetPicture() throws IOException
   	{
   		picture.setIcon( new ImageIcon(ImageIO.read( new File("temp/planarPic.jpg") ) ) );		
   	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new KnotGUI();
			}
		});
	}

		// EventQueue.invokeLater(new Runnable() {

  //           @Override
  //           public void run() {
  //               KnotGUI kGUI = new KnotGUI();
  //               kGUI.setVisible(true);
  //           }
  //       });

	// }
}