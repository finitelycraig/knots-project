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

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import java.util.concurrent.TimeUnit;


public class KnotGUI extends JFrame implements ActionListener {
	private JLabel picture = null;
	// private JLabel pictureA = null;
	private final int KNOT_DIAGRAM = 0;
	private final int ARC_PRESENTATION = 1;
	private int[] primes = new int[]{3, 5, 7};

	private int picToBeDrawn = 0;

	private JButton planarPicButton, arcPicButton;
	private JButton genShadowButton, genAltButton, genAltPrimeButton;
	private JButton colouringStartButton;
	private JTextField gaussInputField;
	private JLabel colouringLabel;
	
	private PictureDraw picDraw;
	private GaussCodeGenTask gen;
	private ColouringTask colour;

	private int crossings = 0;
	private int generationOptions = 0;

	private String gaussCode = "";

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
        JMenuItem genearteRandomPrimeGC = new JMenuItem("Generate random prime GC");
        JMenuItem generateAllGC = new JMenuItem("Generate all Gauss codes of size n");

        generateRandomGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("generateRandomGC pressed");
            	generationOptions = 0;
            	crossings = Integer.parseInt(JOptionPane.showInputDialog("Enter crossing number"));
            	(gen = new GaussCodeGenTask()).execute();

            }
        });

        genearteRandomPrimeGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("generateRandomPrimeGC pressed");
            	generationOptions = 2;
            	crossings = Integer.parseInt(JOptionPane.showInputDialog("Enter crossing number"));
            	(gen = new GaussCodeGenTask()).execute();
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
       	generate.add(genearteRandomPrimeGC);
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
		colouringLabel = new JLabel("This knot is colourable mod:");

		colouringStartButton = makeButton("Start colouring");
		colouringStartButton.addActionListener(this);

		invariants.add(colouringStartButton, BorderLayout.NORTH);
		invariants.add(colouringLabel, BorderLayout.CENTER);

		return invariants;
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == planarPicButton)
		{
			picToBeDrawn = KNOT_DIAGRAM;

			(picDraw = new PictureDraw()).execute();
			try
			{
				picDraw.get(15, TimeUnit.SECONDS); 
			}
			catch(Exception e)
			{
				System.out.println("That took too long");
				// imposing a timeout on picDraw doesn't impose a timeout on the athematica script that it calls
				// here we kill the WolframKernel to stop the Mathematica script
				try 
				{
					System.out.println("In the try");
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("pkill -9 WolframKernel");
					System.out.println("Killed the WolframKernel");
				}
				catch(Exception ex)
				{
					System.out.println("There was an error in killing WolframKernel");
				}
				JOptionPane.showMessageDialog(this,
    "The Mathematica script used to draw knot\ndiagrams often finds nonprime knots difficult, \nso we've set a 15 second timeout.",
    "Drawing Timeout",
    JOptionPane.WARNING_MESSAGE);


			}
		}
		else if (ae.getSource() == arcPicButton)
		{
			picToBeDrawn = ARC_PRESENTATION;
			
			(picDraw = new PictureDraw()).execute();
			try
			{
				picDraw.get(60, TimeUnit.SECONDS); 
			}
			catch(Exception e)
			{
				System.out.println("That took too long");
				try 
				{
					System.out.println("In the try");
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("pkill -9 WolframKernel");
					System.out.println("Killed the WolframKernel");
				}
				catch(Exception ex)
				{
					System.out.println("There was an error in killing WolframKernel");
				}
				JOptionPane.showMessageDialog(this,
    "The Mathematica script used to draw knot\ndiagrams often finds nonprime knots difficult, \nso we've set a 15 second timeout.",
    "Drawing Timeout",
    JOptionPane.WARNING_MESSAGE);

			}
		}
		else if (ae.getSource() == colouringStartButton)
		{
			colouringLabel.setText("This knot is colourable mod:");
			(colour = new ColouringTask()).execute();
		}
	}
	

   	private class PictureDraw extends SwingWorker<Void, Void> {
       	@Override
       	public Void doInBackground() 
       	{
			MathematicaAdapter ma = new MathematicaAdapter();

			String gaussString = gaussInputField.getText();

			try
			{
				if (picToBeDrawn == KNOT_DIAGRAM)
				{
					ma.drawPlanarDiagram(gaussString);
				}
				else if (picToBeDrawn == ARC_PRESENTATION)
				{
					ma.drawArcPresentation(gaussString);
				}
			}
			catch (Exception e)
			{
				System.out.println("Knot diagram picture not found");
			}

       	    return null;
       	}	

      	@Override
       	protected void done() {
        		try 
        		{
	        		System.out.println("Got in the done method");
	        		// ImageIcon icon = new ImageIcon("/temp/planarPic.jpg");
	        		// icon.getImage().flush();
	          //      	picture.setIcon(icon);
	        		// resetPicture();
	        		picture.setIcon(null);
	        		if (picToBeDrawn == KNOT_DIAGRAM)
	        		{
						picture.setIcon( new ImageIcon(ImageIO.read( new File("temp/planarPic.jpg") ) ) );
	        		}
	        		else if (picToBeDrawn == ARC_PRESENTATION)
	        		{
	        			picture.setIcon( new ImageIcon(ImageIO.read( new File("temp/arcPic.jpg") ) ) );
	        		}
					picture.revalidate();
					System.out.println("We've reset the picture");
           		} 
           		catch (Exception ignore) 
           		{
           			//do nothing
           		}
       	}
   	}

   	private class GaussCodeGenTask extends SwingWorker<Void, Void> {
       	@Override
       	public Void doInBackground() 
       	{
       		// we don't want verbose output --- that's why false
            NaiveShadowGaussGenerator sGG = new NaiveShadowGaussGenerator(crossings, generationOptions, false);

            gaussCode = sGG.solutionToString();

       	    return null;
       	}	

      	@Override
       	protected void done() 
       	{
       		gaussInputField.setText(gaussCode);
       	}
   	}

   	private class ColouringTask extends SwingWorker<Void, Integer> {
	    @Override
	    public Void doInBackground() 
	    {
	    	System.out.println("In do in doInBackground");
	    	////////////////////// Set up the knot object from the Gauss code ///////////////////////
			LinkedList<Integer> gaussList = new LinkedList<Integer>();

			gaussCode = gaussInputField.getText();

			for (String s: gaussCode.split("[, ]+"))
			{
				System.out.println("Splitting gauss code");

				int num = Integer.parseInt(s);

				System.out.print(num + " ");

				gaussList.addLast(num);
     		}

	    	System.out.println("Size of gaussList = " + gaussList.size());

			Knot knot = new AdjSetKnot();
			int size = gaussList.size();
			int halfSize = size / 2;
    		
     		for (int i = 0; i < halfSize; i++)
     		{
     			System.out.println("Adding crossings to knot");

				knot.addCrossing("" + i);
     		}

     		for (int i = 0 ; i < size; i++ ) 
     		{
		    	System.out.println("Adding arcs to knot");

     			int n = gaussList.get(i);
     			int m;
     			if (i == (size -1))
     			{
     				m = gaussList.get(0);
     			}
     			else
     			{
     				m = gaussList.get(i + 1);
     			}
     			
     			Knot.Crossing source = knot.getByOrderAdded(Math.abs(n));
     			Knot.Crossing target = knot.getByOrderAdded(Math.abs(m));

     			knot.addArc(source, target, orient(n), orient(m));
     		}

     		for (int i = 0; i < primes.length; i++)
     		{
     			System.out.println("colouring mod " + primes[i]);
     			Colourist colourist = new Colourist(knot, primes[i]);

     			if (colourist.isColourable())
     			{
     				publish(new Integer(primes[i]));
     			}
     		}
    	    gaussList.clear();

	   	    return null;
	   	}	

	   	// Every now and then the event dispatch thread will call process
		// In this example, I get it to set the count text value
		protected void process(List<Integer> colourable) {
			int lastVal = colourable.get(colourable.size()-1);
			String alreadyOnLabel = colouringLabel.getText();
			colouringLabel.setText(alreadyOnLabel + " " + lastVal);
		}

	  	@Override
	   	protected void done() 
	   	{
	   		if (colouringLabel.getText().equals("This knot is colourable mod:"))
	   		{
	   			colouringLabel.setText("This knot isn't colourable mod 3, 5, or 7");
	   		}
	   	}

	   	    public int orient(int n)
    {
    	int orient;

    	if (n < 0)
    	{
    		orient = Knot.UNDER;
    	}
    	else
    	{
    		orient = Knot.OVER;
    	}

    	return orient;
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