package codetest.lstm.background;

import javax.swing.SwingWorker;

import codetest.lstm.core.CoreTrainer;
import codetest.lstm.gui.FormMainHandler;

/**
 * Class for invoking background processes, such as different types of training. 
 * @author Grushnikov Andrey
 */
public class TaskBackground extends SwingWorker<Void, Void> 
{
	//---- Links to main classes
	private FormMainHandler handler;
	private CoreTrainer monkey;
	
	//---- Settings 
	private String sPath;
	private int sEpochs;
	private int sMinibatchSize;
	private int sSequenceLength;
	
	private int sTransformerType;	
	private int sDictionaryType;
	
	//---- Some constants
	public static final int TRANSFORMER_CHAR = CoreTrainer.TRANSFORMER_CHAR;
	public static final int TRANSFORMER_POS = CoreTrainer.TRANSFORMER_POS;
	public static final int TRANSFORMER_SEMANTIC = CoreTrainer.TRANSFORMER_SEMANTIC;
	
	public static final int DICTIONARY_GENERATE = 0;
	public static final int DICTIONARY_USE = 1;
	
	//----------------------------------------------------------------------------
	
	public void setParameters (CoreTrainer monkey, FormMainHandler handler, String path, int epochs, int minibatchsize, int seqlength, int transformer, int dictionary)
	{
		this.monkey = monkey;
		this.handler = handler;
		sPath = path;
		sEpochs = epochs;
		sMinibatchSize = minibatchsize;
		sSequenceLength = seqlength;
		
		sTransformerType = transformer;
		sDictionaryType = dictionary;
	}
	
	//----------------------------------------------------------------------------
	
	@Override
	protected Void doInBackground() throws Exception 
	{
		monkey.loadData(sPath, sMinibatchSize, sSequenceLength, sTransformerType, sDictionaryType);
		monkey.train(sEpochs, handler);	
		return null;
	}
	
	@Override
	public void done()
	{
		handler.callbackTrainDone();
	}

}
