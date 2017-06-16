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

			dimIN = storage.getDim();
			dimOUT = storage.getDim();

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


		//Create input for initialization
		INDArray initializationInput = Nd4j.zeros(1, storage.getDim(), sequencePrime.length);

		for( int i=0; i < sequencePrime.length; i++ ) {	initializationInput.putScalar(new int[]{0,sequencePrime[i],i}, 1.0f); }

		//Sample from network (and feed samples back into input) one character at a time (for all samples)
		//Sampling is done in parallel here
		net.rnnClearPreviousState();
		INDArray output = net.rnnTimeStep(initializationInput);
		output = output.tensorAlongDimension(output.size(2)-1,1,0);	//Gets the last time step output

		for( int i=0; i < sequenceOutputLength; i++ )
		{
			//Set up next input (single time step) by sampling from previous output
			INDArray nextInput = Nd4j.zeros(0, storage.getDim());

			//Output is a probability distribution. Sample from this for each example we want to generate, and add it to the new input

			double[] outputProbDistribution = new double[storage.getDim()];
			for( int j=0; j<outputProbDistribution.length; j++ ) outputProbDistribution[j] = output.getDouble(0,j);
			int sampledCharacterIdx = sampleFromDistribution(outputProbDistribution);

			nextInput.putScalar(new int[]{0,sampledCharacterIdx}, 1.0f);		//Prepare next time step input

			output = net.rnnTimeStep(nextInput);	//Do one time step of forward pass
		}

		return storage.transformIndexSequence(sequenceOutput);
	}


	public static int sampleFromDistribution( double[] distribution){
	    double d = 0.0;
	    
	    
	    Random rng = new Random();
	    double sum = 0.0;
	    for( int t=0; t<10; t++ ) {
            d = rng.nextDouble();
            sum = 0.0;
            for( int i=0; i<distribution.length; i++ ){
                sum += distribution[i];
                if( d <= sum ) return i;
            }
            //If we haven't found the right index yet, maybe the sum is slightly
            //lower than 1 due to rounding error, so try again.
        }
		//Should be extremely unlikely to happen if distribution is a valid probability distribution
		throw new IllegalArgumentException("Distribution is invalid? d="+d+", sum="+sum);
	}



}
