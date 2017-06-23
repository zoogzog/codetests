package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class TextTransformerSemantic implements TextTransformer
{
	private Dictionary dictionary;


	public TextTransformerSemantic ()
	{
		dictionary = new Dictionary();
		dictionary.load("dict.txt");
		dictionary.labelQueryInit(101);
	}



	@Override
	public void transform(String path, SequenceStorage storage) 
	{
		try
		{
		/*	List <INDArray> fvlist = new ArrayList<INDArray>();


			NeuralNetworkWTV wtv = new NeuralNetworkWTV();
			wtv.train(path);

			String[] dict = wtv.getVocabulary();

			for (int i = 0; i < dict.length; i++)
			{
				fvlist.add(wtv.getVectorArray(dict[i]));
			}

			ClassifierKMeans ckm = new ClassifierKMeans();
			ckm.run(100, 100, fvlist);

			for (int i = 0; i < dict.length; i++)
			{
				String word = dict[i];
				int wordID = i;
				int wordPos = 0;
				int wordLabel = ckm.getClass(fvlist.get(i));

				dictionary.addItem(new DictionaryItem(word, wordID, wordPos, wordLabel));
			}*/


			//--- Check dictionary
			//dictionary.save("dict.txt");

			dictionary.load("dict.txt");
			
			dictionary.labelQueryInit(100);
			
			System.out.println("DICT SIZE: " + dictionary.getSize());
			
			SentenceIterator iter = new BasicLineIterator(path);
			// Split on white spaces in the line to get words
			TokenizerFactory t = new DefaultTokenizerFactory();
			t.setTokenPreProcessor(new CommonPreprocessor());
		
			Vector <Integer> ilist = new Vector<Integer>();
			
			while (iter.hasNext())
			{
				String line = iter.nextSentence();
				
				line = line.replace(".", " EOS ");
				
				System.out.println(">>: " + line);

				List<String> tknlst = t.create(line).getTokens();
				
				for (int k = 0; k < tknlst.size(); k++)
				{
					if (tknlst.get(k).equals("eos")) 
					{ 
						//System.out.print("[" + tknlst.get(k) + "," + 101 + "];"); 
						ilist.addElement(100);
					}
					else
					{
						DictionaryItem item = dictionary.getItem(tknlst.get(k));
						if (item == null) { System.out.println("WTF: " + tknlst.get(k)); }
						else 
						{
							//System.out.print("[" + tknlst.get(k) + "," + item.wordLabel + "];");
							ilist.addElement(item.wordLabel);
						}
					
					}
				}
				
			//	System.out.println();
			//	System.out.println("-------------------------");
			}
			
			storage.allocateMemory(ilist.size());
			storage.setSequenceDim(101);
			
			for (int i = 0; i < ilist.size(); i++)
			{
				storage.set(i, ilist.get(i));
			}

		}
		catch (Exception e) { e.printStackTrace();}

	}

	@Override
	public String transformIndexSequence(int[] seq) 
	{
		String output = "";
		
		for (int i = 0; i < seq.length; i++)
		{
			int label = seq[i];
			
			if (label == 100) { output += "."; }
			else
			{
				int[] queryResult = dictionary.labelQueryWordList(label);
				
				int r = (int) (Math.random() * queryResult.length);
				
				output += dictionary.getItem(queryResult[r]).word + " ";
			}
			
		}
		return output;
	}

	@Override
	public int getDim() 
	{
		// TODO Auto-generated method stub
		return 101;
	}


	public static void main (String[] args)
	{
		String path = "data-text/fiction.txt";

		TextTransformerSemantic txt = new TextTransformerSemantic();
		txt.transform(path, null);
	}
}
