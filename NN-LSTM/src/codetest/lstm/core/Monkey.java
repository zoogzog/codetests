package codetest.lstm.core;

import java.util.Random;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import codetest.lstm.data.SequenceStorage;
import codetest.lstm.gui.CallbackIteration;
import codetest.lstm.nn.NeuralNetworkLSTM;
import codetest.lstm.text.FileTransformer;
import codetest.lstm.text.FileTransformerCharacter;
import codetest.lstm.text.FileTransformerWord;

public class Monkey 
{	
	
	//---- Internal storage, which holds a sequence of integer. Used 
	//---- to generate data sets for training the NN
	private SequenceStorage storage  = null;
	
	//---- The NN, LSTM, contains NN structure and the NN class
	private NeuralNetworkLSTM netC = null;

	private boolean isTrained = false;
	private boolean isDataLoaded = false;

	//---- Dimensions of feature vector of NN in and out
	//---- Here, basically IN = OUT (using LSTM)
	private int dimIN = 0;
	private int dimOUT = 0;

	int nCharactersToSample = 300;	

	int setMinibatchSize = 0;
	int setSequenceLength = 0;
	int setEpochs = 0;
	
	//----------------------------------------------------------------------------

	public Monkey ()
	{
		storage  = new SequenceStorage();
	}
	
	//----------------------------------------------------------------------------

	public void loadData (String filePath, int miniBatchSize, int sequenceLength)
	{
		try
		{
			storage.loadSequence(filePath, miniBatchSize, sequenceLength);
			
			dimIN = storage.getDimIN();
			dimOUT = storage.getDimOUT();
			
			isDataLoaded = true;

			netC = new NeuralNetworkLSTM(dimIN, dimOUT);
			
			setMinibatchSize = miniBatchSize;
			setSequenceLength = sequenceLength;
		}
		catch (Exception e) { e.printStackTrace();}
	}

	public void train (int numEpochs, CallbackIteration callback)
	{
		storage.debugPrint();
		if (isDataLoaded)
		{
			isTrained = true;

			int maxSteps = (numEpochs * storage.getPointersLength()) / setMinibatchSize;
			int step = 0;

			for( int i=0; i<numEpochs; i++ )
			{
				while(storage.hasNext())
				{
					if (callback != null) {callback.callbackIterationDone(storage.getSequenceLength(), step, maxSteps); step++;}

					DataSet ds = storage.next();
					netC.getNetwork().fit(ds);
				}

				//---- Reset iterator for the next epoch
				storage.reset();
			}
		} 
	}

	public void saveNN (String path)
	{
		netC.save(path);
	}

	public void loadNN (String path)
	{
		netC.load(path);

		isTrained = true;
	}

	public String generate (String init)
	{
		if (isTrained && isDataLoaded)
		{
			return generateOutput (null, netC.getNetwork(), nCharactersToSample);
		}

		return "";
	}

	//----------------------------------------------------------------------------

	public boolean getIsTrained ()
	{
		return isTrained;
	}

	public boolean getIsDataLoaded ()
	{
		return isDataLoaded;
	}

	//----------------------------------------------------------------------------

	private String generateOutput (int[] sequencePrime, MultiLayerNetwork net, int sequenceOutputLength)
	{
		int [] sequenceOutput = new int[sequenceOutputLength];
		
		//---- Prime sequence with a random value
		if (sequencePrime == null) { sequencePrime = storage.getRandomSequence(1); }
		
		INDArray ndSequencePrime = Nd4j.zeros(1, storage.getDimIN(), sequencePrime.length);
		INDArray ndSequenceOutput = net.rnnTimeStep(ndSequencePrime);
		INDArray ndSequenceInput = Nd4j.zeros(1,storage.inputColumns());
		
		System.out.println("Generating response for the following prime sequence");
		System.out.println(storage.transformIndexSequence(sequencePrime));
		
		for(int i=0; i<sequencePrime.length; i++ )	
		{
			//---- Encode the current element of the sequence
			double[] encodeSequencePrime = storage.encode(sequencePrime[i]);
		
			//---- Store the encoded element
			for (int ep = 0; ep < encodeSequencePrime.length; ep++) 
			{
				ndSequencePrime.putScalar(new int[]{0,ep,i}, encodeSequencePrime[ep]);
			}
		}
		
		net.rnnClearPreviousState();
		
		
	
		ndSequenceOutput = ndSequenceOutput.tensorAlongDimension(ndSequenceOutput.size(2)-1,1,0);	//Gets the last time step output

		for( int i=0; i<sequenceOutputLength; i++ )
		{
			//---- Select input
			ndSequenceInput = Nd4j.zeros(1,storage.inputColumns());
			
			double[] outputNN = new double[storage.totalOutcomes()];
			for( int j=0; j<outputNN.length; j++ ) outputNN[j] = ndSequenceOutput.getDouble(0,j);
			
			int sampledCharacterIdx = storage.decode(outputNN);

			double[] encodeSequence = storage.encode(sampledCharacterIdx);
			
			
				for (int ep = 0; ep < encodeSequence.length; ep++) 
				{ndSequenceInput.putScalar(new int[]{0,ep}, encodeSequence[ep]);}
			

			sequenceOutput[i] = sampledCharacterIdx;

			//---- Select output
			ndSequenceOutput = net.rnnTimeStep(ndSequenceInput);	//Do one time step of forward pass
		};


		return storage.transformIndexSequence(sequenceOutput);
	}




	

}
