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

import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import codetest.lstm.data.SequenceStorage;

public class FileTransformerWord implements FileTransformer
{
	private TextTransformerWord txtTransformer;
	
	private String[] vocabulary = null;
	
	private Map<String,Integer> dictionary = new HashMap<>();
	
	private final int DEFAULT_DIM = 100;
	
	public FileTransformerWord() 
	{
		txtTransformer = new TextTransformerWord();
	}
	
	
	@Override
	public void transform(String path, SequenceStorage storage) 
	{

		try
		{
			if( !new File(path).exists()) { return; }
	
			//---- First we generate representation for each word via word2vec transformer
			txtTransformer.loadTransformer(DEFAULT_DIM);
			
			//---- Next, we fill in the vocabulary
			vocabulary = txtTransformer.getVocabulary();
			
			for (int i = 0; i < vocabulary.length; i++)
			{
				dictionary.put(vocabulary[i], i);
			}
			
			Vector <Integer> mainSequence = new Vector <Integer>();
			//---- Now extract the main sequence 
			
			TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
			tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
			
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			String line = "";
			
			while ((line = br.readLine()) != null)
			{
				 String str = tokenizerFactory.getTokenPreProcessor().preProcess(line);
				 String[] tokenList = str.split(" ");
				 
				 for (int i = 0; i < tokenList.length; i++)
				 {
					 if (dictionary.containsKey(tokenList[i])) { mainSequence.addElement(dictionary.get(tokenList[i])); }
				 }
			}
			
			storage.allocateMemory(mainSequence.size());
			storage.setSequenceMax(vocabulary.length, DEFAULT_DIM, DEFAULT_DIM);
			
			for (int k = 0; k < mainSequence.size(); k++)
			{
				storage.set(k, mainSequence.get(k));
			}
			
			mainSequence.clear();
			
		}
		catch (Exception e) { e.printStackTrace();}
	}

	@Override
	public String transformIndexSequence(int[] seq) 
	{
		String output= "";
		
		for (int i = 0; i < seq.length; i++)
		{
			if (seq[i] >= 0 && seq[i] < vocabulary.length)
			{
				output += vocabulary[seq[i]] + " ";
			}
		}
		
		
		return output;
	}


	@Override
	public int decode(double[] outputNN) 
	{
		String word = txtTransformer.decode(outputNN);
		
		if (dictionary.containsKey(word)) { return dictionary.get(word); }
		
		return -1;
	}
	
	@Override
	public double[] endocde (int value)
	{
		if (value >= 0 && value < vocabulary.length) 
		{ 
			txtTransformer.encode(vocabulary[value]); 
		}
		
		return new double[DEFAULT_DIM];
	}

}
