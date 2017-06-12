package codetest.lstm.launcher;

import codetest.lstm.core.Monkey;

public class MainLauncherConsole 
{
	public static void main( String[] args ) throws Exception
	{
		int miniBatchSize = 32;						
		int sequenceLength = 1000;					
		int numEpochs = 1;						
		
		String filePath = "./britney.txt";

		Monkey kong = new Monkey();
		
		kong.loadData(filePath, miniBatchSize, sequenceLength);
		kong.train(numEpochs, null);
		
		for (int i = 0; i < 5; i++)
		{
			String test = kong.generate(null);
			
			System.out.println(test);
		}
	}
}
