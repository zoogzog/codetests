package codetest.lstm.launcher;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main entry point for the application.
 * @author Grushnikov Andrey
 */

public class MainLauncherGui 
{
	public static void main(String args[])
	{

		//---- Launch the main GUI window
		SwingUtilities.invokeLater(new Runnable() {public void run() {launch();}});
	}

	public static void launch () 
	{
		//---- Set default fonts for the application
		UIManager.getLookAndFeelDefaults().put("defaultFont", new java.awt.Font("Times New Roman", 0, 12));

		
		//---- Detect the maximum screen resolution
		Rectangle screenResolution = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

		MainController controller = MainController.getInstance();
		controller.launchMainWindow(screenResolution);

	}
}
