package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author Grushnikov Andrey
 *
 */
public class Vocabulary 
{
	//---- This dictionary is used for Word Index -> Word relation
	private Vector <VocabularyItem> vocabulary;
	
	private Map <String, Integer> vocabularyWordSearch;
	
	private Vector<SemanticClass> vocabularyLabelSearch;
	
	boolean isLabelSearchInit = false;
	
	public Vocabulary ()
	{
		vocabulary = new Vector <VocabularyItem> ();
		vocabularyWordSearch = new HashMap <String, Integer>();
	}
	

	//------------------------------------------------------
	
	public void addItem (VocabularyItem item)
	{
		vocabulary.addElement(item);
		
		int maxIndex = vocabulary.size();
		
		vocabularyWordSearch.put(item.word, maxIndex);
	}
	
	public void addItem (String word, int wordPos)
	{
		int id = vocabulary.size();
		
		addItem(new VocabularyItem(word, id, wordPos, 0));
	}
	
	public void setItem (VocabularyItem item, int index)
	{
		if (index >= 0 && index < vocabulary.size()) 
		{
			vocabulary.set(index, item);
		}
	}
	
	//------------------------------------------------------
	
	public void labelQueryInit (int labelcount)
	{
		vocabularyLabelSearch = new Vector<SemanticClass> ();
		
		for (int i = 0; i < labelcount; i++)
		{
			vocabularyLabelSearch.addElement(new SemanticClass());
		}
		
		for (int i = 0; i < vocabulary.size(); i++)
		{
			int label = vocabulary.get(i).wordLabel;
			int wordID = vocabulary.get(i).wordIndex;
			
			vocabularyLabelSearch.get(label).addItem(wordID);
		}
	}
	
	public int[] labelQueryWordList (int label)
	{
		return vocabularyLabelSearch.get(label).getItemList();
	}
	
	//------------------------------------------------------
	
	public boolean isWordInDictionary (String word)
	{
		if (vocabularyWordSearch.containsKey(word)) { return true; }
		return false;
	}
	
	//------------------------------------------------------
	
	public VocabularyItem getItem (int index)
	{
		if (index >= 0 && index < vocabulary.size())
		{
			return vocabulary.get(index);
		}
		
		return null;
	}
	
	public VocabularyItem getItem (String word)
	{
		if (vocabularyWordSearch.containsKey(word))
		{
			int index = vocabularyWordSearch.get(word);
			
			return getItem(index);
		}
		
		return null;
	}
	
	public int getSize()
	{
		return vocabulary.size();
	}

	//------------------------------------------------------
	
	/**
	 * Save the build dictionary to a file
	 * @param path
	 */
	public void save (String path)
	{
		try
		{
			BufferedWriter bfw = new BufferedWriter(new FileWriter(path));
			
			for (int i = 0; i< vocabulary.size(); i++)
			{
				String outword = vocabulary.get(i).word;
				int outwordid = vocabulary.get(i).wordIndex;
				int outwordpos = vocabulary.get(i).wordPOS;
				int outwordlabel = vocabulary.get(i).wordLabel;
				
				bfw.write(outword + ";" + outwordid + ";" + outwordpos + ";" + outwordlabel + ";\n");
			}
			
			bfw.flush();
			bfw.close();
		}
		catch (Exception e)
		{
			
		}
	}
	
	/**
	 * Load the previously generated dictionary
	 * @param path
	 */
	public void load (String path)
	{
		vocabulary.clear();
		vocabularyWordSearch.clear();
		
		try
		{
			BufferedReader bfr = new BufferedReader(new FileReader(path));
			
			String line = "";
					
			while ((line = bfr.readLine()) != null)
			{
				//----FIXME correctness of the data is not checked
				String[] data = line.split(";");
				
				String inword = data[0];
				int inwordid= Integer.parseInt(data[1]);
				int inwordpos = Integer.parseInt(data[2]);
				int inwordlabel = Integer.parseInt(data[3]);
				
				//System.out.println("DICT: " + inword + " " + inwordid + " " + inwordlabel);
				
				vocabulary.add(new VocabularyItem(inword, inwordid, inwordpos, inwordlabel));
				vocabularyWordSearch.put(inword, inwordid);
			}
			
		
			
			bfr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//------------------------------------------------------
}
