package codetest.lstm.text;

import codetest.lstm.data.SequenceStorage;

public interface TextTransformer 
{
	public void transform (String path, SequenceStorage storage);
	
	public String transformIndexSequence (int[] seq);
	
	public int getDim ();
	
}
