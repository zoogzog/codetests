package codetest.lstm.text;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FileTransformerCharacter implements FileTransformer
{
	
	public char[] vocabulary = null;
	
	private Map<Character,Integer> dictionary = new HashMap<>();;
	
	//------------------------------------------------------------
	
	public FileTransformerCharacter ()
	{
		vocabulary = generateDefaultCharacterSet();
		
		for (int i = 0; i < vocabulary.length; i++)
		{
			dictionary.put(vocabulary[i], i);
		}
	}
	
	public FileTransformerCharacter (char[] vocabulary)
	{
		this.vocabulary = vocabulary;
	
		for (int i = 0; i < vocabulary.length; i++)
		{
			dictionary.put(vocabulary[i], i);
		}
	}
	
	//------------------------------------------------------------

	@Override
	public void transform(String path, SequenceStorage storage) 
	{
		try
		{
			if( !new File(path).exists()) { return; }
		
			List<String> lines = Files.readAllLines(new File(path).toPath(), Charset.forName("UTF-8"));
			
			//---- Calculate max memory required to store the sequence
			int sequenceLengthTotal = lines.size();
		
			//---- Calculate max size. Yes, this is slow, but easy to understand, can be re-written
			for (String s : lines) 
			{
				char[] buffer = s.toCharArray();
				
				for (int k = 0; k < buffer.length; k++)
				{
					if (dictionary.containsKey(buffer[k]))
					{
						sequenceLengthTotal++;
					}
				}
			}
			
			//---- Allocate memory and init pointer for sotring data
			storage.allocateMemory(sequenceLengthTotal);
			//---- Specify the max value could be encountered in the sequence
			storage.setSequenceMax(vocabulary.length);

			
			int index = 0;
			
			for (String s : lines) 
			{
				char[] buffer = s.toCharArray();
				
				for (int k = 0; k < buffer.length; k++)
				{
					if (dictionary.containsKey(buffer[k]))
					{
						storage.set(index, dictionary.get(buffer[k]));
						index++;
					}
				}
				if(dictionary.containsKey('\n')) { storage.set(index, dictionary.get('\n')); index++; }
				
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();	
		}
	}

	//------------------------------------------------------------
	
	public int transformChar (char c)
	{
		if (dictionary.containsKey(c)) { return dictionary.get(c); }
		
		return -1;
	}
	
	public char transformIndex (int index)
	{
		if (index < 0 || index >= vocabulary.length) { return 0xFFFF; }
		
		if (dictionary.containsValue(index)) { return vocabulary[index]; }
		
		return 0xFFFF;
	}
	
	@Override
	public String transformIndexSequence (int[] seq)
	{
		char[] output = new char[seq.length];
		
		for (int i = 0; i < seq.length; i++)
		{
			output[i] = transformIndex(seq[i]);
		}
		
		return new String(output);
	}
	
	//------------------------------------------------------------
	
	public char getRandomChar ()
	{
		Random rng = new Random();
		
		int index = (int) (rng.nextDouble() * vocabulary.length);
		
		if (index >= vocabulary.length) { index = vocabulary.length - 1; }
		
		return vocabulary[index];
	}
	
	//------------------------------------------------------------
	
	public char[] generateDefaultCharacterSet()
	{
		List<Character> validChars = new LinkedList<>();
		for(char c='a'; c<='z'; c++) validChars.add(c);
		for(char c='A'; c<='Z'; c++) validChars.add(c);
		for(char c='0'; c<='9'; c++) validChars.add(c);
		char[] temp = {'!', '&', '(', ')', '?', '-', '\'', '"', ',', '.', ':', ';', ' ', '\n', '\t'};
		for( char c : temp ) validChars.add(c);
		char[] out = new char[validChars.size()];
		int i=0;
		for( Character c : validChars ) out[i++] = c;
		return out;
	}
	
	
}
