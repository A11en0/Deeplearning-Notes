package dmic.semi.sstsc.data;

import dmic.stsc.tools.OperationTools;

public class ClusterDistanceCalculation {

	private ClusterInfo[] newClusterInfo;
	private ClusterInfo[] clusterInfos;
	private int newInstNum;
	private int instNum;
	private double[][] sims;
	private double cpValue;

	
	public ClusterDistanceCalculation(int newInstNum, ClusterInfo[] newClusterInfo, int instNum, ClusterInfo[] clusterInfos) {
		super();
		this.newClusterInfo = newClusterInfo;
		this.clusterInfos = clusterInfos;
		this.newInstNum = newInstNum;
		this.instNum = instNum;
		sims = new double[newClusterInfo.length][clusterInfos.length];
	}

	public void compute()
	{
		cpValue = 0.0;
		for (int i=0; i<newClusterInfo.length; i++)
		{
			double[] wdists = new double[clusterInfos.length];
			for (int j=0; j<clusterInfos.length; j++)
			{
				sims[i][j] = calculateSimilarity(newClusterInfo[i], clusterInfos[j]);
				if (sims[i][j] > 0)
				{
					wdists[j] = ((double)clusterInfos[j].clusterNum/instNum) *(1.0/sims[i][j]);
				} else {
					wdists[j] = Double.MAX_VALUE;
				}
			}
			double newCluster2Clusters = OperationTools.minValue(wdists);
			cpValue += ((double)newClusterInfo[i].clusterNum * newCluster2Clusters);
		}
		cpValue /= newInstNum;
//		return cpValue;
	}

	private double calculateSimilarity(ClusterInfo ci1, ClusterInfo ci2)
	{
		double sim = 0.0;
		if (ci1!=null&&ci2!=null)
		{
			double dist = calculateEuclideanDist(ci1.centroid, ci2.centroid);
			if (dist>0)
			{
				sim = (ci1.radius+ci2.radius)/dist;
			}
		}
		return sim;
	}

	private double calculateEuclideanDist(double[] ds, double[] es)
	{
		double dist = 0.0;
		for (int i=0; i<ds.length; i++)
		{
			dist += Math.pow((ds[i]-es[i]), 2);
		}
		return Math.sqrt(dist);
	}

	public double[][] getSims() {
		return sims;
	}

	public double getCpValue() {
		return cpValue;
	}
	
}
