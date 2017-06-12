package codetest.lstm.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class FormMain 
{
	private final String DEFAULT_WINDOW_TITLE = "<1000MONKEYS1000>"; 

	//----------------------------------------------------------------
	//---- Environment variables

	private static FormMain instance = null;

	private JFrame frameMain;


	private int windowStartX = 100;
	private int windowStartY = 100;
	private int windowWidth = 330;
	private int windowHeight = 400;

	private FormMainPanel panelAPP = null;

	//----------------------------------------------------------------

	private FormMain (Rectangle screenResolution, FormMainHandler handler)
	{
		//---- Define all components of  the panel

		//	controllerButton.init(this);

		//---- Create the main panel and set up all components
		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		addComponents(panelMain, handler);

		//---- Finalize creation of the main window
		frameMain = new JFrame();
		frameMain.setTitle(DEFAULT_WINDOW_TITLE);
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMain.setSize(windowWidth, windowHeight);
		frameMain.setResizable(false);
		frameMain.setLocation(windowStartX, windowStartY);
		frameMain.setContentPane(panelMain);

		frameMain.setVisible(true);


	}

	public static FormMain getInstance (Rectangle screenResolution, FormMainHandler controllerButton)
	{
		if (instance == null) { instance = new FormMain(screenResolution, controllerButton); }

		return instance;
	}

	private void addComponents (JPanel panel, FormMainHandler handler)
	{
		panelAPP= new FormMainPanel(handler);
		
		panel.add(panelAPP.mainPanel, BorderLayout.CENTER);
	}
	
	public FormMainPanel getComponentPanel ()
	{
		return panelAPP;
	}
}
