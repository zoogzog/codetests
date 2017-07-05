package codetest.lstm.nn;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;

import codetest.lstm.text.TextTokenizer;
import codetest.lstm.tokenizer.CustomTokenizerFactory;

public class NeuralNetworkWTV 
{
	private Word2Vec vec = null;

	private boolean isTrained = false;

	private int LAYER_SIZE = 100;

	public NeuralNetworkWTV ()
	{

	}

	public void setSettings (int layerSize)
	{
		LAYER_SIZE = layerSize;
	}

	//---- Trains word2vec on a corpus, custom tokenizer from stanford NLP 
	//---- package is used here.
	public void train (String path, TextTokenizer tck)
	{
		try
		{


			SentenceIterator iter = new BasicLineIterator(path);
			TokenizerFactory t = new CustomTokenizerFactory(tck);

			vec = new Word2Vec.Builder()
					.minWordFrequency(5)
					.iterations(1)
					.layerSize(LAYER_SIZE)
					.seed(42)
					.windowSize(5)
					.iterate(iter)
					.tokenizerFactory(t)
					.build();


			vec.fit();

			isTrained = true;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	//------------------------------------------------------

	public void load (String path)
	{
		if (!new File(path).exists()) { return; }


		try 
		{
			WordVectorSerializer.readWord2Vec(new File(path));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void save (String path)
	{
		if (!isTrained) { return; }

		WordVectorSerializer.writeWord2Vec(vec, new File(path));
	}

	//------------------------------------------------------s

	public double[] getVector (String word)
	{
		if (!isTrained) { return null; }

		return vec.getWordVector(word);
	}

	public INDArray getVectorArray (String word)
	{
		if (!isTrained) { return null; }

		return vec.getWordVectorMatrix(word);
	}

	public String[] getWordNearest (String word)
	{
		if (!isTrained) { return null; }

		Collection <String> tmp = vec.wordsNearest(word, 10);


		return tmp.toArray(new String[tmp.size()]);
	}

	public String[] getWordNeared (INDArray vector)
	{
		if (!isTrained) { return null; }

		Collection <String> tmp = vec.wordsNearest(vector, 10);

		return tmp.toArray(new String[tmp.size()]);
	}

	//------------------------------------------------------

	/**
	 * Obtain vocabulary built during running of the w2v training procedure.
	 * @return
	 */
	public String[] getVocabulary ()
	{
		if (!isTrained) { return null; }

		VocabCache <VocabWord> vocab = vec.getVocab();

		String[] output = new String[vocab.numWords()];

		for (int k = 0; k < output.length; k++)
		{
			output[k] = vocab.wordAtIndex(k);
		}

		return output;

	}
}
