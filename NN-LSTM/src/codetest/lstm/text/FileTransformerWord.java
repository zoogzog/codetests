package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class FileTransformerWord implements FileTransformer
{
	private TextTokenizer txtdriver;
	
	
	public FileTransformerWord() 
	{
		txtdriver = new TextTokenizer();
	}
	
	
	
	
	@Override
	public void transform(String path, SequenceStorage storage) 
	{

		try
		{
			if( !new File(path).exists()) { return; }
	
			BufferedReader br = new BufferedReader(new FileReader(path));
						
			Vector <Integer> wordList = new Vector <Integer>();
			
			String str = "";

			while ((str = br.readLine()) != null)
			{

				String[] tokenList = txtdriver.generateTokens(str, true);
				int[] dictionaryIndexList = txtdriver.getDictionaryIndex(tokenList);
				
				for (int k = 0; k < dictionaryIndexList.length; k++)
				{
					wordList.addElement(dictionaryIndexList[k]);
				}
			}
			
			br.close();
			
			
			System.out.println("Loaded words: " + wordList.size());
			
			//---- Allocate memory and init pointer for sotring data
			storage.allocateMemory(wordList.size());
			//---- Specify the max value could be encountered in the sequence
			storage.setSequenceMax(txtdriver.getDictionarySize());
			
			
			for (int k = 0; k < wordList.size(); k++)
			{
				storage.set(k, wordList.get(k));
			}
			
			System.out.println("Loaded words: " + wordList.size());
		}
		catch (Exception e) { e.printStackTrace();}
	}

	@Override
	public String transformIndexSequence(int[] seq) 
	{
		String output= "";
		
		for (int i = 0; i < seq.length; i++)
		{
			output = output + " " + txtdriver.getDictionaryWord(seq[i]);
		}
		
		
		return output;
	}

}
