package codetest.lstm.tokenizer;

import java.io.InputStream;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import codetest.lstm.text.TextTokenizer;

public class CustomTokenizerFactory implements TokenizerFactory
{
	private TextTokenizer tokenizer = null;
	
	
	public  CustomTokenizerFactory (TextTokenizer tckdriver)
	{
		tokenizer = tckdriver;
	}
	
	
	
	@Override
	public Tokenizer create(String toTokenize) 
	{
		CustomTokenizer tck = new CustomTokenizer(toTokenize, tokenizer);
		return tck;
	}

	@Override
	public Tokenizer create(InputStream toTokenize) 
	{
		
		return null;
	}

	@Override
	public void setTokenPreProcessor(TokenPreProcess preProcessor) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public TokenPreProcess getTokenPreProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

}
