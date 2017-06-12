package codetest.lstm.nn;

import java.io.File;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class NeuralNetworkLSTM 
{
	private MultiLayerConfiguration nnConfiguration = null;
	private MultiLayerNetwork nnNetwork = null;

	private final int lstmLayerSize = 200;					//Number of units in each GravesLSTM layer
	private final int tbpttLength = 50;                     //Length for truncated backpropagation through time. i.e., do parameter updates ever 50 characters

	public NeuralNetworkLSTM (int dimIn, int dimOut)
	{
		try
		{
	/*	nnConfiguration = new NeuralNetConfiguration.Builder()
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
				.learningRate(0.1)
				.rmsDecay(0.95)
				.seed(12345)
				.regularization(true)
				.l2(0.001)
				.weightInit(WeightInit.XAVIER)
				.updater(Updater.RMSPROP)
				.list()
				.layer(0, new GravesLSTM.Builder().nIn(dimIn).nOut(lstmLayerSize)
						.activation("tanh").build())
				.layer(1, new GravesLSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
						.activation("tanh").build())
				.layer(2, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation("softmax")        //MCXENT + softmax for classification
						.nIn(lstmLayerSize).nOut(dimOut).build())
				.backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
				.pretrain(false).backprop(true)
				.build();
	*/

			nnConfiguration = new NeuralNetConfiguration.Builder()
					.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
					.learningRate(0.1)
					.rmsDecay(0.95)
					.seed(12345)
					.regularization(true)
					.l2(0.001)
					.weightInit(WeightInit.XAVIER)
					.updater(Updater.RMSPROP)
					.list()
					.layer(0, new GravesLSTM.Builder().nIn(dimIn).nOut(lstmLayerSize)
							.activation("tanh").build())
					.layer(1, new GravesLSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
							.activation("tanh").build())
					.layer(2, new RnnOutputLayer.Builder(LossFunction.MSE).activation("sigmoid")        //MCXENT + softmax for classification
							.nIn(lstmLayerSize).nOut(dimOut).build())
					.backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
					.pretrain(false).backprop(true)
					.build();
			
		nnNetwork = new MultiLayerNetwork(nnConfiguration);
		nnNetwork.init();
		nnNetwork.setListeners(new ScoreIterationListener(1));
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	public MultiLayerNetwork getNetwork ()
	{
		return nnNetwork;
	}

	public void save (String path)
	{
		if (nnNetwork == null) { return; }
		
		try
		{
			System.out.println("Save NN to file: " + path);
			
			File locationToSave = new File(path);
			boolean saveUpdater = true;                                            
			ModelSerializer.writeModel(nnNetwork, locationToSave, saveUpdater);
		}
		catch (Exception e) { e.printStackTrace();} 
	}
	
	public void load (String path)
	{
		try
		{
			nnNetwork = ModelSerializer.restoreMultiLayerNetwork(path);
		}
		catch (Exception e) { e.printStackTrace();}
	}
}
