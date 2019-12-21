package dmic.semi.sstsc.data;

import java.io.IOException;
import java.util.Vector;

import dmic.stsc.tools.svmModel;
import dmic.stsc.tools.svm_predict;
import dmic.stsc.tools.svm_train;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

public class Classifier extends svmModel
{
	public ClusterInfo[] clusterInfos;
	public int classNum;
	public int featureNum;
	
//	public Classifier(svm_model model, int timestamps, int classNum, int featureNum) 
//	{
//		super(model, timestamps);
//		this.classNum = classNum;
//		this.featureNum = featureNum;
//	}
	
//	public Classifier(svm_model model, int timestamps, Vector<Double> vy, Vector<svm_node[]> vx, int classNum, int featureNum) 
//	{
//		super(model, timestamps, vy, vx);
//		this.classNum = classNum;
//		this.featureNum = featureNum;
//	}
	
	public Classifier(svm_parameter param, int max_index, Vector<Double> vy, Vector<svm_node[]> vx, int classNum, int featureNum)
	{
		super();
		this.classNum = classNum;
		this.featureNum = featureNum;
		svm_train st = new svm_train(param);
		try {
			st.run(vx, vy, max_index);
			this.model = st.get_model();
			this.timestamps = 0;
			this.vx = vx;
			this.vy = vy;
			divideLabel();
			getClusterInfos();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void getClusterInfos() 
	{
		double[][] clusterCentroids = new double[classNum][featureNum];
		double[] clusterRadius = new double[classNum];
		getClusterCentroids(clusterCentroids);
		getClusterRadius(clusterCentroids, clusterRadius);

		clusterInfos = new ClusterInfo[classNum];
		for (int i=0; i<classNum; i++)
		{
			if (label_vy.containsKey((double)i))
			{
				clusterInfos[i] = new ClusterInfo(clusterCentroids[i], clusterRadius[i], 
						i, label_vy.get((double)i).size());
			} else {
				clusterInfos[i] = new ClusterInfo(clusterCentroids[i], clusterRadius[i], 
						i, 0);
			}
		}
	}

	private void getClusterCentroids(double[][] clusterCentroids)
	{
		for(int i=0; i<classNum; i++)
		{
			for (int j=0; j<featureNum; j++)
			{
				clusterCentroids[i][j] = 0.0;
			}
		}
		
		for (double key : label_vy.keySet())
		{
			int inx = (int)key;
			Vector<svm_node[]> values = label_vy.get(key);
			for (int i=0; i<values.size(); i++)
			{
				svm_node[] nodes = values.get(i);
				for (int j=0; j<nodes.length; j++)
				{
					clusterCentroids[inx][nodes[j].index] += nodes[j].value;
				}
			}
			for (int i=0; i<featureNum; i++)
			{
				clusterCentroids[inx][i] /= values.size();
			}
		}
		
	}

	private void getClusterRadius(double[][] clusterCentroids, double[] clusterRadius) 
	{
		for (int i=0; i<classNum; i++)
		{
			clusterRadius[i] = 0.0;
		}
		for (double key : label_vy.keySet())
		{
			int inx = (int)key;
			Vector<svm_node[]> values = label_vy.get(key);
			for (int i=0; i<values.size(); i++)
			{
				svm_node[] nodes = values.get(i);
				clusterRadius[inx] += calculateEuclideanDist(clusterCentroids[inx], values.get(i));
			}
			if (values.size() > 0)
			{
				clusterRadius[inx] /= values.size();
			}
		}
		
	}

	private double calculateEuclideanDist(double[] ds, svm_node[] nodes)
	{
		double dist = 0.0;
		for (int i=0; i<nodes.length; i++)
		{
			int inx = nodes[i].index;
			dist += Math.pow((ds[inx]-nodes[i].value), 2);
		}
		return Math.sqrt(dist);
	}

	public double[][] predict(Vector<Double> tvy, Vector<svm_node[]> tvx)
	{
		svm_predict sp =new svm_predict(this.model, classNum);
		sp.predict(tvy, tvx);
		return sp.get_predict_result();
	}
	
}
