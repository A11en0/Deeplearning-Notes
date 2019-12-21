package dmic.sstsc.data;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.JTextArea;

import dmic.stsc.tools.FileTools;
import dmic.stsc.tools.OperationTools;
import dmic.stsc.tools.SVMTools;
import dmic.stsc.tools.svmModel;
import dmic.stsc.tools.svm_predict;
import dmic.stsc.tools.svm_train;
import libsvm.svm_node;
import libsvm.svm_parameter;


public class EnsembleClassification {
	private String fileDir;
	private List<Integer> classId;

	private svm_parameter param;
	private String model_file_name;
	
	private svmModel[] models;
	private int classifierNum;
	private int chunkNum;
	private int classNum;
	private int daysNum;
	private int featureNum;
	private int currentClassifierNum = 0;
	private double threshold;
	
	JTextArea printResultJTextArea;
	
//	public EnsembleClassification(String fileDir, List<Integer> classId) {
//		super();
//		this.fileDir = fileDir;
//		this.classId = classId;
//	}

	public EnsembleClassification(String fileDir, List<Integer> classId, svm_parameter param)
	{
		super();
		this.fileDir = fileDir;
		this.classId = classId;
		this.param = param;
	}

	public void classify()
	{
		models = new svmModel[classifierNum];
		String filename = fileDir+"k"+String.valueOf(featureNum)+".day";
		int s=0, e=0;
		for (int i=0; i<daysNum; i++)
		{
			// prepare data
			printResultJTextArea.append("---第"+String.valueOf(i+1)+"数据块---\n");
			List<String> data = FileTools.readFile(filename+String.valueOf(i)+".pz_d");
			s = e;
			e = (i+1)*chunkNum;
			e = (e>classId.size()) ? classId.size() : e;
			Vector<Double> vy = new Vector<Double>(data.size());
			Vector<svm_node[]> vx = new Vector<svm_node[]>(data.size());
			int max_index = SVMTools.getClassifyData(data, classId.subList(s, e), vy, vx);
			
			//预测新数据块并返回概念漂移检测结果
			double[] cpResult = predict(vy, vx);

			// 根据概念漂移结果解析集成模型的更新方向
			int inx = 0;
			int old = Integer.MIN_VALUE;
			int cpNum = 0;
			if (currentClassifierNum>0)
			{
				for (int j=0; j<currentClassifierNum; j++)
				{
					// 发生概念漂移
					if(cpResult[j]>threshold)
					{
						cpNum++;
						if (old<models[j].timestamps)
						{
							old = models[j].timestamps;
							inx = j;  //最老的基分类器索引
						}
					}
				}
				// 新数据块相对于集成模型的所有数据块并没有都发生概念漂移，选择相似度最高的，即语义距离最近的数据块替换
				if (cpNum!=currentClassifierNum) 
				{
					inx = OperationTools.minInx(cpResult); //最相似的基分类器索引
				} else {
					// 新数据块相对于集成模型的所有数据块均发生概念漂移，且集成模型尚未满
					printResultJTextArea.append("该数据块相对于用于构建集成模型的所有数据块均发生概念漂移\n");
					if (currentClassifierNum<classifierNum)
					{
						inx = currentClassifierNum++; //集成模型未满时，加入的基分类器索引
					} else{
						System.out.println("csaf");
					}
					// 新数据块相对于集成模型的所有数据块均发生概念漂移，且集成模型已经满了，替换最老的，这里原有的inx值不变
				}
			} else {
				currentClassifierNum ++;
				inx = 0;
			}
			
			//构建基分类器，并更新集成模型
			svm_train st = new svm_train(param);
			try {
				st.run(vx, vy, max_index);
				svmModel sv = new svmModel(st.get_model(), 0, vy, vx);
				models[inx] = sv;
				for (int j=0; j<currentClassifierNum; j++)
				{
					models[j].timestamps++;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
	
	private double[] predict(Vector<Double> vy, Vector<svm_node[]> vx)
	{
		double[] cp = new double[classifierNum];
		for (int i=0; i<classifierNum; i++)
		{
			cp[i] = Double.MAX_VALUE;
		}
		int instNum = vy.size();
		double resl = 0.0;
		if (currentClassifierNum>0)
		{
			// 集成分类器结果
			double[][] predictResults = new double[instNum][classNum];
			for (int i=0; i<currentClassifierNum; i++)
			{
				svm_predict sp =new svm_predict(models[i].model, classNum);
				sp.predict(vy, vx);
				double[][] zpredictResults = sp.get_predict_result();
				DistanceCalculation cs = new DistanceCalculation(models[i], vx);
				cs.compute();				
				double[] weights = cs.getPredictWeight();
				// 分类器结果相加
				for (int j=0; j<instNum; j++)
				{
					for(int k=0; k<classNum; k++)
					{
						predictResults[j][k] += (weights[j] * zpredictResults[j][k]);
					}
				}
				cp[i] = cs.getChunkDistance();
			}
			
			int total = 0;
			int correct = 0;
			for(int i=0; i<instNum; i++)
			{
				double inx = OperationTools.maxInx(predictResults[i]);
				double target = vy.get(i);
				if (target==inx)
				{
					correct ++;
				}
				total ++;
			}
			resl = (double)correct/total;
			System.out.println("预测结果"+String.valueOf(resl));
		}
		printResultJTextArea.append("预测正确率："+String.valueOf((double)resl)+"\n");
		printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length()); 
		return cp;
	}

//	private double max(double[] arr) 
//	{
//		double maxV = Double.MIN_EXPONENT;
//		double inx = 0;
//		for (int i=0; i<arr.length; i++)
//		{
//			if (maxV<arr[i])
//			{
//				maxV = arr[i];
//				inx = i;
//			}
//		}
//		return inx;
//	}
	
//	private int min(double[] arr) 
//	{
//		double minV = Double.MAX_VALUE;
//		int inx = 0;
//		for (int i=0; i<arr.length; i++)
//		{
//			if (minV>arr[i])
//			{
//				minV = arr[i];
//				inx = i;
//			}
//		}
//		return inx;
//	}

//	private int getClassifyData(List<String> data, List<Integer> ids, Vector<Double> vy, Vector<svm_node[]> vx)
//	{
//		int max_index = 0;
//		for (int i=0; i<data.size(); i++)
//		{
//			String line = data.get(i);
//			String[] values = line.split(" ");
//			svm_node[] x = new svm_node[values.length];
//			for(int k=0;k<values.length;k++)
//			{
//				x[k] = new svm_node();
//				x[k].index = k;
//				x[k].value = SVMTools.atof(values[k]);
//			}
//			if(values.length>0)
//			{
//				max_index = Math.max(max_index, x[values.length-1].index);
//			}
//			vx.addElement(x);
//			vy.addElement((double)ids.get(i));
//		}
//		return max_index;
//	}

	public int getClassifierNum() {
		return classifierNum;
	}
	public void setClassifierNum(int classifierNum) {
		this.classifierNum = classifierNum;
	}

	public svm_parameter getParam() {
		return param;
	}

	public void setParam(svm_parameter param) {
		this.param = param;
	}

	public int getChunkNum() {
		return chunkNum;
	}

	public void setChunkNum(int chunkNum) {
		this.chunkNum = chunkNum;
	}

	public int getClassNum() {
		return classNum;
	}

	public void setClassNum(int classNum) {
		this.classNum = classNum;
	}

	public int getDaysNum() {
		return daysNum;
	}

	public void setDaysNum(int daysNum) {
		this.daysNum = daysNum;
	}

	public int getFeatureNum() {
		return featureNum;
	}

	public void setFeatureNum(int featureNum) {
		this.featureNum = featureNum;
	}

	public JTextArea getPrintResultJTextArea() {
		return printResultJTextArea;
	}

	public void setPrintResultJTextArea(JTextArea printResultJTextArea) {
		this.printResultJTextArea = printResultJTextArea;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	
	

}
