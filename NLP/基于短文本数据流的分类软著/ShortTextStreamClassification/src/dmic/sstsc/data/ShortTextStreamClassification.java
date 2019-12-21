package dmic.sstsc.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import dmic.stsc.tools.FileTools;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;

public class ShortTextStreamClassification {

    private File shortTextsFile;
	// 短文本扩展
	private File externalCorpusFile;
    private File externalIntervalFile;
    private int ewordsNum;
	private int etopicNum;
	
	//短文本向量化
	private double valpha;
	private double vbeta;
	private double vlamda;
	private int viter;
	private int vtopicNum;
	private int vWordsNum;
	private int chunkNum = 50;
	private int daysNum;
	private String dir;
	private String docWordIndexDir;
	private String oBTMDir;
	
	//短文本分类
	private int classifierNum;
	private int svmCost;
	private String svmType;
	private String kernelType;
	private double threshold;
	
	//存储短文本信息
	private ShortTexts shortTexts;	
	
	JTextArea printResultJTextArea;
//	JTextField conceptDriftJTextField;
	
	static{
		System.loadLibrary("demo2");
	}

	public native void oBTM(int K, int W, double alpha, double beta, 
			String input_dir, int n_day, String res_dir, int n_iter, double lam);
	
	public ShortTextStreamClassification(File shortTextsFile)
	{
		super();
		this.shortTextsFile = shortTextsFile;
		shortTexts = new ShortTexts();
		shortTexts.getOriginalShortTexts(FileTools.readFile(shortTextsFile));	
		threshold = 1-0.5/shortTexts.classNum;
//		conceptDriftJTextField.setText(String.valueOf(threshold));
	}

	/**
	 * 短文本扩展
	 * @return
	 */
	public boolean extendShortText()
	{
		printResultJTextArea.append("\n文本扩展参数信息如下：\n");
		printResultJTextArea.append("外部语料库文件位置："+externalCorpusFile.getPath()+"\n");
		printResultJTextArea.append("扩展区间信息文件位置："+externalIntervalFile.getPath()+"\n");
		printResultJTextArea.append("每条短文本最大可扩展的主题数："+String.valueOf(etopicNum)+"\n");
		printResultJTextArea.append("每个主题下最大可扩展的单词数："+String.valueOf(ewordsNum)+"\n");
		ShortTextExpansion ste = new ShortTextExpansion(externalCorpusFile, externalIntervalFile);
		ste.setShortTexts(shortTexts);
		ste.setTopicNum(etopicNum);
		ste.setWordsNum(ewordsNum);
		ste.setPrintResultJTextArea(printResultJTextArea);
		ste.expansion();
		return true;
	}
	/**
	 * 短文本向量化
	 * @return
    	 */
	public boolean vectorShortText()
	{
		printResultJTextArea.append("\n特征表示参数信息如下：\n");
		printResultJTextArea.append("alpha值："+String.valueOf(valpha)+"\n");
		printResultJTextArea.append("beta值："+String.valueOf(vbeta)+"\n");
		printResultJTextArea.append("衰退值："+String.valueOf(vlamda)+"\n");
		printResultJTextArea.append("迭代数："+String.valueOf(viter)+"\n");
		printResultJTextArea.append("主题数："+String.valueOf(vtopicNum)+"\n");
		printResultJTextArea.append("特征表示数据准备...\n");
		dir = externalCorpusFile + File.separator + "Vectorization" + File.separator;
		docWordIndexDir = dir + "indexChunks" + File.separator;
		adjustVectorizationFormat();
		// online BTM
		printResultJTextArea.append("特征表示开始...\n");
		printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length()); 
		oBTMDir = dir + "oBTM" + File.separator;
		FileTools.isChartPathExist(oBTMDir);
		oBTM(vtopicNum, vWordsNum, valpha, vbeta, docWordIndexDir, daysNum, oBTMDir, viter, vlamda);
		printResultJTextArea.append("特征表示完成，中间数据保存在："+dir+"\n");
		printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length()); 
		return true;
	}
	
	/**
	 * 短文本分类
	 * @return
	 */
	public boolean classifyShortText()
	{
		printResultJTextArea.append("\n分类参数信息如下：\n");
		printResultJTextArea.append("集成模型的基分类器个数："+String.valueOf(classifierNum)+"\n");
		printResultJTextArea.append("SVM的类型："+svmType+"\n");
		printResultJTextArea.append("SVM的核函数类型："+kernelType+"\n");
		printResultJTextArea.append("SVM的代价值参数："+String.valueOf(svmCost)+"\n");

		printResultJTextArea.append("分类参数准备...\n");
		printResultJTextArea.append("数据块个数："+String.valueOf(daysNum)+"\n");
		printResultJTextArea.append("数据块大小："+String.valueOf(chunkNum)+"\n");
		printResultJTextArea.append("类别个数："+String.valueOf(shortTexts.classNum)+"\n");
		printResultJTextArea.append("特征个数："+String.valueOf(vtopicNum)+"\n");
		svm_parameter param = initClassifyParam();
		
		printResultJTextArea.append("漂移检测阈值："+String.valueOf(threshold)+"\n");
		
		printResultJTextArea.append("分类开始...\n");
		printResultJTextArea.setCaretPosition(printResultJTextArea.getText().length()); 
		EnsembleClassification ec = new EnsembleClassification(oBTMDir, shortTexts.classId, param);
		ec.setClassifierNum(classifierNum);
		ec.setChunkNum(chunkNum);
		ec.setClassNum(shortTexts.classNum);
		ec.setDaysNum(daysNum);
		ec.setFeatureNum(vtopicNum);
		ec.setPrintResultJTextArea(printResultJTextArea);
		ec.setThreshold(threshold);
		ec.classify();
		return true;
	}
	
	private svm_parameter initClassifyParam()
	{
		svm_parameter param = new svm_parameter();
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
		param.C = svmCost;
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
		return param;
	}

	private void adjustVectorizationFormat() 
	{
		List<String> extendedShortTexts = shortTexts.extendedShortTexts;
		Map<String, Integer> classMap = shortTexts.classMap;		
		String[][] chunkIndexs;
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		int docsNum = shortTexts.length/chunkNum + ((shortTexts.length%chunkNum)==0 ? 0 : 1);
		chunkIndexs = new String[docsNum][];
		
		int count = 0;
		for (int i=0; i<docsNum; i++)
		{
			if (count >= shortTexts.length)
			{
				break;
			}
			int j=count;
			int num = chunkNum;
			if (i==(docsNum-1))
			{
				num = shortTexts.length - count;
			}
			String[] docIndexs = new String[num];
			int inx = 0;
			for (; j<(count+num); j++)
			{
				String doc = getWordIndex(extendedShortTexts.get(j), wordMap);
				docIndexs[inx++] = doc;
			}
			chunkIndexs[i] = docIndexs;
			count = j;
		}
		vWordsNum = wordMap.size();
		daysNum = docsNum;
		FileTools.isChartPathExist(dir);
		String classMapFileName = dir + "classmap.txt";
		FileTools.writeFile(classMapFileName, classMapToString(classMap));
		
		String wordMapFileName = dir + "wordmap.txt";
		FileTools.writeFile(wordMapFileName, wordMapToString(wordMap));
		String classIdFileName = dir + "classId.txt";
		FileTools.writeFile(classIdFileName, classIdToString(shortTexts.classId));
		FileTools.writeFiles(docWordIndexDir, chunkIndexs);
		
	}

	private String[] classIdToString(List<Integer> classId) 
	{
		String[] data = new String[classId.size()];
		for (int i=0; i<classId.size(); i++)
		{
			data[i] = String.valueOf(classId.get(i));
		}
		return data;
	}

	private String[] wordMapToString(Map<String, Integer> wordMap) 
	{
		List<Map.Entry<String, Integer>> infoIds =
			    new ArrayList<Map.Entry<String, Integer>>(wordMap.entrySet());
		Collections.sort(infoIds, new wordMapComparator());
		
		String[] data = classMapToString(wordMap);
		return data;
	}

	private String[] classMapToString(Map<String, Integer> classMap)
	{
		String[] data = new String[classMap.size()];
		int inx = 0;
		for(String key : classMap.keySet())
		{
			data[inx] = key + "\t" + String.valueOf(classMap.get(key));
			inx ++;
		}
		return data;
	}

	private String getWordIndex(String doc, Map<String, Integer> wordMap)
	{
		StringBuffer sb = new StringBuffer();
		if (doc!=null)
		{
			String[] words = doc.split(" ");
			for (int i=0; i<words.length; i++)
			{
				if (wordMap.containsKey(words[i]))
				{
					sb.append(String.valueOf(wordMap.get(words[i])));
					sb.append(" ");
//					wordIndexs[i] = wordMap.get(words[i]);
				} else {
					int id = wordMap.size();
					wordMap.put(words[i], id);
					sb.append(String.valueOf(id));
					sb.append(" ");
//					wordIndexs[i] = id;
				}
			}
			sb.deleteCharAt(sb.length()-1);
			
		} else {
			System.err.println("调整向量化格式时出错");
			System.exit(1);
		}
		return sb.toString();
	}

	public File getExternalCorpusFile() {
		return externalCorpusFile;
	}

	public void setExternalCorpusFile(File externalCorpusFile) {
		this.externalCorpusFile = externalCorpusFile;
	}

	public File getShortTextsFile() {
		return shortTextsFile;
	}

	public void setShortTextsFile(File shortTextsFile) {
		this.shortTextsFile = shortTextsFile;
	}

	public File getExternalIntervalFile() {
		return externalIntervalFile;
	}

	public void setExternalIntervalFile(File externalIntervalFile) {
		this.externalIntervalFile = externalIntervalFile;
	}

	public int getEwordsNum() {
		return ewordsNum;
	}

	public void setEwordsNum(int ewordsNum) {
		this.ewordsNum = ewordsNum;
	}

	public int getEtopicNum() {
		return etopicNum;
	}

	public void setEtopicNum(int etopicNum) {
		this.etopicNum = etopicNum;
	}

	public double getValpha() {
		return valpha;
	}

	public void setValpha(double valpha) {
		this.valpha = valpha;
	}

	public double getVbeta() {
		return vbeta;
	}

	public void setVbeta(double vbeta) {
		this.vbeta = vbeta;
	}

	public double getVlamda() {
		return vlamda;
	}

	public void setVlamda(double vlamda) {
		this.vlamda = vlamda;
	}

	public int getViter() {
		return viter;
	}

	public void setViter(int viter) {
		this.viter = viter;
	}

	public int getVtopicNum() {
		return vtopicNum;
	}

	public void setVtopicNum(int vtopicNum) {
		this.vtopicNum = vtopicNum;
	}
    
	public void setClassifierNum(int classifierNum) {
		this.classifierNum = classifierNum;
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


	public class wordMapComparator implements Comparator<Entry<String, Integer> >{

		@Override
		public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
			
			return o1.getValue()-o2.getValue();
		}
		
	}
    
}
