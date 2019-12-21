package dmic.semi.sstsc.data;

import java.util.LinkedList;

import dmic.stsc.tools.OperationTools;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class Cluster {

	public SimpleKMeans cluster;
	public ClusterInfo[] clusterInfos;
	public int instNum;
	public int clusterNum;
	
	public double[][] cluster2Class;
	
	
	public Cluster(SimpleKMeans cluster, ClusterInfo[] clusterInfos, int instNum)
	{
		super();
		this.cluster = cluster;
		this.clusterInfos = clusterInfos;
		this.instNum = instNum;
		this.clusterNum = cluster.getNumClusters();
	}


	public double[][] predict(Instances data)
	{
		// cluster newinst
		double[][] preResult = new double[data.numInstances()][clusterNum];
		//SimpleKMeans prediction, then multply cluster2Class
		for (int i=0; i<data.numInstances(); i++)
		{
			try {
				int c = cluster.clusterInstance(data.get(i));
				preResult[i][c] = 1;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return preResult;
	}


	public void propagateLabel(LinkedList<Classifier> classifiers, int classNum)
	{
		cluster2Class = null;
		for (int i=0; i<classifiers.size(); i++)
		{
			Classifier classifier = classifiers.get(i);
			if (cluster2Class==null)
			{
				cluster2Class = new double[clusterInfos.length][classifier.clusterInfos.length];
			}
			ClusterDistanceCalculation cdc = new ClusterDistanceCalculation(instNum, clusterInfos,
					 classifier.vy.size(), classifier.clusterInfos);
			cdc.compute();
			double[][] sims = cdc.getSims();
			OperationTools.add2DimArr(cluster2Class, sims);
		}
		OperationTools.division(cluster2Class, OperationTools.addColumn(cluster2Class));
		
	}
}
