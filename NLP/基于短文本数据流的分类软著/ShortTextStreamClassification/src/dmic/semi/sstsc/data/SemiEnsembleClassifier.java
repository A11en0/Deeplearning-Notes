package dmic.semi.sstsc.data;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import dmic.stsc.tools.OperationTools;
import dmic.stsc.tools.SVMTools;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

public class SemiEnsembleClassifier {

	LinkedList<Classifier> classifiers;
	LinkedList<Cluster> clusters;
	
	int classifierNum;
	int clusterNum;
	
	int cost;
	String svmType;
	String kernelType;
	svm_parameter param;
	String model_file_name;
	int chunkSize;
	int classNum;
	int featureNum;
	double threshold;
	
	int curClassifierNum;
	int curClusterNum;
	
	List<String> clas = new ArrayList<String>();
	InstanceConstruction ic;
	
	public SemiEnsembleClassifier(int classifierNum, int clusterNum)
	{
		super();
		this.classifierNum = classifierNum;
		this.clusterNum = clusterNum;
		classifiers = new LinkedList<Classifier>();
		clusters = new LinkedList<Cluster>();
		curClassifierNum = 0;
		curClusterNum = 0;
	}
	
	public void init()
	{
		ic = new InstanceConstruction(featureNum, clas);
		initParam();
	}
	
	
	
	public double classify(List<String> data, List<Integer> classId, String label)
	{
		double accuracy = 0.0;
		double[][] predictResults = new double[data.size()][classNum];
		// kmeans cluster
		Instances insts = ic.getInstances(data, classId);
		int[] instsCluster = new int[data.size()];
		Vector<Double> vy = new Vector<Double>(data.size());
		Vector<svm_node[]> vx = new Vector<svm_node[]>(data.size());
		int max_index = SVMTools.getClassifyData(data, classId, vy, vx);
		try {
			SimpleKMeans skmeans = new SimpleKMeans();
			skmeans.setNumClusters(classNum);
			skmeans.buildClusterer(insts);
			ClusterInfo[] newClusterInfo = getClusterInfo(skmeans, insts, instsCluster);
			// predict
			if (curClassifierNum > 0)
			{
				//classifier
				ClusterDistanceCalculation[] casArr = new ClusterDistanceCalculation[curClassifierNum];
				double[][] allClassifierSims = null;
				for (int i=0; i<curClassifierNum; i++)
				{
					Classifier classifier = classifiers.get(i);
					if (allClassifierSims==null)
					{
						allClassifierSims = new double[newClusterInfo.length][classifier.classNum];
					}
					
					casArr[i] = new ClusterDistanceCalculation(insts.numInstances(),
							newClusterInfo, classifier.vy.size(), classifier.clusterInfos);
					casArr[i].compute();
					OperationTools.add2DimArr(allClassifierSims, casArr[i].getSims());

				}
				if (allClassifierSims!= null)
				{
					OperationTools.zeroToOne2(allClassifierSims);
				}
				for (int i=0; i<curClassifierNum; i++)
				{
					Classifier classifier = classifiers.get(i);
					double[][] preResult = classifier.predict(vy, vx);  //inst*class
					double[][] sims = casArr[i].getSims();
					double cpValue = casArr[i].getCpValue();
					if (cpValue<threshold)
					{
						for (int j=0; j<instsCluster.length; j++)
						{
							int newK = instsCluster[j];
							for (int k=0; k<preResult[j].length; k++)
							{
								double d = sims[newK][k]/allClassifierSims[newK][k];
								preResult[j][k] = preResult[j][k] * d;
								predictResults[j][k] += preResult[j][k];
							}
						}
						
					}
					
				}
				// cluster
				ClusterDistanceCalculation[] cusArr = new ClusterDistanceCalculation[curClusterNum];
				double[][] allClusterSims = null;
				for (int i=0; i<curClusterNum; i++)
				{
					
					Cluster cluster = clusters.get(i);
					if (allClusterSims==null)
					{
						allClusterSims = new double[newClusterInfo.length][cluster.clusterInfos.length];
					}
					cusArr[i] = new ClusterDistanceCalculation(insts.numInstances(),
							newClusterInfo, cluster.instNum, cluster.clusterInfos);
					cusArr[i].compute();
					OperationTools.add2DimArr(allClusterSims, cusArr[i].getSims());
					
				}
				if (allClusterSims != null)
				{
					OperationTools.zeroToOne2(allClusterSims);
				}
				for (int i=0; i<curClusterNum; i++)
				{
					Cluster cluster = clusters.get(i);
					double[][] preResult = cluster.predict(insts);
					double[][] sims = cusArr[i].getSims();
					double cpValue = cusArr[i].getCpValue();
					if (cpValue<threshold)
					{
						// weight * cluster result
						for (int j=0; j<instsCluster.length; j++)
						{
							int newK = instsCluster[j];
							for (int k=0; k<preResult[j].length; k++)
							{
								preResult[j][k] = preResult[j][k] * 
										(sims[newK][k]/allClusterSims[newK][k]);
							}
						}
						// class result
						double[][] cluster2Class = cluster.cluster2Class;
						for (int j=0; j<preResult.length; j++)
						{
							for (int k=0; k<classNum; k++)
							{
								double vs = 0.0;
								for (int n=0; n<preResult[j].length; n++)
								{
									vs += cluster2Class[n][k] * preResult[j][n];
								}
								predictResults[j][k] += vs;
							}
						}
						
					}
				}
				// predict result
				accuracy = getAccuracy(predictResults, vy);
			}
			//build model
			if (label.equals("1"))
			{
				// classifier
				Classifier newClassifier = new Classifier(param, max_index, vy, vx, classNum, featureNum);
				if (curClassifierNum==classifierNum)
				{
					classifiers.removeFirst();				
				} else {
					curClassifierNum++;
				}
				classifiers.addLast(newClassifier);
				
			}
			if (label.equals("0")&&classifiers.size()>0)
			{
				// cluster
				Cluster newCluster = new Cluster(skmeans, newClusterInfo, insts.numInstances());
				newCluster.propagateLabel(classifiers, classNum);
				if (curClusterNum==clusterNum)
				{
					clusters.removeFirst();			
				} else {
					curClusterNum ++;
				}
				clusters.addLast(newCluster);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		return accuracy;		
	}
	private double getAccuracy(double[][] predictResults, Vector<Double> vy) 
	{
		int correctNum = 0;
		int totalNum = 0;
		for (int i=0; i<predictResults.length; i++)
		{
			double maxValue = Double.MIN_VALUE;
			int inx = -1;
			for (int j=0; j<predictResults[i].length; j++)
			{
				if (maxValue<predictResults[i][j])
				{
					maxValue = predictResults[i][j];
					inx = j;
				}
			}
			if (vy.get(i) == (double)inx)
			{
				correctNum++;
			}
			totalNum++;
		}
		return (double)correctNum/totalNum;
	}
	

	private ClusterInfo[] getClusterInfo(SimpleKMeans skmeans, Instances data, int[] instsCluster)
	{
		int clustersNum = skmeans.getNumClusters();
		Instances[] clusterInsts = new Instances[clustersNum];
		for (int i=0; i<data.numInstances(); i++)
		{
			try {
				int inx = skmeans.clusterInstance(data.get(i));
				instsCluster[i] = inx;
				if (clusterInsts[inx]==null)
				{
					clusterInsts[inx] = data.stringFreeStructure();
				}
				clusterInsts[inx].add(data.get(i));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ClusterInfo[] clusterInfos = new ClusterInfo[clustersNum];
		Instances clusterCentroids = skmeans.getClusterCentroids();
		if (clusterCentroids==null || clusterCentroids.size()==0)
		{
			System.err.println("簇中心为null或者没有值");
			System.exit(1);
		}
		for (int i=0; i<clustersNum; i++)
		{
			Instance inst = clusterCentroids.get(i);
			double[] centroidsValues = new double[clusterCentroids.numAttributes()-1];
			for (int j=0; j<inst.numAttributes()-1; j++)
			{
				centroidsValues[j] = inst.value(j);
			}
			double radiusValue = 0.0;
			Instances clusterInst = clusterInsts[i];
			int clusterInstNum = clusterInst.numInstances();
			for (int j=0; j<clusterInstNum; j++)
			{
				radiusValue += calculateEuclideanDist(centroidsValues,clusterInst.get(j));
			}
			if (clusterInstNum > 0)
			{
				radiusValue /= clusterInstNum;
			}
			clusterInfos[i] = new ClusterInfo(centroidsValues, radiusValue, i, clusterInstNum);
			
		}
		return clusterInfos;
	}
	
	private double calculateEuclideanDist(double[] ds, Instance instance)
	{
		double dist = 0.0;
		for (int i=0; i<instance.numAttributes()-1; i++)
		{
			dist += Math.pow((ds[i]-instance.value(i)), 2);
		}
		return Math.sqrt(dist);
	}
	private void initParam() 
	{

		int i;
		svm_print_interface print_func = null;	// default printing to stdout

		param = new svm_parameter();
		// default values
		if (svmType.equals("C-SVM"))
		{
			param.svm_type = svm_parameter.C_SVC;
		}
		if (kernelType.equals("linear"))
		{
			param.kernel_type = svm_parameter.LINEAR;
		}
		if (kernelType.equals("polynomial"))
		{
			param.kernel_type = svm_parameter.POLY;
		}
		if (kernelType.equals("radial basis function"))
		{
			param.kernel_type = svm_parameter.RBF;
		}
		if (kernelType.equals("sigmoid"))
		{
			param.kernel_type = svm_parameter.SIGMOID;
		}
		if (kernelType.equals("precomputed kernel"))
		{
			param.kernel_type = svm_parameter.PRECOMPUTED;
		}
		param.C = cost;
		param.degree = 3;
		param.gamma = 0;	// 1/num_features
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 1;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public void setKernelType(String kernelType) {
		this.kernelType = kernelType;
	}
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	public void setClassifierNum(int classifierNum) {
		this.classifierNum = classifierNum;
	}
	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}
	public void setClassNum(int classNum) {
		this.classNum = classNum;
	}
	public void setFeatureNum(int featureNum) {
		this.featureNum = featureNum;
	}
	public void setClas(List<String> clas) {
		this.clas = clas;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public void setSvmType(String svmType) {
		this.svmType = svmType;
	}
	
}
