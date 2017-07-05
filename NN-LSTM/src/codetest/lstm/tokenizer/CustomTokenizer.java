package codetest.lstm.tokenizer;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;

import codetest.lstm.text.TextTokenizer;

public class CustomTokenizer implements Tokenizer 
{
	String[] tcklst = null;
	
	int tckIndex = 0;
	
	
	public CustomTokenizer (String line, TextTokenizer tck)
	{
		tcklst = tck.generateTokens(line, true);
	}


	@Override
	public boolean hasMoreTokens() 
	{
		if (tckIndex < tcklst.length) { return true; }
		return false;
	}

	@Override
	public int countTokens() 
	{
		return tcklst.length;
	}

	@Override
	public String nextToken() 
	{
		String output = tcklst[tckIndex];
		tckIndex++;
		return output;
	}

	@Override
	public List<String> getTokens() 
	{
		List<String> output = new ArrayList<String>();
		
		for (int k = 0; k < tcklst.length; k++)
		{
			output.add(tcklst[k]);
		}
		
		return output;
	}

	@Override
	public void setTokenPreProcessor(TokenPreProcess tokenPreProcessor) 
	{
		
	}
}