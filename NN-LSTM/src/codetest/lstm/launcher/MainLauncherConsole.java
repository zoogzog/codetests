package codetest.lstm.launcher;

import codetest.lstm.core.Monkey;

/**
 * Console version of the app
 * @author Grushinkov Andrey
 */
public class MainLauncherConsole 
{
	public static void main( String[] args ) throws Exception
	{
		int miniBatchSize = 32;						
		int sequenceLength = 1000;					
		int numEpochs = 1;		
		
		int sequenceLengthOutput = 300;
		
		String filePath = "./britney.txt";

		Monkey kingkong = new Monkey();
		
		kingkong.loadData(filePath, miniBatchSize, sequenceLength);
		kingkong.train(numEpochs, null);
		
		for (int i = 0; i < 5; i++)
		{
			String test = kingkong.generate(null, sequenceLengthOutput);
			
			System.out.println(test);
		}
	}
}
