package dmic.sstsc.data;

import java.util.Map;
import java.util.Vector;

import dmic.stsc.tools.OperationTools;
import dmic.stsc.tools.svmModel;
import libsvm.svm_node;

public class DistanceCalculation
{

	private svmModel model;
	private Vector<svm_node[]> test_vx;
	private double[] shortTextDistances;
	private double chunkDistance;

	public DistanceCalculation(svmModel model, Vector<svm_node[]> test_vx) {
		super();
		this.model = model;
		this.test_vx = test_vx;
	}

	public void compute()
	{
		chunkDistance = 0;
		shortTextDistances = new double[test_vx.size()];		
		Map<Double, Vector<svm_node[]>> label_vy = model.label_vy;
		for(int i=0; i<test_vx.size(); i++)
		{
			double[] clusterSimilarities = new double[label_vy.size()]; 
			int c=0;
			for(Vector<svm_node[]> cluster : label_vy.values())
			{
				double clusterSimilarity = 0;
				double w = (double)cluster.size()/model.vx.size();
				for(int k=0; k<cluster.size(); k++)
				{
					double dist = 1 - (w * cosine(test_vx.elementAt(i), cluster.elementAt(k)));
					clusterSimilarity += dist;
				}
				clusterSimilarity /= cluster.size();
				clusterSimilarities[c++] = clusterSimilarity;
			}
			shortTextDistances[i] = OperationTools.minValue(clusterSimilarities);
			chunkDistance += shortTextDistances[i];
		}
		chunkDistance /= test_vx.size();
	}

	public double[] getPredictWeight()
	{
		double[] weights = new double[test_vx.size()];
		for (int i=0; i<test_vx.size(); i++)
		{
			weights[i] = (1-shortTextDistances[i])*(1-chunkDistance);
		}
		return weights;
	}
	
	private double cosine(svm_node[] x, svm_node[] y)
	{
		if(x.length != y.length)
		{
			System.err.println("test set has different dimension with train set.");
			System.exit(1);
		}
			
		double cos = 0.0;
		double xys = 0.0, x2s = 0.0, y2s = 0.0;
		for(int i=0; i<x.length; i++)
		{
			xys += x[i].value * y[i].value;
			x2s += x[i].value * x[i].value;
			y2s += y[i].value * y[i].value;
		}
		
		cos = (double)xys / (Math.sqrt(x2s) * Math.sqrt(y2s));
		return cos;
	}
	
//	private double min(double[] arrs) 
//	{		
//		double min_value = Integer.MAX_VALUE;		
//		for(int i=0; i<arrs.length; i++)
//		{
//			if(arrs[i]<min_value)
//				min_value = arrs[i];
//		}
//		return min_value;
//	}

	public double[] getShortTextDistances() {
		return shortTextDistances;
	}

	public double getChunkDistance() {
		return chunkDistance;
	}

	
	
}
