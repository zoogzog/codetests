package codetest.lstm.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jfree.layout.LCBLayout;
import org.w3c.dom.ls.LSLoadEvent;

import com.sun.java.accessibility.util.java.awt.ButtonTranslator;

import codetest.lstm.nn.Settings;


public class FormMainPanel 
{
	public JTextField textfieldFilePath;

	public JTextField textfieldSequenceLength;
	public JTextField textfieldMiniBatch;
	public JTextField textfieldEpoch;

	public JTextField textfieldTotalSize;
	public JTextField textfieldIterationStep;
	public JTextField textfieldTotalProgress;

	public JButton buttonSelectFile;
	public JButton buttonTrainNet;
	public JButton buttonSaveNet;
	public JButton buttonLoadNet;
	public JButton buttonGenerateSample;
	
	public JTextArea textareaGeneratedText;
	
	public JRadioButton radioButtonTrChar;
	public JRadioButton radioButtonTrPos;
	public JRadioButton radioButtonTrSem;
	public ButtonGroup radioGroupTrType;

	public JRadioButton radioButtonDicGenerate;
	public JRadioButton radioButtonDicUse;
	public ButtonGroup radioGroupDicType;
	

	public JPanel mainPanel;

	public FormMainPanel (FormMainHandler handler)
	{
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setSize(new Dimension (240, 105));
		mainPanel.setMinimumSize(mainPanel.getSize());
		mainPanel.setMaximumSize(mainPanel.getSize());
		mainPanel.setPreferredSize(mainPanel.getSize());
		GridBagConstraints layoutConstraits = new GridBagConstraints();

		//==================================================================

		textfieldFilePath = new JTextField();
		textfieldFilePath.setPreferredSize(new Dimension(200, 20));
		textfieldFilePath.setSize(new Dimension(200, 20));
		textfieldFilePath.setEditable(false);
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 0;
		layoutConstraits.anchor = GridBagConstraints.CENTER;
		layoutConstraits.fill = GridBagConstraints.NONE;
		layoutConstraits.weightx = 0.1;
		layoutConstraits.gridwidth = 2;
		layoutConstraits.weighty = 0.1;
		mainPanel.add(textfieldFilePath, layoutConstraits);

		buttonSelectFile = new JButton("Select File");
		buttonSelectFile.setPreferredSize(new Dimension(100, 20));
		buttonSelectFile.setSize(new Dimension(100, 20));
		buttonSelectFile.addActionListener(handler);
		buttonSelectFile.setActionCommand(FormMainHandler.CMD_SELECTFILE);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = 0;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(buttonSelectFile, layoutConstraits);

		//==================================================================

		componentTransformMethod(handler, layoutConstraits, 1);
		componentDictionaryType(handler, layoutConstraits, 2);
		componentNetButtons(handler, layoutConstraits, 3);
		componentNetSettingsLabels(handler, layoutConstraits, 4);
		componentNetSettings(handler, layoutConstraits, 5);
		componentStatisticsLabels(handler, layoutConstraits, 6);
		componentStatisticsInfo(handler, layoutConstraits, 7);
		componentTextGenerator(handler, layoutConstraits, 8);


	}
	
	private void componentTransformMethod (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		radioButtonTrChar = new JRadioButton("Character");
		radioButtonTrChar.setPreferredSize(new Dimension(100, 20));
		radioButtonTrChar.setSize(new Dimension(100, 20));
		radioButtonTrChar.addActionListener(handler);
		radioButtonTrChar.setActionCommand(FormMainHandler.CMD_RADIOBUTTON);
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(radioButtonTrChar, layoutConstraits);
		
		radioButtonTrPos = new JRadioButton("POS");
		radioButtonTrPos.setPreferredSize(new Dimension(100, 20));
		radioButtonTrPos.setSize(new Dimension(100, 20));
		radioButtonTrPos.addActionListener(handler);
		radioButtonTrPos.setActionCommand(FormMainHandler.CMD_RADIOBUTTON);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(radioButtonTrPos, layoutConstraits);
		
		radioButtonTrSem = new JRadioButton("Semantic");
		radioButtonTrSem.setPreferredSize(new Dimension(100, 20));
		radioButtonTrSem.setSize(new Dimension(100, 20));
		radioButtonTrSem.addActionListener(handler);
		radioButtonTrSem.setActionCommand(FormMainHandler.CMD_RADIOBUTTON);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(radioButtonTrSem, layoutConstraits);
		
		radioGroupTrType = new ButtonGroup();
		radioGroupTrType.add(radioButtonTrChar);
		radioGroupTrType.add(radioButtonTrPos);
		radioGroupTrType.add(radioButtonTrSem);
	}
	
	private void componentDictionaryType (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		JLabel labelDictionary = new JLabel (" Dictionary:");
		labelDictionary.setPreferredSize(new Dimension(100, 20));
		labelDictionary.setSize(new Dimension(100, 20));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		mainPanel.add(labelDictionary, layoutConstraits);
		
		radioButtonDicGenerate = new JRadioButton("Generate");
		radioButtonDicGenerate.setPreferredSize(new Dimension(100, 20));
		radioButtonDicGenerate.setSize(new Dimension(100, 20));
		radioButtonDicGenerate.addActionListener(handler);
		radioButtonDicGenerate.setActionCommand(FormMainHandler.CMD_TRAINNN);
		radioButtonDicGenerate.setEnabled(false);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(radioButtonDicGenerate , layoutConstraits);
		
		radioButtonDicUse = new JRadioButton("Default");
		radioButtonDicUse.setPreferredSize(new Dimension(100, 20));
		radioButtonDicUse.setSize(new Dimension(100, 20));
		radioButtonDicUse.addActionListener(handler);
		radioButtonDicUse.setActionCommand(FormMainHandler.CMD_TRAINNN);
		radioButtonDicUse.setEnabled(false);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(radioButtonDicUse , layoutConstraits);
		
		radioGroupDicType = new ButtonGroup();
		radioGroupDicType.add(radioButtonDicGenerate);
		radioGroupDicType.add(radioButtonDicUse);
	}
	
	private void componentNetButtons (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		buttonTrainNet = new JButton("Train NN");
		buttonTrainNet.setPreferredSize(new Dimension(100, 20));
		buttonTrainNet.setSize(new Dimension(100, 20));
		buttonTrainNet.addActionListener(handler);
		buttonTrainNet.setActionCommand(FormMainHandler.CMD_TRAINNN);
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(buttonTrainNet, layoutConstraits);

		buttonSaveNet = new JButton("Save NN");
		buttonSaveNet.setPreferredSize(new Dimension(100, 20));
		buttonSaveNet.setSize(new Dimension(100, 20));
		buttonSaveNet.setEnabled(false);
		buttonSaveNet.addActionListener(handler);
		buttonSaveNet.setActionCommand(FormMainHandler.CMD_NNSAVE);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(buttonSaveNet, layoutConstraits);

		buttonLoadNet = new JButton("Load NN");
		buttonLoadNet.setPreferredSize(new Dimension(100, 20));
		buttonLoadNet.setSize(new Dimension(100, 20));
		buttonLoadNet.addActionListener(handler);
		buttonLoadNet.setActionCommand(FormMainHandler.CMD_NNLOAD);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(buttonLoadNet, layoutConstraits);

	}

	private void componentNetSettings (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		textfieldSequenceLength = new JTextField();
		textfieldSequenceLength.setPreferredSize(new Dimension(100, 20));
		textfieldSequenceLength.setSize(new Dimension(100, 20));
		textfieldSequenceLength.setText(String.valueOf(Settings.DEF_NNSEQUENCELENGTH));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		mainPanel.add(textfieldSequenceLength, layoutConstraits);

		textfieldMiniBatch = new JTextField();
		textfieldMiniBatch.setPreferredSize(new Dimension(100, 20));
		textfieldMiniBatch.setSize(new Dimension(100, 20));
		textfieldMiniBatch.setText(String.valueOf(Settings.DEF_NNMINIBATCHSIZE));
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = row;
		mainPanel.add(textfieldMiniBatch, layoutConstraits);

		textfieldEpoch = new JTextField();
		textfieldEpoch.setPreferredSize(new Dimension(100, 20));
		textfieldEpoch.setSize(new Dimension(100, 20));
		textfieldEpoch.setText(String.valueOf(Settings.DEF_NNEPOCHCOUNT));
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(textfieldEpoch, layoutConstraits);
	}

	private void componentNetSettingsLabels  (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		JLabel labelTrainSequence = new JLabel ("Training set");
		 labelTrainSequence.setPreferredSize(new Dimension(100, 20));
		 labelTrainSequence .setSize(new Dimension(100, 20));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		mainPanel.add(labelTrainSequence, layoutConstraits);

		JLabel labelMinibatchSize = new JLabel ("Minibatch size");
		labelMinibatchSize.setPreferredSize(new Dimension(100, 20));
		labelMinibatchSize.setSize(new Dimension(100, 20));
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = row;
		mainPanel.add(labelMinibatchSize, layoutConstraits);

		JLabel labelNetEpochs = new JLabel("Training epocs");
		labelNetEpochs.setPreferredSize(new Dimension(100, 20));
		labelNetEpochs.setSize(new Dimension(100, 20));
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row;
		mainPanel.add(labelNetEpochs, layoutConstraits);
	}
	
	private void componentStatisticsLabels (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		JLabel labelSequenceLength = new JLabel ("Sequence length");
		labelSequenceLength.setPreferredSize(new Dimension(100, 20));
		labelSequenceLength.setSize(new Dimension(100, 20));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		mainPanel.add(labelSequenceLength, layoutConstraits);

		JLabel labelIterationCurrent = new JLabel ("Current step");
		labelIterationCurrent.setPreferredSize(new Dimension(100, 20));
		labelIterationCurrent.setSize(new Dimension(100, 20));
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = row;
		mainPanel.add(labelIterationCurrent, layoutConstraits);

		JLabel labelIterationMax = new JLabel("Max steps");
		labelIterationMax.setPreferredSize(new Dimension(100, 20));
		labelIterationMax.setSize(new Dimension(100, 20));
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row;
		mainPanel.add(labelIterationMax, layoutConstraits);
	}

	private void componentStatisticsInfo (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		textfieldTotalSize = new JTextField();
		textfieldTotalSize.setPreferredSize(new Dimension(100, 20));
		textfieldTotalSize.setSize(new Dimension(100, 20));
		textfieldTotalSize.setEditable(false);
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		mainPanel.add(textfieldTotalSize, layoutConstraits);

		textfieldIterationStep = new JTextField();
		textfieldIterationStep.setPreferredSize(new Dimension(100, 20));
		textfieldIterationStep.setSize(new Dimension(100, 20));
		textfieldIterationStep.setEditable(false);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = row;
		mainPanel.add(textfieldIterationStep, layoutConstraits);

		textfieldTotalProgress = new JTextField();
		textfieldTotalProgress.setPreferredSize(new Dimension(100, 20));
		textfieldTotalProgress.setSize(new Dimension(100, 20));
		textfieldTotalProgress.setEditable(false);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(textfieldTotalProgress, layoutConstraits);
	}

	private void componentTextGenerator (FormMainHandler handler, GridBagConstraints layoutConstraits, int row)
	{
		//==================================================================

		textareaGeneratedText = new JTextArea();
		textareaGeneratedText.setPreferredSize(new Dimension(300, 200));
		textareaGeneratedText.setSize(new Dimension(300, 200));
		textareaGeneratedText.setEditable(false);
		textareaGeneratedText.setLineWrap(true);
		textareaGeneratedText.setWrapStyleWord(true);
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = row;
		layoutConstraits.gridwidth = 3;
		mainPanel.add(textareaGeneratedText, layoutConstraits);

		//==================================================================

		buttonGenerateSample = new JButton("Generate");
		buttonGenerateSample.setPreferredSize(new Dimension(100, 20));
		buttonGenerateSample.setSize(new Dimension(100, 20));
		buttonGenerateSample.setEnabled(false);
		buttonGenerateSample.setActionCommand(FormMainHandler.CMD_GENERATE);
		buttonGenerateSample.addActionListener(handler);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = row+1;
		layoutConstraits.gridwidth = 1;
		mainPanel.add(buttonGenerateSample, layoutConstraits);
	}
}
