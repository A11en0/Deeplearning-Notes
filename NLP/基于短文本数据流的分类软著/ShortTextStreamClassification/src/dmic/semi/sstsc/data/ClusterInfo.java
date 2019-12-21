package dmic.semi.sstsc.data;

public class ClusterInfo {

	public double[] centroid;
	public double radius;
	public int clusterClass;
	public int clusterNum;
	public ClusterInfo(double[] centroid, double radius, int clusterClass, int clusterNum)
	{
		super();
		this.centroid = centroid;
		this.radius = radius;
		this.clusterClass = clusterClass;
		this.clusterNum = clusterNum;
	}
	
}
