package codetest.lstm.nn;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;

public class ClassifierKMeans 
{
	private   ClusterSet cs;
	private List<Cluster> cl;

	boolean isTrained = false;

	private final static  String DISTANCE_FUNCTION = "euclidean";

	public void run (int classCount, int iterationMax, List<INDArray> vectorSet)
	{

		
		List<Point> pointsLst = Point.toPoints(vectorSet);
		
		
		System.out.println("PList: " + pointsLst.size());
		

		KMeansClustering kmc = KMeansClustering.setup(classCount, iterationMax, DISTANCE_FUNCTION);

		cs = kmc.applyTo(pointsLst);



		isTrained = true;
		
		
		cl = cs.getClusters();
	}

	public int getClass (double[] vector)
	{
		if (!isTrained) { return -1; }

		Point newpoint = new Point("myid", "mylabel", vector);
		PointClassification pc = cs.classifyPoint(newpoint);

		return  cl.indexOf(pc.getCluster());


	}
	
	public int getClass (INDArray vector)
	{
		if (!isTrained) { return -1; }
		
		Point newpoint = new Point("myid", "mylabel", vector);
		PointClassification pc = cs.classifyPoint(newpoint);
		
		return  cl.indexOf(pc.getCluster());
	}


}
