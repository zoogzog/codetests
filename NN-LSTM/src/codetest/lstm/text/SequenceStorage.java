package codetest.lstm.text;

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

@SuppressWarnings("serial")
public class SequenceStorage implements DataSetIterator 
{

	private int[] sequence = null; 
	private int sequenceMaxValue = 0;
	
	private int dimIN = 0;
	private int dimOUT = 0;
	
	private int DEFAULT_SEQUENCEMAXVALUE = 500;

	private LinkedList<Integer> setPointers = new LinkedList<>();
	private int setMinibatchSize = 0;
	private int setTrainSequenceLength = 0;

	//------------------------------------------------------------

	public void allocateMemory (int length)
	{
		sequence = new int[length];	
	}

	public void setSequenceMax (int value)
	{
		sequenceMaxValue = value;
		
		if (sequenceMaxValue > DEFAULT_SEQUENCEMAXVALUE)
		{
			dimIN = DEFAULT_SEQUENCEMAXVALUE;
			dimOUT = DEFAULT_SEQUENCEMAXVALUE;
		}
		else
		{
			dimIN = sequenceMaxValue;
			dimOUT = sequenceMaxValue;
		}
	}
	
	public void generateTrainingSet (int setMinibatchSize, int setTrainSequenceLength)
	{
		this.setMinibatchSize = setMinibatchSize;
		this.setTrainSequenceLength = setTrainSequenceLength;

		initializeOffsets();
	}
	
	private void initializeOffsets()
	{
		int nMinibatchesPerEpoch = (sequence.length - 1) / setTrainSequenceLength - 2;

		for (int i = 0; i < nMinibatchesPerEpoch; i++) 
		{
			setPointers.add(i * setTrainSequenceLength);
		}

		//FIXME is this causes error?
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
	
	public int getSequenceMax ()
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
	
	public int[] getFeatureVector (int sequenceElementValue)
	{
		if (sequenceMaxValue > DEFAULT_SEQUENCEMAXVALUE)
		{
			int[] output = new int[DEFAULT_SEQUENCEMAXVALUE];
			
			
		}
		else
		{
			int[] output = new int[sequenceMaxValue];
			
			for (int i = 0; i < output.length; i++)
			{
				if (i == sequenceElementValue) { output[i] = 1; }
				else { output[i] = 0; }
			}
			
			return output;
		}
		
		
		return null;
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
		
		
		INDArray input = Nd4j.create(new int[] { currMinibatchSize, sequenceMaxValue, setTrainSequenceLength}, 'f');
		INDArray labels = Nd4j.create(new int[] { currMinibatchSize, sequenceMaxValue, setTrainSequenceLength }, 'f');

		for (int i = 0; i < currMinibatchSize; i++) 
		{
			int startIdx = setPointers.removeFirst();
			int endIdx = startIdx + setTrainSequenceLength;
	

			int c = 0;
			for (int j = startIdx + 1; j < endIdx; j++, c++) 
			{
				int sequenceElementNext = sequence[j]; 
				int sequenceElementCurrent = sequence[startIdx]; 

				input.putScalar(new int[] { i, sequenceElementCurrent, c }, 1.0);
				labels.putScalar(new int[] { i, sequenceElementNext, c }, 1.0);
				
				
				sequenceElementCurrent = sequenceElementNext;
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

}
