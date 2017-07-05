package codetest.lstm.core;

import java.util.Random;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import codetest.lstm.data.SequenceStorage;
import codetest.lstm.gui.CallbackIteration;
import codetest.lstm.nn.NeuralNetworkLSTM;
import codetest.lstm.text.TextTransformer;
import codetest.lstm.text.TextTransformerCharacter;
import codetest.lstm.text.TextTransformerPos;
import codetest.lstm.text.TextTransformerSemantic;

/**
 * Main class for performing NN training, generating text
 * @author Grushnikov Andrey
 */
public class CoreTrainer 
{	
	//---- Internal storage, which holds a sequence of integers. It is used 
	//---- to generate data sets for training the NN. The sequence is 
	//---- stored as integer numbers, than converted into one-shoe encoding
	//---- when requested by the neural network training algorithm.
	private SequenceStorage storage  = null;

	//---- Class which is used to transform initial text into sequence of integers
	private TextTransformer transformer = null;

	//---- Class which describes the ANN structure
	private NeuralNetworkLSTM netC = null;

	//---- Flag: if ANN was trained equals true, if not then false
	private boolean isTrained = false;

	//---- Flag: if sequence of integers is generated then true, otherwise false
	private boolean isDataLoaded = false;

	//---- Parameters for training ANN: size of input and output vectors
	private int dimIN = 0;
	private int dimOUT = 0;

	//---- Parameters for training ANN
	private int setMinibatchSize = 0;
	private int setSequenceLength = 0;
	private int setEpochs = 0;

	//----------------------------------------------------------------------------

	//---- Generate a sequence of characters
	public static final int TRANSFORMER_CHAR = 0;
	
	//---- Generate a sequence of word's part of speech tags
	public static final int TRANSFORMER_POS = 1;
	
	//---- Generate a sequence of semantic meanings
	public static final int TRANSFORMER_SEMANTIC = 2;
	
	//---- Currently selected transformation type, default - character
	private int TRANSFORMER_TYPE = TRANSFORMER_SEMANTIC;
	
	//----------------------------------------------------------------------------
	
	public CoreTrainer ()
	{
		storage  = new SequenceStorage();
	}

	//----------------------------------------------------------------------------

	/**
	 * Parse text, generate sequence of integers which represents time series with the
	 * selecting transformation algorithm.
	 * @param filePath
	 * @param miniBatchSize
	 * @param sequenceLength
	 */
	public void loadData (String filePath, int miniBatchSize, int sequenceLength, int trtype, int dictionary)
	{
		try
		{
			TRANSFORMER_TYPE = trtype;
			
			//---- Choose a transformer
			switch (TRANSFORMER_TYPE)
			{
			case TRANSFORMER_CHAR: transformer = new TextTransformerCharacter(); break;
			case TRANSFORMER_POS: transformer = new TextTransformerPos(); break;
			case TRANSFORMER_SEMANTIC: 
				transformer = new TextTransformerSemantic(); 
				
				break;
			default: transformer = new TextTransformerCharacter(); break;
			}
			
			//---- Perform transformation from the original text into sequence of integers
			transformer.transform(filePath, storage);

			//---- Specify parameters for generating training datasets
			storage.loadSequence(filePath, miniBatchSize, sequenceLength);

			//---- Dimensions of the input and output vectors
			dimIN = storage.getDim();
			dimOUT = storage.getDim();

			isDataLoaded = true;

			netC = new NeuralNetworkLSTM(dimIN, dimOUT);

			setMinibatchSize = miniBatchSize;
			setSequenceLength = sequenceLength;
		}
		catch (Exception e) { e.printStackTrace();}
	}

	//----------------------------------------------------------------------------
	
	/**
	 * Train the ANN. The data has to be loaded before calling this function
	 * @param numEpochs -- number of epochs ANN is trained
	 * @param callback -- returns progress via callback if it is not null
	 */
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
		if (netC == null) { netC = new NeuralNetworkLSTM(); }
		netC.load(path);

		isTrained = true;
	}

	//----------------------------------------------------------------------------
	
	/**
	 * Generates sequence of integers then transforms into text according to selected transformation
	 * algorithm.
	 * @param init 
	 * @param nSampleLength
	 * @return
	 */
	public String generate (String init, int nSampleLength)
	{
		int[] sequencePrime = null;
		MultiLayerNetwork net = netC.getNetwork();


		//---- Check if the neural network was trained or loaded
		if (!isTrained) { return ""; }

		//---- Select the type of transformer to use
		if (transformer == null) 
		{ 
			//---- Choose a transformer
			switch (TRANSFORMER_TYPE)
			{
			case TRANSFORMER_CHAR: transformer = new TextTransformerCharacter(); break;
			case TRANSFORMER_POS: transformer = new TextTransformerPos(); break;
			case TRANSFORMER_SEMANTIC: transformer = new TextTransformerSemantic(); break;
			default: transformer = new TextTransformerCharacter(); break;
			}
		}

		//---- Obtain the dimension of the output layer of the network
		//---- Ideally here it should be obtained from the network itself rather than the 
		//---- text transformer, but this is hard to do...
		int dim = transformer.getDim();

		//---- Prime the sequence with a single element
		if( sequencePrime == null ){ sequencePrime = new int[]{(int) (Math.random() * dim)}; }

		INDArray sequencePrimeInput = Nd4j.zeros(1, dim, sequencePrime.length);

		for( int i=0; i<sequencePrime.length; i++ )
		{
			sequencePrimeInput.putScalar(new int[]{0,sequencePrime[i],i}, 1.0f);
		}

		int[] sequenceOut = new int[nSampleLength];

		//Sample from network (and feed samples back into input) one character at a time (for all samples)
		//Sampling is done in parallel here
		net.rnnClearPreviousState();
		INDArray output = net.rnnTimeStep(sequencePrimeInput);
		output = output.tensorAlongDimension(output.size(2)-1,1,0);	//Gets the last time step output

		for( int i=0; i < nSampleLength; i++ )
		{
			//Set up next input (single time step) by sampling from previous output
			INDArray nextInput = Nd4j.zeros(1,dim);
			//Output is a probability distribution. Sample from this for each example we want to generate, and add it to the new input
			for( int s=0; s<1; s++ )
			{
				double[] outputProbDistribution = new double[dim];
				for( int j=0; j<outputProbDistribution.length; j++ ) outputProbDistribution[j] = output.getDouble(s,j);
				int sampledCharacterIdx = sampleFromDistribution(outputProbDistribution);


				nextInput.putScalar(new int[]{s,sampledCharacterIdx}, 1.0f);		//Prepare next time step input
				sequenceOut[i] = sampledCharacterIdx;	//Add sampled character to StringBuilder (human readable output)
			}

			output = net.rnnTimeStep(nextInput);	//Do one time step of forward pass
		}


		return transformer.transformIndexSequence(sequenceOut);


	}

	//----------------------------------------------------------------------------

	/**
	 * Returns true if the neural network was trained, false if not
	 * @return
	 */
	public boolean getIsTrained ()
	{
		return isTrained;
	}

	/**
	 * Returns true if the sequence was loaded, false if not
	 * @return
	 */
	public boolean getIsDataLoaded ()
	{
		return isDataLoaded;
	}

	//----------------------------------------------------------------------------

	/** 
	 * Sample class from distribution which is the output of the NN
	 * @param distribution -- output of the NN
	 * @return
	 */
	public static int sampleFromDistribution( double[] distribution)
	{
		double d = 0.0;

		Random rng = new Random();
		double sum = 0.0;

		for( int t=0; t < 10; t++ ) 
		{
			d = rng.nextDouble();
			sum = 0.0;  
			
			for( int i = 0; i < distribution.length; i++ )
			{  	
				sum += distribution[i];
				if(d <= sum) return i;
			}
			//If we haven't found the right index yet, maybe the sum is slightly
			//lower than 1 due to rounding error, so try again.
		}

		throw new IllegalArgumentException("Distribution is invalid? d="+d+", sum="+sum);
	}



}
