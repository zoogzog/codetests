package codetest.lstm.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import codetest.lstm.background.TaskBackground;
import codetest.lstm.core.CoreTrainer;
import codetest.lstm.nn.Settings;
import freemarker.core.CommandLine;

public class FormMainHandler implements ActionListener, CallbackIteration
{
	public static final String CMD_SELECTFILE = "cmd-slctfl";
	public static final String CMD_TRAINNN = "cmd-trainn";
	public static final String CMD_GENERATE = "cmd-generate";
	
	public static final String CMD_NNSAVE = "cmd-save";
	public static final String CMD_NNLOAD = "cmd-load";
	
	public static final String CMD_RADIOBUTTON = "cmd-radio";

	private FormMain fmlnk = null;
	private CoreTrainer monkey = null;

	private String filePath = "";
	
	//----------------------------------------------------------------------------

	public FormMainHandler ()
	{

	}

	//----------------------------------------------------------------------------

	public void init (FormMain lnk, CoreTrainer monkey)
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
			case CMD_RADIOBUTTON: commandRadioButtonClick(); break;
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

			int transformer = TaskBackground.TRANSFORMER_CHAR;
			
			//---- These radio buttons are in a group, lazily assigning value here
			if (fmlnk.getComponentPanel().radioButtonTrChar.isSelected()) { transformer = TaskBackground.TRANSFORMER_CHAR; }
			if (fmlnk.getComponentPanel().radioButtonTrPos.isSelected()) { transformer = TaskBackground.TRANSFORMER_POS; }
			if (fmlnk.getComponentPanel().radioButtonTrSem.isSelected()) { transformer = TaskBackground.TRANSFORMER_SEMANTIC; }
			
			int dictionary = TaskBackground.DICTIONARY_GENERATE;
			
			//---- These radio buttons are in a group, lazily assigning value here
			if (fmlnk.getComponentPanel().radioButtonDicGenerate.isSelected()) { dictionary = TaskBackground.DICTIONARY_GENERATE; }
			if (fmlnk.getComponentPanel().radioButtonDicUse.isSelected()) { dictionary = TaskBackground.DICTIONARY_USE; }
			
			TaskBackground task = new TaskBackground();
			
			task.setParameters(monkey, this, filePath, epochs, minibatchsize, seqlength, transformer, dictionary);
			task.execute();
		}
		catch (Exception e) { return; }
	}

	private void commandGenerate ()
	{
		if (monkey.getIsTrained())
		{
			System.out.println("enter generation");
			String txt = "";
			String rsp = monkey.generate(null, 300);
			
			rsp = rsp.replace("    ", "");
			rsp = rsp.replace("  ", " ");
			rsp = rsp.replace("\n ", "\n");
			
			fmlnk.getComponentPanel().textareaGeneratedText.setText(rsp); 
		}
	}

	private void commandSaveNN ()
	{
		if (monkey.getIsTrained())
		{
			JFileChooser fileChooserDriver = new JFileChooser();

			int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

			if (isFileSelected == JFileChooser.APPROVE_OPTION)
			{
				String path = fileChooserDriver.getSelectedFile().getPath();
				monkey.saveNN(path + ".zip");
			}
		}
	}
	
	private void commmandLoadNN ()
	{		
		JFileChooser fileChooserDriver = new JFileChooser();

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String path = fileChooserDriver.getSelectedFile().getPath();
			
			//---- FIXME do loading in background here
			monkey.loadNN(path);
			
			fmlnk.getComponentPanel().buttonGenerateSample.setEnabled(true);
		}
	}
	
	private void commandRadioButtonClick ()
	{
		if (fmlnk.getComponentPanel().radioButtonTrSem.isSelected())
		{
			fmlnk.getComponentPanel().radioButtonDicGenerate.setEnabled(true);
			fmlnk.getComponentPanel().radioButtonDicUse.setEnabled(true);
		}
		else
		{
			fmlnk.getComponentPanel().radioButtonDicGenerate.setEnabled(false);
			fmlnk.getComponentPanel().radioButtonDicUse.setEnabled(false);
		}
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
