package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Dictionary 
{
	//---- This dictionary is used for Word Index -> Word relation
	private Vector <DictionaryItem> dictionary;
	
	private Map <String, Integer> dictionaryWordSearch;
	
	private Vector<SemanticClass> dictionaryLabelSearch;
	
	public Dictionary ()
	{
		dictionary = new Vector <DictionaryItem> ();
		dictionaryWordSearch = new HashMap <String, Integer>();
	}
	

	//------------------------------------------------------
	
	public void addItem (DictionaryItem item)
	{
		dictionary.addElement(item);
		
		int maxIndex = dictionary.size();
		
		dictionaryWordSearch.put(item.word, maxIndex);
	}
	
	public void addItem (String word, int wordPos)
	{
		int id = dictionary.size();
		
		addItem(new DictionaryItem(word, id, wordPos, 0));
	}
	
	public void setItem (DictionaryItem item, int index)
	{
		if (index >= 0 && index < dictionary.size()) 
		{
			dictionary.set(index, item);
		}
	}
	
	//------------------------------------------------------
	
	public boolean isWordInDictionary (String word)
	{
		if (dictionaryWordSearch.containsKey(word)) { return true; }
		return false;
	}
	
	//------------------------------------------------------
	
	public DictionaryItem getItem (int index)
	{
		if (index >= 0 && index < dictionary.size())
		{
			return dictionary.get(index);
		}
		
		return null;
	}
	
	public DictionaryItem getItem (String word)
	{
		if (dictionaryWordSearch.containsKey(word))
		{
			int index = dictionaryWordSearch.get(word);
			
			return getItem(index);
		}
		
		return null;
	}
	
	public int getSize()
	{
		return dictionary.size();
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
			
			for (int i = 0; i< dictionary.size(); i++)
			{
				String outword = dictionary.get(i).word;
				int outwordid = dictionary.get(i).wordIndex;
				int outwordpos = dictionary.get(i).wordPOS;
				int outwordlabel = dictionary.get(i).wordLabel;
				
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
				
				dictionary.add(new DictionaryItem(inword, inwordid, inwordpos, inwordlabel));
			}
			
			
			bfr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
