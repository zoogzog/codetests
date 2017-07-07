package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;

import codetest.lstm.data.SequenceStorage;
import codetest.lstm.nn.ClassifierKMeans;
import codetest.lstm.nn.NeuralNetworkWTV;

/**
 * Vocabulary generation: Read txt -> tokenize -> word2vec -> k-means clustering (semantic clusters) -> vocabulary
 * Sequence generation: Read txt -> tokenize -> vocabulary, get semantic class -> store semantic class in sequence
 * @author Grushnikov Andrey
 */
public class TextTransformerSemantic implements TextTransformer
{
	private Vocabulary vocabulary;

	private int KM_CLUSTER_COUNT = 100;
	private int KM_ITERATION_MAX = 100;
	private int WTV_DIMENSION = 100;

	private boolean isGenerateVocabulary = true;

	//---- Default vocabulary path
	private final String VOCABULARY_PATH = "dict.txt";
	
	public TextTransformerSemantic ()
	{
		vocabulary = new Vocabulary();
	}

	//-----------------------------------------------------------------------------
	
	@Override
	public void transform(String path, SequenceStorage storage) 
	{
		try
		{
			if (isGenerateVocabulary) { vocabularyGenerate(path); }
			else 
			{
				//---- Check if the vocabulary file specified. If  file exists, load 
				//---- vocabulary from that file, if not generate vocabulary from the corpus
				if ((new File(VOCABULARY_PATH).exists())) { vocabularyLoad(path);}
				else { vocabularyGenerate(path); }
			}
			

			//---- Parse file, build sequence
			Vector <Integer> sequence  = transformText(path);

			//---- Allocate memory and specify max value in the sequence
			storage.allocateMemory(sequence.size());
			storage.setSequenceDim(KM_CLUSTER_COUNT + 1);
			
			//---- Store sequence in the storage
			for (int i = 0; i < sequence.size(); i++) { storage.set(i, sequence.get(i)); }
	
		}
		catch (Exception e) { e.printStackTrace();}

	}

	public Vector <Integer> transformText (String path)
	{
		try
		{
			Vector <Integer> output = new Vector<Integer>();
			
			TextTokenizer tck = new TextTokenizer(true, true, false);
			
			vocabulary.labelQueryInit(KM_CLUSTER_COUNT);
			
			BufferedReader bfr = new BufferedReader(new FileReader(path));
			
			String str = "";
			
			while ((str = bfr.readLine()) != null)
			{
				String[] tcklst = tck.generateTokens(str, true);
				
				for (int i = 0; i < tcklst.length; i++)
				{
					//--- EOS is not in the vocabulary, handle it separately
					if (tcklst[i].equals("eos")) 
					{ 
						output.addElement(KM_CLUSTER_COUNT);
					}
					else
					{
						VocabularyItem item = vocabulary.getItem(tcklst[i]);
						if (item != null) { output.addElement(item.wordLabel); }
					}
				}
			}
			
			bfr.close();
			
			return output;
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}
	
	
	@Override
	public String transformIndexSequence(int[] seq) 
	{
		//---- Check if the vocabulary is initialized 
		//---- If not, then try to load it using the default path
		//---- If can not load, return empty string
		if (!vocabulary.getIsLoaded())
		{
			if (new File(VOCABULARY_PATH).exists()) { vocabularyLoad(VOCABULARY_PATH); }
			else { return ""; }
		}
		
		String output = "";

		for (int i = 0; i < seq.length; i++)
		{
			int label = seq[i];

			if (label == KM_CLUSTER_COUNT) { output += "."; }
			else
			{
				int[] queryResult = vocabulary.labelQueryWordList(label);

				int r = (int) (Math.random() * queryResult.length);

				output += vocabulary.getItem(queryResult[r]).word + " ";
			}

		}
		return output;
	}

	@Override
	public int getDim() 
	{
		// TODO Auto-generated method stub
		return KM_CLUSTER_COUNT + 1;
	}

	//-----------------------------------------------------------------------------

	//---- Generate vocabulary of words from corpus
	public void vocabularyGenerate (String path)
	{
		TextTokenizer tck = new TextTokenizer(true, true, false);

		List <INDArray> fvlist = new ArrayList<INDArray>();

		NeuralNetworkWTV wtv = new NeuralNetworkWTV();
		wtv.setSettings(WTV_DIMENSION);
		wtv.train(path, tck);

		String[] dict = wtv.getVocabulary();

		for (int i = 0; i < dict.length; i++)
		{
			fvlist.add(wtv.getVectorArray(dict[i]));
		}

		ClassifierKMeans ckm = new ClassifierKMeans();
		ckm.run(KM_CLUSTER_COUNT, KM_ITERATION_MAX, fvlist);

		for (int i = 0; i < dict.length; i++)
		{
			String word = dict[i];
			int wordID = i;
			int wordPos = 0;
			int wordLabel = ckm.getClass(fvlist.get(i));

			vocabulary.addItem(new VocabularyItem(word, wordID, wordPos, wordLabel));
		}
	}

	//-----------------------------------------------------------------------------
	
	/**
	 * Save the generated vocabulary to a specified path
	 * @param path
	 */
	public void vocabularySave (String path)
	{
		vocabulary.save(path);
	}
	
	/**
	 * Load previously generated vocabulary, specified by its path
	 * @param path
	 */
	public void vocabularyLoad (String path)
	{
		vocabulary.load(path);
		vocabulary.labelQueryInit(KM_CLUSTER_COUNT);
	}
	
}
