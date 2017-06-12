package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TextTokenizer 
{
	private TokenizerModel tokenizerModel = null;
	private Tokenizer tokenizer = null;

	private POSModel posModel = null;
	private POSTaggerME posTagger = null;
	

	public Map <String, String> descriptorPOS = new HashMap <String, String>();

	public Map<String, Integer> dictionaryMap = new HashMap <String, Integer>();
	public String[] dictionaryTable;

	private final String DEFAULT_DICTIONARY_PATH = "./data-nlp/en_US.dic";
	private final String DEFAULT_TOKENIZER_PATH = "data-nlp/en-token.bin";
	private final String DEFAULT_POSMODERL_PATH = "data-nlp/en-pos-maxent.bin";

	//---------------------------------------------------------------------------------------

	private void loadTablePOS ()
	{
		descriptorPOS.put("CC", "Coordinating conjunction");
		descriptorPOS.put("CD", "Cardinal number");
		descriptorPOS.put("DT", "Determiner");
		descriptorPOS.put("EX", "Existential there");
		descriptorPOS.put("FW", "Foreign word");
		descriptorPOS.put("IN", "Preposition or subordinating conjunction");
		descriptorPOS.put("JJ", "Adjective");
		descriptorPOS.put("JJR", "Adjective, comparative");
		descriptorPOS.put("JJS", "Adjective, superlative");
		descriptorPOS.put("LS", "List item marker");
		descriptorPOS.put("MD", "Modal");
		descriptorPOS.put("NN", "Noun, singular or mass");
		descriptorPOS.put("NNS", "Noun, plural");
		descriptorPOS.put("NNP", "Proper noun, singular");
		descriptorPOS.put("NNPS", "Proper noun, plural");
		descriptorPOS.put("PDT", "Predeterminer");
		descriptorPOS.put("POS", "Possessive ending");
		descriptorPOS.put("PRP", "Personal pronoun");
		descriptorPOS.put("PRP$", "Possessive pronoun");
		descriptorPOS.put("RB", "Adverb");
		descriptorPOS.put("RBR", "Adverb, comparative");
		descriptorPOS.put("RBS", "Adverb, superlative");
		descriptorPOS.put("RP", "Particle");
		descriptorPOS.put("SYM", "Symbol");
		descriptorPOS.put("TO", "to");
		descriptorPOS.put("UH", "Interjection");
		descriptorPOS.put("VB", "Verb, base form");
		descriptorPOS.put("VBD", "Verb, past tense");
		descriptorPOS.put("VBG", "Verb, gerund or present participle");
		descriptorPOS.put("VBN", "Verb, past participle");
		descriptorPOS.put("VBP", "Verb, non­3rd person singular present");
		descriptorPOS.put("VBZ", "Verb, 3rd person singular present");
		descriptorPOS.put("WDT", "Wh­determiner");
		descriptorPOS.put("WP", "Wh­pronoun");
		descriptorPOS.put("WP$", "Possessive wh­pronoun");
		descriptorPOS.put("WRB", "Wh­adverb");
	}

	private void loadTableDictionary ()
	{

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(DEFAULT_DICTIONARY_PATH));

			String line = "";

			int index = 0;

			while ((line = br.readLine()) != null)
			{

				String word = "";

				if (line.contains("/")) { word = line.split("/")[0]; }
				else { word = line;}

				word = word.replaceAll(" ", "");
				word = word.toLowerCase();

				dictionaryMap.put(word, index);
				index++;
			}
			
			//---- and EOF as '.'
			dictionaryMap.put(".", index);

			dictionaryTable = new String[dictionaryMap.size()];

			index = 0;

			for (String key :  dictionaryMap.keySet())
			{
				dictionaryTable[index] = key;

				index++;
			}

			br.close();

		}
		catch (Exception e) { e.printStackTrace(); }
	}

	private void loadTokenizer ()
	{
		try
		{
			InputStream modelIn = new FileInputStream(DEFAULT_TOKENIZER_PATH);
			tokenizerModel =  new TokenizerModel(modelIn);
			tokenizer = new TokenizerME(tokenizerModel);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	private void loadPosDetector ()
	{
		try
		{
			InputStream modelPOS = new FileInputStream(DEFAULT_POSMODERL_PATH);
			
			posModel = new POSModel(modelPOS);
			posTagger = new POSTaggerME(posModel);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public TextTokenizer ()
	{
		loadTablePOS();
		loadTableDictionary();
		
		loadTokenizer();
		loadPosDetector();
	}

	public String[] generateTokens (String str, boolean isExtendNegation)
	{
		String[] negationListShort = {"can't", "couldn't", "wouldn't", "shouldn't", "wont", "don't", "doesn't"};
		String[] negationListLong = {"can not", "could not", "would not", "should not", "will not", "do not", "does not"};
		
		String strX = str;
		
		if (isExtendNegation)
		{
			for (int i = 0; i < negationListShort.length; i++)
			{
				strX = strX.replaceAll(negationListShort[i], negationListLong[i]);
			}
		}
		
		
		String tokens[] = tokenizer.tokenize(strX);
		
		return tokens;
	}
	
	public String[] generatePos (String[] tokenList)
	{
		String[] tagT = posTagger.tag(tokenList);
		
		return tagT;
	}
	
	public String generatePos (String token)
	{
		return posTagger.tag(new String[]{token})[0];
	}

	public int[]  getDictionaryIndex (String[] tokenList)
	{
		Vector <Integer> output = new Vector <Integer>();
		
		for (int i = 0; i < tokenList.length; i++)
		{
			String word = tokenList[i].toLowerCase();
			
			int wordIndex = getDictionaryIndex(word);
			
			//---- Try advanced search by attempting to convert a word to dictionary form
			if (wordIndex == -1)
			{

			///	System.out.println(word + " " + wordIndex + " " + descriptorPOS.get(generatePos(word)));
				
			word = getTransformToDictionaryForm(word);
			
			
			wordIndex = getDictionaryIndex(word);
			
			//System.out.println("Tr: " + word + " " + wordIndex);
			
			}
			
			if (wordIndex != -1)
			{
				output.addElement(wordIndex);
			}
		}
		
		int[] outputArray = new int[output.size()];
		
		for (int k = 0; k < outputArray.length; k++) { outputArray[k] = output.get(k); }
		
		return outputArray;
	}
	
	public int getDictionaryIndex (String word)
	{
		if (dictionaryMap.containsKey(word)) { return dictionaryMap.get(word); }
		
		return -1;
	}
	
	private String getTransformToDictionaryForm (String word)
	{
		String wordPos = generatePos(word);
		
		switch (wordPos)
		{
		case "RB": word = getTransformRB(word); break;
		}
		
		return word;
	}
	
	//---- RB: Adverb
	private String getTransformRB (String word)
	{
		if (word.length() > 3) 
		{
			//---- Assume here that adverb and on 'ly'
			word = word.substring(0, word.length() - 2);
		}
		
		return word;
	}
	
	
	public String getDictionaryWord (int index)
	{
		if (index >= 0 && index < dictionaryTable.length)
		{
			return dictionaryTable[index];
		}
		
		return "";
	}
	
	public int getDictionarySize ()
	{
		return dictionaryTable.length;
	}

	public static void main (String[] args)
	{
		try
		{
			TextTokenizer txtdriver = new TextTokenizer();

			String testStr = "Well that’s extremely helpful–those guys  took takes "
					+ "made makes make delivered sure do a great job over there. What it can't does is "
					+ "takes a sentence like this brutally murdered by offensive killers hottest hotter longer sharper";
			String[] tk = txtdriver.generateTokens(testStr, true);

			txtdriver.getDictionaryIndex(tk);


		}
		catch (Exception e) { e.printStackTrace();} 
	}
}
