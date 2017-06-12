package codetest.lstm.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import codetest.lstm.text.FileTransformer;
import codetest.lstm.text.FileTransformerCharacter;
import codetest.lstm.text.FileTransformerWord;

@SuppressWarnings("serial")
public class SequenceStorage implements DataSetIterator 
{
	//---- Transformer is responsible for transforming the input data
	//---- in a text format into data, which can be understood by the NN
	private FileTransformer transformer = null;
	
	//---- Input sequence
	private int[] sequence = null; 

	//---- The maximum possible value of the input sequence, sequence[i] belongs to
	//---- the range between [0, to sequenceMaxValue]
	private int sequenceMaxValue = 0;

	//---- If sequenceMaxValue is too big (number of distinct classes is big)
	//---- we use different way of mapping integer values into feature vectors.
	//---- Instead of generating a feature vector, where element on i-th position
	//---- represents the class, we generate DEFAULT_SEQUENCEMAXVALUE dimensional vector
	//---- with some code consisted of 1-s.
	private int dimIN = 0;
	private int dimOUT = 0;


	//---- These are variables related to generation of a DataSet container for trainining 
	//---- the NN. The pointers set point to locations in the sequence form where to start
	//---- grabbing integers to generate a training set fo the minibatch
	private LinkedList<Integer> setPointers = new LinkedList<>();
	private int setMinibatchSize = 0;
	private int setTrainSequenceLength = 0;

	//------------------------------------------------------------

	public void allocateMemory (int length)
	{
		sequence = new int[length];	
	}

	public void setSequenceMax (int value, int dimIN, int dimOUT)
	{
		sequenceMaxValue = value;

		this.dimIN = dimIN;
		this.dimOUT = dimOUT;
	}

	public void loadSequence (String filePath, int setMinibatchSize, int setTrainSequenceLength)
	{
		transformer = new FileTransformerWord();
		transformer.transform(filePath, this);
		
		this.setMinibatchSize = setMinibatchSize;
		this.setTrainSequenceLength = setTrainSequenceLength;

		initializeOffsets();
	}

	private void initializeOffsets()
	{
		int nMinibatchesPerEpoch = (sequence.length - 1) / setTrainSequenceLength - 2;

		System.out.println("Task size: " + nMinibatchesPerEpoch);

		for (int i = 0; i < nMinibatchesPerEpoch; i++) 
		{
			setPointers.add(i * setTrainSequenceLength);
		}

		//FIXME is this causes error? SHould not
		Random rng = new Random ();

		Collections.shuffle(setPointers, rng);
	}

	//------------------------------------------------------------

	public void set (int index, int value)
	{
		if (index >= 0 && index < sequence.length)
		{
			sequence[index] = value;
		}
	}

	public int get (int index)
	{
		if (index >= 0 && index < sequence.length)
		{
			return sequence[index];
		}

		return -1;
	}

	public int getSequenceLength ()
	{
		return sequence.length;
	}

	public int getPointersLength ()
	{
		return setPointers.size();
	}

	public int getSequenceMaxValue ()
	{
		return sequenceMaxValue;
	}

	public int getDimIN ()
	{
		return dimIN;
	}

	public int getDimOUT ()
	{
		return dimOUT;
	}

	public int getRandomSequenceValue ()
	{
		int value = (int) (Math.random() * sequenceMaxValue);

		return value;
	}

	public int[] getRandomSequence (int size)
	{
		int[] output = new int[size];

		for (int i = 0; i < size; i++)
		{
			output[i] = getRandomSequenceValue();
		}

		return output;
	}

	//------------------------------------------------------------


	@Override
	public boolean hasNext() 
	{
		return setPointers.size() > 0;
	}

	@Override
	public DataSet next() 
	{
		return next(this.setMinibatchSize);
	}


	@Override
	public DataSet next(int num) 
	{
		if (setPointers.size() == 0) { throw new NoSuchElementException(); }

		int currMinibatchSize = Math.min(num, setPointers.size());

		// Allocate space:
		// Note the order here:
		// dimension 0 = number of examples in minibatch
		// dimension 1 = size of each vector (i.e., number of characters)
		// dimension 2 = length of each time series/example
		// Why 'f' order here? See http://deeplearning4j.org/usingrnns.html#data
		// section "Alternative: Implementing a custom DataSetIterator"
		INDArray input = Nd4j.create(new int[] { currMinibatchSize, dimIN, setTrainSequenceLength}, 'f');
		INDArray labels = Nd4j.create(new int[] { currMinibatchSize, dimOUT, setTrainSequenceLength }, 'f');

		for (int i = 0; i < currMinibatchSize; i++) 
		{
			int startIdx = setPointers.removeFirst();
			int endIdx = startIdx + setTrainSequenceLength;
			
			int c = 0;
			for (int j = startIdx + 1; j < endIdx; j++, c++) 
			{
				//---- What is the ID in the main sequence for the current element and next
				int sequenceElementNext = sequence[j]; 
				int sequecneElementCurrent = sequence[startIdx]; 
				
				//---- What is the encoded representation
				double[] seqEncodedIN = transformer.endocde(sequecneElementCurrent);
				double[] seqEncodedOUT = transformer.endocde(sequenceElementNext);
				
				//---- Store the encoded representation in the dataset
				for (int ep = 0; ep < seqEncodedIN.length; ep++) { input.putScalar(new int[] { i, ep, c }, seqEncodedIN[ep]); }
				for (int ep = 0; ep < seqEncodedIN.length; ep++) { 	labels.putScalar(new int[] { i, ep, c }, seqEncodedOUT[ep]); }
			
				sequecneElementCurrent = sequenceElementNext;
			}
		}

		return new DataSet(input, labels);
	}

	@Override
	public int totalExamples() 
	{
		return (sequence.length - 1) / setMinibatchSize - 2;
	}

	@Override
	public int inputColumns() 
	{
		return dimIN;
	}

	@Override
	public int totalOutcomes() 
	{

		return dimOUT;
	}

	@Override
	public void reset() 
	{
		setPointers.clear();
		initializeOffsets();
	}

	@Override
	public boolean resetSupported() 
	{
		return true;
	}

	@Override
	public boolean asyncSupported() 
	{
		return true;
	}

	@Override
	public int batch() 
	{
		return setMinibatchSize;
	}

	@Override
	public int cursor() 
	{
		return totalExamples() - setPointers.size();
	}

	@Override
	public int numExamples() 
	{
		return totalExamples();
	}

	@Override
	public void setPreProcessor(DataSetPreProcessor arg0) 
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public DataSetPreProcessor getPreProcessor() 
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<String> getLabels() 
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void remove() 
	{
		throw new UnsupportedOperationException();
	}

	//------------------------------------------------------------
	
	public int decode (double[] outputNN)
	{
		return transformer.decode(outputNN);
	}

	public double[] encode (int value)
	{
		return transformer.endocde(value);
	}
	
	public String transformIndexSequence (int[] sequence)
	{
		return transformer.transformIndexSequence(sequence);
	}
	
	//------------------------------------------------------------

	
	//------------------------------------------------------------
	
	public void debugPrint ()
	{
		System.out.println("-------------------------------------");
		System.out.println("Sequence size: " + sequence.length);
		System.out.println("Sequence max value: " + sequenceMaxValue);

		System.out.println("Sequence dim in: " + dimIN);
		System.out.println("Sequence dim out: " + dimOUT);

		System.out.println("Pointers list size: " + setPointers.size());
		System.out.println("Minibatch: " + setMinibatchSize);
		System.out.println("Train sequence length: " + setTrainSequenceLength);
		System.out.println("-------------------------------------");
		System.out.println("Method totalExamples: " + totalExamples());
		System.out.println("Method inputColumns: " + inputColumns());
		System.out.println("Method totalOutcomes: " + totalOutcomes());
		System.out.println("Method batch: " + batch());
		System.out.println("Method cursor: " + cursor());
		System.out.println("Method numExamples: " + numExamples());
		System.out.println("-------------------------------------");


	}
}