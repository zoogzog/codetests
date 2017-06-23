package codetest.lstm.text;

import java.util.Vector;

public class SemanticClass 
{
	//---- A collection of words, that belong to this class;
	private Vector <Integer> wordlist;


	public SemanticClass ()
	{
		wordlist = new Vector <Integer> ();
	}

	public int getSize ()
	{
		return wordlist.size();
	}

	public void addItem (int wordID)
	{
		wordlist.addElement(wordID);
	}

	public int getItem (int index)
	{
		if (index >= 0 && index < wordlist.size())
		{
			return wordlist.get(index);
		}

		return -1;
	}
	
	public int[] getItemList ()
	{
		int[] output = new int[wordlist.size()];
		
		for (int i = 0; i < wordlist.size(); i++)
		{
			output[i] = wordlist.get(i);
		}
		
		return output;
	}
}
