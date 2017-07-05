package codetest.lstm.launcher;

import java.awt.Rectangle;

import codetest.lstm.core.CoreTrainer;
import codetest.lstm.gui.FormMain;
import codetest.lstm.gui.FormMainHandler;



/**
 * This class handles all the operations with all data/gui components.
 * @author Andrey Grushnikov
 */

public class MainController
{
	private static MainController controller = null;

	//---- Main GUI form of the application and its controllers (handlers)
	private FormMain windowMain = null;
	private FormMainHandler windowMainHandler = null;

	private CoreTrainer monkey = null;
	
	//----------------------------------------------------------------

	private MainController ()
	{

	}

	public static MainController getInstance ()
	{
		if (controller == null) { controller = new MainController(); }

		return controller;
	}

	//----------------------------------------------------------------

	public void launchMainWindow (Rectangle screenResolution)
	{
		//---- Init handlers, create window, launch.
		if (windowMain == null) 
		{ 
			monkey = new CoreTrainer();
			
			windowMainHandler = new FormMainHandler();

			windowMain = FormMain.getInstance(screenResolution, windowMainHandler); 

			windowMainHandler.init(windowMain, monkey);
		}
	}

}
