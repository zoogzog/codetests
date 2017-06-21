package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.sun.org.glassfish.gmbal.Description;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TextTokenizer 
{
	//---- Paths to resources
	private final String DEFAULT_DICTIONARY_PATH = "data-nlp/en_US.dic";
	private final String DEFAULT_TOKENIZER_PATH = "data-nlp/en-token.bin";
	private final String DEFAULT_POSMODERL_PATH = "data-nlp/en-pos-maxent.bin";

	//---- Tokenizer model and driver
	private TokenizerModel tokenizerModel = null;
	private Tokenizer tokenizer = null;

	//---- Part of speech tagger and pos description table
	private POSModel posModel = null;
	private POSTaggerME posTagger = null;

	private Map <String, String> posDescriptionTable = new HashMap <String, String>();
	private Map <String, Integer> posTableLookupStr = new HashMap <String, Integer> ();
	private String[] posTableLookupInt;

	//---- Classes related to dictionary
	public Map<String, Integer> dictionaryMap = new HashMap <String, Integer>();
	public String[] dictionaryTable;

	//---- Boolean flags to describe what tables/modelsare loaded
	private boolean isLoadedTokenizer = false;
	private boolean isLoadedPosTagger = false;
	private boolean isLoadedDictionary = false;

	public Map <String, String> posTester = new HashMap <String, String>();
	
	//---------------------------------------------------------------------------------------

	private void loadTablePos ()
	{
		posDescriptionTable.put("CC", "Coordinating conjunction");
		posDescriptionTable.put("CD", "Cardinal number");
		posDescriptionTable.put("DT", "Determiner");
		posDescriptionTable.put("EX", "Existential there");
		posDescriptionTable.put("FW", "Foreign word");
		posDescriptionTable.put("IN", "Preposition or subordinating conjunction");
		posDescriptionTable.put("JJ", "Adjective");
		posDescriptionTable.put("JJR", "Adjective, comparative");
		posDescriptionTable.put("JJS", "Adjective, superlative");
		posDescriptionTable.put("LS", "List item marker");
		posDescriptionTable.put("MD", "Modal");
		posDescriptionTable.put("NN", "Noun, singular or mass");
		posDescriptionTable.put("NNS", "Noun, plural");
		posDescriptionTable.put("NNP", "Proper noun, singular");
		posDescriptionTable.put("NNPS", "Proper noun, plural");
		posDescriptionTable.put("PDT", "Predeterminer");
		posDescriptionTable.put("POS", "Possessive ending");
		posDescriptionTable.put("PRP", "Personal pronoun");
		posDescriptionTable.put("PRP$", "Possessive pronoun");
		posDescriptionTable.put("RB", "Adverb");
		posDescriptionTable.put("RBR", "Adverb, comparative");
		posDescriptionTable.put("RBS", "Adverb, superlative");
		posDescriptionTable.put("RP", "Particle");
		posDescriptionTable.put("SYM", "Symbol");
		posDescriptionTable.put("TO", "to");
		posDescriptionTable.put("UH", "Interjection");
		posDescriptionTable.put("VB", "Verb, base form");
		posDescriptionTable.put("VBD", "Verb, past tense");
		posDescriptionTable.put("VBG", "Verb, gerund or present participle");
		posDescriptionTable.put("VBN", "Verb, past participle");
		posDescriptionTable.put("VBP", "Verb, non­3rd person singular present");
		posDescriptionTable.put("VBZ", "Verb, 3rd person singular present");
		posDescriptionTable.put("WDT", "Wh­determiner");
		posDescriptionTable.put("WP", "Wh­pronoun");
		posDescriptionTable.put("WP$", "Possessive wh­pronoun");
		posDescriptionTable.put("WRB", "Whadverb");
		
		posTester.put("CC", "or");
		posTester.put("CD", "5");
		posTester.put("DT", "a");
		posTester.put("EX", "there");
		posTester.put("FW", "asshole");
		posTester.put("IN", "down");
		posTester.put("JJ", "happy");
		posTester.put("JJR", "happier");
		posTester.put("JJS", "happiest");
		posTester.put("LS", "#ls#");
		posTester.put("MD", "can");
		posTester.put("NN", "lama");
		posTester.put("NNS", "lamas");
		posTester.put("NNP", "Jack");
		posTester.put("NNPS", "Jacks");
		posTester.put("PDT", "#pdt#");
		posTester.put("POS", "#pos#");
		posTester.put("PRP", "it");
		posTester.put("PRP$", "your");
		posTester.put("RB", "slowly");
		posTester.put("RBR", "slowlier");
		posTester.put("RBS", "slowliest");
		posTester.put("RP", "off");
		posTester.put("SYM", "#");
		posTester.put("TO", "to");
		posTester.put("UH", "Interjection");
		posTester.put("VB", "play");
		posTester.put("VBD", "plays");
		posTester.put("VBG", "playing");
		posTester.put("VBN", "played");
		posTester.put("VBP", "shine");
		posTester.put("VBZ", "shines");
		posTester.put("WDT", "which");
		posTester.put("WP", "whose");
		posTester.put("WP$", "what");
		posTester.put("WRB", "where");
		
		//---- Added
		posDescriptionTable.put("EOS", "End of sentence");
		
		posTableLookupInt = new String [posDescriptionTable.size()];
		
		int index = 0;
		for (String key : posDescriptionTable.keySet())
		{
			posTableLookupStr.put(key, index);
			posTableLookupInt[index] = key;
			index++;
		}
	}

	private void loadDictionary ()
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

			isLoadedDictionary = true;
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

			isLoadedTokenizer = true;
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	private void loadPosTagger ()
	{
		try
		{
			loadTablePos();

			InputStream modelPOS = new FileInputStream(DEFAULT_POSMODERL_PATH);

			posModel = new POSModel(modelPOS);
			posTagger = new POSTaggerME(posModel);

			isLoadedPosTagger = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------------------

	public TextTokenizer ()
	{
		loadTokenizer();
		loadPosTagger();
		loadDictionary();
	}

	public TextTokenizer (boolean loadTokenizer, boolean loadPosTagger, boolean loadDictionary)
	{
		if (loadTokenizer) { loadTokenizer(); }
		if (loadPosTagger) { loadPosTagger(); }
		if (loadDictionary) { loadDictionary(); }
	}

	//---------------------------------------------------------------------------------------

	public String[] generateTokens (String str, boolean isExtendNegation)
	{
		if (!isLoadedTokenizer) { return null; }

		String[] negationListShort = {"can't", "couldn't", "wouldn't", "shouldn't", "wont", "don't", "doesn't"};
		String[] negationListLong = {"can not", "could not", "would not", "should not", "will not", "do not", "does not"};

		String strX = str;

		if (isExtendNegation)
		{
			for (int i = 0; i < negationListShort.length; i++)
			{
				strX = strX.replaceAll(negationListShort[i], negationListLong[i]);
				strX = strX.replaceAll("--", " ");
			}
		}


		String tokens[] = tokenizer.tokenize(strX);

		return tokens;
	}

	public String[] generatePos (String[] tokenList)
	{
		if (!isLoadedPosTagger) { return null; }

		String[] tagT = posTagger.tag(tokenList);
		
		//---- Is there any more elegant way to insert EOS?
		for (int i = 0; i < tagT.length; i++)
		{
			if (tokenList[i].equals(".") || tokenList[i].equals("!") || tokenList[i].equals("?")) { tagT[i] = "EOS"; }
		}

		return tagT;
	}

	public String generatePos (String token)
	{
		if (!isLoadedPosTagger) { return null; }

		return posTagger.tag(new String[]{token})[0];
	}

	public String getPosDescriptor (String pos)
	{
		if (posDescriptionTable.containsKey(pos)) { return posDescriptionTable.get(pos); }
		return "";
	}
	
	public int getPosIndex (String pos)
	{
		if (posTableLookupStr.containsKey(pos)) { return posTableLookupStr.get(pos); }
		return -1;
	}
	
	public String getPosString (int index)
	{
		if (index >= 0 && index < posTableLookupInt.length)
		{
			return posTableLookupInt[index];
		}
		
		return "";
	}
	
	public int getPosTableSize ()
	{
		return posTableLookupInt.length;
	}
	
	
	//---------------------------------------------------------------------------------------
		
	
	//---------------------------------------------------------------------------------------

	public int[]  getDictionaryIndex (String[] tokenList)
	{
		if (!isLoadedDictionary) { return null; }

		Vector <Integer> output = new Vector <Integer>();

		for (int i = 0; i < tokenList.length; i++)
		{
			String word = tokenList[i].toLowerCase();

			int wordIndex = getDictionaryIndex(word);

			//---- Try advanced search by attempting to convert a word to dictionary form
			if (wordIndex == -1)
			{
				word = getTransformToDictionaryForm(word);
				wordIndex = getDictionaryIndex(word);
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

	//---------------------------------------------------------------------------------------
	//---- FIXME below are method for transforming words into their dictionary form to
	//---- reduce the size of the vocabulary
	//---------------------------------------------------------------------------------------
	
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

	//---------------------------------------------------------------------------------------

	public String getDictionaryWord (int index)
	{
		if (!isLoadedDictionary) { return ""; }
		
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

	//---------------------------------------------------------------------------------------

	//---- Generates a corpus related dictionary, saves it into a file
	public void buildLocalDictionary (String pathCorpus, String pathDictionary)
	{

	}

	//---------------------------------------------------------------------------------------

	public static void main (String[] args)
	{
		try
		{
			TextTokenizer txtdriver = new TextTokenizer(true, true, false);

			String testStr = 
"A towel, it says, is about the most  massively  useful  thing  an " +
"interstellar  hitch hiker can have. Partly it has great practical " + 
"value - you can wrap it around you for warmth as you bound across " + 
"the cold moons of Jaglan Beta; you can lie on it on the brilliant " + 
"marble-sanded beaches of Santraginus V, inhaling  the  heady  sea " + 
"vapours;  you can sleep under it beneath the stars which shine so " + 
"redly on the desert world of Kakrafoon; use it  to  sail  a  mini" + 
"raft  down  the slow heavy river Moth; wet it for use in hand-to- " + 
"hand-combat; wrap it round your head to ward off noxious fumes or " + 
"to  avoid  the  gaze of the Ravenous Bugblatter Beast of Traal (a " + 
"mindboggingly stupid animal, it assumes that if you can't see it, " + 
"it  can't  see  you - daft as a bush, but very ravenous); you can " + 
"wave your towel in emergencies  as  a  distress  signal,  and  of " + 
"course  dry  yourself  off  with it if it still seems to be clean " + 
"enough.";
			String[] tk = txtdriver.generateTokens(testStr, true);

			String[] pos = txtdriver.generatePos(tk);
			
			for (int i = 0; i < pos.length; i++)
			{
				int posIndex = txtdriver.getPosIndex(pos[i]);
				
				if (posIndex != -1)
				{
					System.out.println(tk[i] + " " + pos[i] + " " + posIndex);
				}
			}


		}
		catch (Exception e) { e.printStackTrace();} 
	}
}
