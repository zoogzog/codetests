package codetest.lstm.text;

public class DictionaryItem 
{
	//---- The word itself
	public String word;
	
	//---- Index in the dictionary
	public int wordIndex;
	
	//---- Part of speech of this word
	public int wordPOS;
	
	//---- Label of semantic similarity
	public int wordLabel;
	
	//---- Frequency
	public double wordFrequency;
	
	public DictionaryItem ()
	{
		
	}
	
	public DictionaryItem (String word, int wordIndex, int wordPOS, int wordLabel)
	{
		this.word = word;
		this.wordIndex = wordIndex;
		this.wordPOS = wordPOS;
		this.wordLabel = wordLabel;
	}
}