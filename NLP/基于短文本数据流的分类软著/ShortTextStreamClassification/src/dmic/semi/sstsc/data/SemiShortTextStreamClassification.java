package dmic.semi.sstsc.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import dmic.stsc.tools.FileTools;

public class SemiShortTextStreamClassification {

	private JTextArea printResultJTextArea;
	public JProgressBar trainJProgressBar;
	private String shortTextFileName;
	private String externalCorpusFileName;
	private String labelFileName;
	
	private SemiShortTexts shortTexts;
	private int chunkSize = 500;
	private int chunkNum;
	private int classifierNum;
	private int clusterNum;
	
	private int svmCost;
	private String svmType;
	private String kernelType;
	private double threshold = 1-0.5/2;
	
	public SemiShortTextStreamClassification(JTextArea printResultJTextArea, String shortTextFileName,
			String labelFileName) {
		super();
		this.printResultJTextArea = printResultJTextArea;
		this.shortTextFileName = shortTextFileName;
		this.labelFileName = labelFileName;
		shortTexts = new SemiShortTexts();
		shortTexts.getOriginalShortTexts(FileTools.readFile(shortTextFileName));
		shortTexts.getLabelInfo(FileTools.readFile(labelFileName));
		chunkNum = shortTexts.length/chunkSize + ((shortTexts.length % chunkSize)==0 ? 0 : 1);
		if (shortTexts.isLabel.size()<chunkNum)
		{
			System.err.println("标签数据与原始短文本数据不匹配，请修改标签信息文件的内容");
			System.exit(1);
		}
		printResultJTextArea.append("加载完成，原始短文本数据流信息如下：\n");
		printResultJTextArea.append("短文本数量："+shortTexts.length+"\n");
		printResultJTextArea.append("短文本类别个数："+shortTexts.classNum+"\n");
		printResultJTextArea.append("数据块大小："+chunkSize+"\n");
		printResultJTextArea.append("数据块数量："+chunkNum+"\n");
		printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length()); 
	}
	
	public int vectorShortTexts(int currentProgress)
	{
		printResultJTextArea.append("\n文本特征表示参数信息如下：\n");
		printResultJTextArea.append("外部语料库文件位置："+externalCorpusFileName+"\n");
		Doc2Vector d2v = new Doc2Vector(externalCorpusFileName);
		printResultJTextArea.append("加载完成，外部语料库信息如下：\n");
		printResultJTextArea.append("词的总数："+d2v.word2vec.length+"\n");
		printResultJTextArea.append("词被表示的维度大小："+d2v.wordsize+"\n");
		printResultJTextArea.append("短文本的特征表示开始"+"\n");
		currentProgress = currentProgress + 20;
		trainJProgressBar.setValue(currentProgress);
		
		d2v.run(shortTexts, chunkNum, chunkSize);
		d2v = null;
		printResultJTextArea.append("特征表示完成，获得数据信息如下："+"\n");
		printResultJTextArea.append("短文本的特征维度："+ shortTexts.featureNum + "\n");
		printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length()); 
		currentProgress = currentProgress + 5;
		trainJProgressBar.setValue(currentProgress);
		return currentProgress;
	}
	
	public int classifyShortTexts(int currentProgress, int MAX_PROGRESS)
	{
		printResultJTextArea.append("\n分类参数信息如下：\n");
		printResultJTextArea.append("集成模型的基分类器个数："+String.valueOf(classifierNum));
		printResultJTextArea.append(" 聚类器类器个数："+String.valueOf(clusterNum)+"\n");
		printResultJTextArea.append("SVM的类型："+svmType+"\n");
		printResultJTextArea.append("SVM的核函数类型："+kernelType+"\n");
		printResultJTextArea.append("SVM的代价值参数："+String.valueOf(svmCost)+"\n");		
		printResultJTextArea.append("漂移检测阈值："+String.valueOf(threshold)+"\n");
		SemiEnsembleClassifier sec = new SemiEnsembleClassifier(classifierNum,clusterNum);
		sec.setChunkSize(chunkSize);
		sec.setCost(svmCost);
		sec.setKernelType(kernelType);
		sec.setThreshold(threshold);
		sec.setSvmType(svmType);
		
		int classNum = shortTexts.classNum;
		int featureNum = shortTexts.featureNum;
		System.out.println(String.valueOf(featureNum));
		sec.setClassNum(classNum);
		sec.setFeatureNum(featureNum);
		// sort
		int[] arr = new int[shortTexts.classMap.size()];
		int inx = 0;
		for (String key : shortTexts.classMap.keySet())
		{
			arr[inx++] = shortTexts.classMap.get(key);
		}
		Arrays.sort(arr);
		List<String> clas = new ArrayList<String>();
		for (int i=0; i<arr.length; i++)
		{
			clas.add(String.valueOf(arr[i]));
		}
		sec.setClas(clas);
		sec.init();
		currentProgress = currentProgress + 1;
		trainJProgressBar.setValue(currentProgress);
		
		printResultJTextArea.append("分类开始...\n");
		printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length()); 
		int s=0,e=0;
		for (int i=0; i<shortTexts.VectorShortTexts.length; i++)
		{
			System.out.println("数据块 "+String.valueOf(i));
			printResultJTextArea.append("---第 "+String.valueOf(i)+"数据块---\n");
			s = e;
			e = e + shortTexts.VectorShortTexts[i].size();
			double accuracy = sec.classify(shortTexts.VectorShortTexts[i], 
					shortTexts.classId.subList(s, e), shortTexts.isLabel.get(i));
			System.out.println(accuracy);
			printResultJTextArea.append("预测正确率："+String.valueOf(accuracy)+"\n");
			printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length());
			
			currentProgress = currentProgress + 3;
			trainJProgressBar.setValue(currentProgress);
		}
		return currentProgress;
	}

	public void setExternalCorpusFileName(String externalCorpusFileName) {
		this.externalCorpusFileName = externalCorpusFileName;
	}

	public void setClassifierNum(int classifierNum) {
		this.classifierNum = classifierNum;
	}

	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}

	public void setSvmCost(int svmCost) {
		this.svmCost = svmCost;
	}

	public void setSvmType(String svmType) {
		this.svmType = svmType;
	}

	public void setKernelType(String kernelType) {
		this.kernelType = kernelType;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public void setTrainJProgressBar(JProgressBar trainJProgressBar) {
		this.trainJProgressBar = trainJProgressBar;
	}


	
	
}
