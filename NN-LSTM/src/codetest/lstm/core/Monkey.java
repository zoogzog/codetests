package codetest.lstm.core;

import java.util.Random;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import codetest.lstm.gui.CallbackIteration;
import codetest.lstm.nn.NeuralNetworkLSTM;
import codetest.lstm.text.FileTransformer;
import codetest.lstm.text.FileTransformerCharacter;
import codetest.lstm.text.FileTransformerWord;
import codetest.lstm.text.SequenceStorage;

public class Monkey 
{
	private NeuralNetworkLSTM netC = null;

	private boolean isTrained = false;
	private boolean isDataLoaded = false;

	private int dimIN = 0;
	private int dimOUT = 0;

	private Random rng;

	int nCharactersToSample = 300;	

	int setMinibatchSize = 0;
	int setSequenceLength = 0;
	int setEpochs = 0;


	FileTransformer transformer = new FileTransformerWord();
			//FileTransformerCharacter();
	SequenceStorage storage  = new SequenceStorage();

	//----------------------------------------------------------------------------

	public Monkey ()
	{
		rng = new Random(12345);
	}

	//----------------------------------------------------------------------------

	public void loadData (String filePath, int miniBatchSize, int sequenceLength)
	{
		try
		{
			System.out.println("TRACE 1");
			transformer.transform(filePath, storage);
			storage.generateTrainingSet(miniBatchSize, sequenceLength);
			


			System.out.println("TRACE 2");
			dimIN = storage.getDimIN();
			dimOUT = storage.totalOutcomes();
			
			//----- Check here internal structure, all functions + resets of iter and storage!
			//----- SHOULD be the same
			
			isDataLoaded = true;

			System.out.println("TRACE 3: " + dimIN + " " + dimOUT);
			netC = new NeuralNetworkLSTM(dimIN, dimOUT);

			
			System.out.println("TRACE 4");
			setMinibatchSize = miniBatchSize;
			setSequenceLength = sequenceLength;
		}
		catch (Exception e) { e.printStackTrace();}
	}

	public void train (int numEpochs, CallbackIteration callback)
	{
		System.out.println("TRAINING!");
		if (isDataLoaded)
		{
			
			System.out.println("Shit is loaded");
			isTrained = true;

	
			int maxSteps = (numEpochs * storage.getPointersLength()) / setMinibatchSize;

			int step = 0;

			for( int i=0; i<numEpochs; i++ )
			{
				//while(iter.hasNext())
				System.out.println("Epoch " + i);
				while(storage.hasNext())
				{
					if (callback != null) 
					{ 

						callback.callbackIterationDone(storage.getSequenceLength(), step, maxSteps); step++;
					}

					
					System.out.println("here");
					
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
			return sampleCharactersFromNetwork(null, netC.getNetwork(), rng, nCharactersToSample);
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

	private String sampleCharactersFromNetwork(int[] initSeq, MultiLayerNetwork net,
			Random rng, int charactersToSample)
	{

		if (initSeq == null) 
		{ 
			initSeq = new int[1]; 
			initSeq[0] = (int) (rng.nextDouble() * storage.getSequenceMax());
		}
		
		
		
		INDArray initializationInput = Nd4j.zeros(1, storage.inputColumns(), initSeq.length);
		

		for( int i=0; i<initSeq.length; i++ )
		{
			initializationInput.putScalar(new int[]{0,initSeq[i],i}, 1.0f);
		}
		
		int [] textID = new int[charactersToSample];

		
		net.rnnClearPreviousState();
		
		INDArray output = net.rnnTimeStep(initializationInput);
		output = output.tensorAlongDimension(output.size(2)-1,1,0);	//Gets the last time step output

		for( int i=0; i<charactersToSample; i++ )
		{
			//Set up next input (single time step) by sampling from previous output
			INDArray nextInput = Nd4j.zeros(1,storage.inputColumns());
			//Output is a probability distribution. Sample from this for each example we want to generate, and add it to the new input

			double[] outputProbDistribution = new double[storage.totalOutcomes()];
			for( int j=0; j<outputProbDistribution.length; j++ ) outputProbDistribution[j] = output.getDouble(0,j);
			int sampledCharacterIdx = sampleFromDistribution(outputProbDistribution,rng);

			nextInput.putScalar(new int[]{0,sampledCharacterIdx}, 1.0f);		

			textID[i] = sampledCharacterIdx;

			output = net.rnnTimeStep(nextInput);	//Do one time step of forward pass
		};


		return new String(transformer.transformIndexSequence(textID));
	}



	/** Given a probability distribution over discrete classes, sample from the distribution
	 * and return the generated class index.
	 * @param distribution Probability distribution over classes. Must sum to 1.0
	 */
	public static int sampleFromDistribution( double[] distribution, Random rng )
	{
		double d = rng.nextDouble();
		double sum = 0.0;

		for( int i=0; i<distribution.length; i++ )
		{
			sum += distribution[i];
			if( d <= sum ) return i;
		}
		//Should never happen if distribution is a valid probability distribution
		throw new IllegalArgumentException("Distribution is invalid? d="+d+", sum="+sum);
	}
	

}
