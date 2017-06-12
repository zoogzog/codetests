package codetest.lstm.text;

public interface FileTransformer 
{
	public void transform (String path, SequenceStorage storage);
	
	public String transformIndexSequence (int[] seq);
}
