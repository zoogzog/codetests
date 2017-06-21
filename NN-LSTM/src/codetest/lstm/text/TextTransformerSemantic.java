package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
	}
	
	//---- Extracts tokens and fills the dictionary
	private void transformTokenize (String path, Vector <String> tokenSequence)
	{
		try
		{			
			TextTokenizer tokenizer = new TextTokenizer(true, true, false);

			BufferedReader bfr = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF8"));

			String line = "";

			while ((line = bfr.readLine()) != null)
			{
				String[] tk = tokenizer.generateTokens(line, true);
				String[] tkpos = tokenizer.generatePos(tk);
				
				for (int i = 0; i < tkpos.length; i++)
				{
					//---- FIXME sometimes tokenizer generates strange output
					//---- Cause not know. Possible - encoding, punctuation in words
					int index = tokenizer.getPosIndex(tkpos[i]);
					
					if (index != -1)
					{
						tokenSequence.addElement(tk[i]);
						
						//---- If word is not in the dictionary
						if (!dictionary.isWordInDictionary(tk[i]))
						{
							dictionary.addItem(tk[i], tokenizer.getPosIndex(tkpos[i]));
						}
					}
				}
			}
			
			bfr.close();

		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	//---- This method received sequence of tokens (clean text) outputs list of 
	//---- INDArrays which are vector representations of tokens
	//---- FIXME currently i could not find an easy way to directly insert tokens into
	//---- FIXME dl4j w2v class. Probably some interface implementation is needed 
	//---- FIXME to handle this. Thus the token list is saved to a temporary file
	//---- FIXME then it is given to w2v to parse
	private void transformWordToVector (Vector <String> tokenSequence, List <INDArray> fvlist)
	{
		
	}
	
	//---- This method obtains a list of vectorized words, classifies them into K classes
	//---- Saves all labels of each word (token) to the dictionary
	private void transformClusterize (List<INDArray> fvlist)
	{
		ClassifierKMeans ckm = new ClassifierKMeans();
		
	}
	
	@Override
	public void transform(String path, SequenceStorage storage) 
	{
		//---- GLOBAL PARAMETER
		int CLUSTER_COUNT = 100;
		
		Vector <String> tokenSequence = new Vector<String>();
		List <INDArray> fvlist = new ArrayList<INDArray>();
		
		transformTokenize(path, tokenSequence);
		
		System.out.println("Dictionary: " + dictionary.getSize() + " Sequence: " + tokenSequence.size());
		
		transformWordToVector (tokenSequence, fvlist);
		
	}

	@Override
	public String transformIndexSequence(int[] seq) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDim() 
	{
		// TODO Auto-generated method stub
		return 0;
	}


	public static void main (String[] args)
	{
		String path = "data-text/fiction.txt";

		TextTransformerSemantic txt = new TextTransformerSemantic();
		txt.transform(path, null);
	}
}
