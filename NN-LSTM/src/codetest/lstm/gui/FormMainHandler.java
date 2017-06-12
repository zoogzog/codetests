package codetest.lstm.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import codetest.lstm.background.TaskBackground;
import codetest.lstm.core.Monkey;
import codetest.lstm.nn.Settings;
import freemarker.core.CommandLine;

public class FormMainHandler implements ActionListener, CallbackIteration
{
	public static final String CMD_SELECTFILE = "cmd-slctfl";
	public static final String CMD_TRAINNN = "cmd-trainn";
	public static final String CMD_GENERATE = "cmd-generate";
	
	public static final String CMD_NNSAVE = "cmd-save";
	public static final String CMD_NNLOAD = "cmd-load";

	private FormMain fmlnk = null;
	private Monkey monkey = null;

	String filePath = "";

	//----------------------------------------------------------------------------

	public FormMainHandler ()
	{

	}

	//----------------------------------------------------------------------------

	public void init (FormMain lnk, Monkey monkey)
	{
		this.fmlnk = lnk;
		this.monkey = monkey;
	}

	//----------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent source) 
	{
		if (fmlnk != null)
		{
			String cmdName = source.getActionCommand();

			switch (cmdName)
			{
			case CMD_SELECTFILE: commandSelectFile(); break;
			case CMD_TRAINNN: commandTrainNetwork(); break;
			case CMD_GENERATE: commandGenerate(); break;
			case CMD_NNSAVE: commandSaveNN(); break;
			case CMD_NNLOAD: commmandLoadNN(); break;
			}

		}

	}

	//----------------------------------------------------------------------------

	private void commandSelectFile ()
	{
		JFileChooser fileChooserDriver = new JFileChooser();

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			filePath = fileChooserDriver.getSelectedFile().getPath();

			fmlnk.getComponentPanel().textfieldFilePath.setText(filePath);
		}
	}

	private void commandTrainNetwork ()
	{
		if (filePath.length() == 0) { return; }
		
		try
		{
			int epochs = Integer.parseInt(fmlnk.getComponentPanel().textfieldEpoch.getText());
			int minibatchsize = Integer.parseInt(fmlnk.getComponentPanel().textfieldMiniBatch.getText());
			int seqlength = Integer.parseInt(fmlnk.getComponentPanel().textfieldSequenceLength.getText());

			fmlnk.getComponentPanel().buttonTrainNet.setEnabled(false);
			fmlnk.getComponentPanel().buttonGenerateSample.setEnabled(false);
			
			fmlnk.getComponentPanel().buttonSaveNet.setEnabled(false);
			fmlnk.getComponentPanel().buttonLoadNet.setEnabled(false);

			TaskBackground task = new TaskBackground();

			task.setParameters(monkey, this, filePath, epochs, minibatchsize, seqlength);
			task.execute();
		}
		catch (Exception e) { return; }
	}

	private void commandGenerate ()
	{
		if (monkey.getIsTrained() && monkey.getIsDataLoaded())
		{
			String txt = "";
			String rsp = monkey.generate(null);
			
			fmlnk.getComponentPanel().textareaGeneratedText.setText(rsp);
			
		}
	}

	private void commandSaveNN ()
	{
		if (monkey.getIsTrained())
		{
			monkey.saveNN("nnfile.zip");
		}
	}
	
	private void commmandLoadNN ()
	{
		if (filePath == "") { return; }
		
		fmlnk.getComponentPanel().buttonGenerateSample.setEnabled(true);
		
		
		monkey.loadData(filePath, Settings.DEF_NNMINIBATCHSIZE, Settings.DEF_NNSEQUENCELENGTH);
		monkey.loadNN("nnfile.zip");
	}
	
	//----------------------------------------------------------------------------

	public void callbackTrainDone ()
	{
		fmlnk.getComponentPanel().buttonTrainNet.setEnabled(true);
		fmlnk.getComponentPanel().buttonGenerateSample.setEnabled(true);
		
		fmlnk.getComponentPanel().buttonSaveNet.setEnabled(true);
		fmlnk.getComponentPanel().buttonLoadNet.setEnabled(true);
	}

	@Override
	public void callbackIterationDone(int total, int step, int maxstep) 
	{
		fmlnk.getComponentPanel().textfieldTotalSize.setText(String.valueOf(total));
		fmlnk.getComponentPanel().textfieldIterationStep.setText(String.valueOf(step));
		fmlnk.getComponentPanel().textfieldTotalProgress.setText(String.valueOf(maxstep));
		
	}
	

}
