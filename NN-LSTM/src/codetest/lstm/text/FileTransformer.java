package codetest.lstm.text;

import codetest.lstm.data.SequenceStorage;

public interface FileTransformer 
{
	public void transform (String path, SequenceStorage storage);
	
	public String transformIndexSequence (int[] seq);
	
	public int decode (double[] outputNN);
	
	public double[] endocde (int value);
}
