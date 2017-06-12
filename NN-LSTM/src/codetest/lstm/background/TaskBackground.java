package codetest.lstm.background;

import javax.swing.SwingWorker;

import codetest.lstm.core.Monkey;
import codetest.lstm.gui.FormMainHandler;

public class TaskBackground extends SwingWorker<Void, Void> 
{
	private FormMainHandler handler;
	private Monkey monkey;
	
	private String sPath;
	private int sEpochs;
	private int sMinibatchSize;
	private int sSequenceLength;
	
	public void setParameters (Monkey monkey, FormMainHandler handler, String path, int epochs, int minibatchsize, int seqlength)
	{
		this.monkey = monkey;
		this.handler = handler;
		sPath = path;
		sEpochs = epochs;
		sMinibatchSize = minibatchsize;
		sSequenceLength = seqlength;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception 
	{
		monkey.loadData(sPath, sMinibatchSize, sSequenceLength);
		monkey.train(sEpochs, handler);	
		return null;
	}
	
	@Override
	public void done()
	{
		handler.callbackTrainDone();
	}

}
