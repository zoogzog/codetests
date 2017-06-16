package codetest.lstm.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import codetest.lstm.data.SequenceStorage;

public class TextTransformerPos implements TextTransformer
{
	private TextTokenizer tokenizer = null;
		
	public TextTransformerPos()
	{
		tokenizer = new TextTokenizer(true, true, false);
	}
	
	@Override
	public void transform(String path, SequenceStorage storage) 
	{
		
		try
		{
			BufferedReader bfr = new BufferedReader(new FileReader(path));
			
			String line = "";
			
			int dim = tokenizer.getPosTableSize();
			
			Vector <Integer> tmpSequence = new Vector <Integer>();
			
			while ((line = bfr.readLine()) != null)
			{
				String[] tk = tokenizer.generateTokens(line, true);
				String[] tkpos = tokenizer.generatePos(tk);
				
				for (int i = 0; i < tkpos.length; i++)
				{
					int index = tokenizer.getPosIndex(tkpos[i]);
					
					tmpSequence.addElement(index);
				}
			}
			
			//---- Load sequence from the temporary storage into the main storage
			storage.allocateMemory(tmpSequence.size());
			storage.setSequenceDim(dim);
			
			for (int i = 0; i < tmpSequence.size(); i++)
			{
				storage.set(i, tmpSequence.get(i));
			}
		}
		catch (Exception e){ e.printStackTrace(); }
	}

	@Override
	public String transformIndexSequence(int[] seq) 
	{
		String output = "";
		
		for (int i = 0; i < seq.length; i++)
		{
			String posElement = tokenizer.getPosString(seq[i]);
			
			
			
			output = output + tokenizer.posTester.get(posElement) + ";";
		}
		
		
		return output;
	}

	@Override
	public int getDim() 
	{
		return tokenizer.getPosTableSize();
	}

}
