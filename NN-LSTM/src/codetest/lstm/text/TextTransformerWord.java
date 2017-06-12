package codetest.lstm.text;

import java.io.FileNotFoundException;

import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class TextTransformerWord 
{
	private  Word2Vec transformer;

	private final String DEFAULT_CORPUS_PATH = "Shakespeare.txt";

	public TextTransformerWord ()
	{

	}

	public void loadTransformer (int dimOUT)
	{
		loadTransformer(DEFAULT_CORPUS_PATH, dimOUT);
	}

	public void loadTransformer (String path, int dimOUT)
	{
		try 
		{
			SentenceIterator iter = new BasicLineIterator(path);
			TokenizerFactory t = new DefaultTokenizerFactory();
			t.setTokenPreProcessor(new CommonPreprocessor());

			transformer = new Word2Vec.Builder()
	                .minWordFrequency(5)
	                .iterations(1)
	                .layerSize(dimOUT)
	                .seed(42)
	                .windowSize(5)
	                .iterate(iter)
	                .tokenizerFactory(t)
	                .build();


			transformer.fit();

		} 
		catch (FileNotFoundException e)
		{

			e.printStackTrace();
		}
	}

	public String[] getVocabulary ()
	{
		VocabCache <VocabWord> vocab = transformer.getVocab();
		
		String[] output = new String[vocab.numWords()];
		
		for (int i = 0; i < vocab.numWords(); i++)
		{
			output[i] = vocab.wordAtIndex(i);
		}
		
		return output;
	}
	
	public double[] encode (String word)
	{
		return transformer.getWordVector(word);
	}
	
	public String decode (double[] fv)
	{
		INDArray input = Nd4j.create(fv);
		
		String word =transformer.wordsNearest(input, 1).iterator().next();
		
		return word;
	}

}
