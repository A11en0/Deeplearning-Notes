package dmic.sstsc.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JTextArea;

import dmic.stsc.tools.FileTools;

public class ShortTextExpansion {
	
	private File externalCorpusFile;
    private File externalIntervalFile;
    private String twordsName = "model-final.twords";
    
    private ShortTexts shortTexts;
    
    private LDA lda;    
	private int K;
	private double[][] infTheta;
	
	private int wordsNum = 5;
	private int topicNum = 3;
	private Vector<Float[]> intervals;
	private Vector<String>[] topWords;
	private int[][] topic_prob_bydecs;
	/**topic ids that are extended into short text, size: M × 
	 * number of topics extended into short text*/
	public Vector<Integer>[] extendTopicIds;
	/**extended texts are top N words according to topic ids, size: M*/
	public String[] extendedTexts;
	/**doc.size*/
	private int M;
	
	JTextArea printResultJTextArea;
	public ShortTextExpansion(File externalCorpusFile, File externalIntervalFile) {
		super();
		this.externalCorpusFile = externalCorpusFile;
		this.externalIntervalFile = externalIntervalFile;
		
//		init();
	}

//	private void init()
//	{
//		// 获取原始短文本相关信息
////		shortTexts = new ShortTexts();
////		shortTexts.getOriginalShortTexts(FileTools.readFile(shortTextsFile));
//		M = shortTexts.length;
//		
//		// LDA模型推断原始短文本主题信息
//		lda = new LDA(externalCorpusFile, shortTexts.originalShortTexts);
//		K = lda.getK();
//		infTheta = lda.getInfTheta();	
//		
//	}	
	
	public void expansion()
	{
		// LDA模型推断原始短文本主题信息
		printResultJTextArea.append("由外部语料库加载的模型推断短文本数据流的主题信息...\n");
		LDAinfer();
		printResultJTextArea.append("准备短文本数据流扩展所需要的参数信息...\n");
		initExpansion();
		//expend short text
		System.out.println("start to extend short text");
		printResultJTextArea.append("开始扩展..\n");
		int times;
		for(int m=0; m<M; m++){
			//by decs
			topic_prob_bydecs[m] = binary_InsertSort(topic_prob_bydecs[m], infTheta[m]);
			for(int i=0; i<topicNum; i++){
//				System.out.println(pz_d[m][topic_prob_bydecs[m][i]]);
				times = extendableTimes(m, topic_prob_bydecs[m][i]);
				addTopicIds(m, topic_prob_bydecs[m][i], times);
				addTexts(m, topic_prob_bydecs[m][i], times);
			}
			
		}
		shortTexts.getExtendedShortTexts(extendedTexts);
		printResultJTextArea.append("短文本数据流扩展完成！\n");
	}
	
	private void LDAinfer()
	{
		M = shortTexts.length;		
		lda = new LDA(externalCorpusFile, shortTexts.originalShortTexts);
		K = lda.getK();
		infTheta = lda.getInfTheta();	
	}

	private void initExpansion() 
	{
		// 读Top单词 和 扩展区间
		topWords = new Vector[K];
		intervals = new Vector<>();
		readInterval();
		readTwords();
		extendTopicIds = new Vector[M];
		extendedTexts = new String[M];
		topic_prob_bydecs = new int[M][K];
		for (int m=0; m<M; m++)
		{
			for(int k=0; k<K; k++){
				topic_prob_bydecs[m][k] = k;
			}
		}
	}

	public int[] binary_InsertSort(int[] key, double[] value){
		int high, low, tmp;
		for(int i=1; i<key.length; i++){
			high = i-1;
			low = 0;
			tmp = key[i];
			while(low<=high){
				int mid = (high+low)/2;
				if(value[key[i]]<value[key[mid]]){
					low = mid+1;
				}else{
					high = mid-1;
				}
			}
			int j;
			for(j=i; j>low; j--){
				key[j] = key[j-1];
			}
			key[j] = tmp;
		}
		return key;
	}

	/**
	 * a topic appears once or several times on extended texts 
	 * depending on the probability of that topic which interval
	 * @param the mth document 
	 * @param the kth topic
	 * @return times the kth topic appears
	 */
	private int extendableTimes(int m, int k) {
		float upperBound, lowerBound;
		int times;
		for(int i=0; i<intervals.size(); i++){
			lowerBound = intervals.get(i)[0];
			upperBound = intervals.get(i)[1];
			times = intervals.get(i)[2].intValue();
			if((infTheta[m][k]<upperBound)&&(infTheta[m][k]>=lowerBound))
				return times;
		}
		return 0;
	}
	
	/**
	 * the kth topic add to the mth document vector with n times
	 * @param the mth document 
	 * @param the kth topic
	 * @param n times the kth topic appears
	 */
	private void addTopicIds(int m, int k, int n) {
		if(extendTopicIds[m] == null)
			extendTopicIds[m] = new Vector<Integer>();
		for(int i=0; i<n; i++){
			extendTopicIds[m].add(k);
		}
	}
	/**
	 * the kth topic content add to the mth document vector with n times
	 * @param the mth document 
	 * @param the kth topic
	 * @param n times the kth topic appears
	 */
	private void addTexts(int m, int k, int n) {
		
		if(extendedTexts[m]==null)
		{
			extendedTexts[m] = "";
		}
		else{
			extendedTexts[m] += " ";
		}
		
		int num = 0;
		if(wordsNum == -1)
			num = topWords[k].size();
		else
			num = wordsNum;
		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<n; i++){
			for(int j=0; j<num; j++){
				sb.append(topWords[k].get(j));
				sb.append(" ");
//				extendedTexts[m] += topWords[k].get(j);
//				extendedTexts[m] += " ";				
			}
		}
		String value = sb.toString();
		if (sb.length() != 0)
		{
			extendedTexts[m] += value.substring(0,sb.length()-1);			
		}
	}
	private boolean readTwords()
	{
		String filename = externalCorpusFile + File.separator + twordsName;
		String line;
		int k=0;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(filename)));
			while((line=br.readLine())!=null){
				if(k>K){
					System.err.println("Error:\tinput K doesn't match with model");
					br.close();
					return false;
				}
				if(line.startsWith("Topic")&&line.endsWith("th:")){
					topWords[k++] = new Vector<String>();
					continue;
				}
				line = line.replaceAll("\t", "");
				String word = line.split(" ")[0];
				topWords[k-1].add(word);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	
		
	}

	private boolean readInterval()
	{

		String line;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(externalIntervalFile));
			while((line=br.readLine())!=null){
				String[] values = line.split(" ");
				if(values.length!=3){
					System.err.println("Error:\tInterval type is '[float float) int'");
					br.close();
					return false;
				}
				Float[] valuef = new Float[3];
				valuef[0] = Float.valueOf(values[0].substring(1));
				valuef[1] = Float.valueOf(values[1].substring(0,values[1].length()-1));
				valuef[2] = Float.valueOf(values[2]);
				intervals.add(valuef);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	
		
	}

	public void setWordsNum(int wordsNum) {
		this.wordsNum = wordsNum;
	}

	public void setTopicNum(int topicNum) {
		this.topicNum = topicNum;
	}

	public ShortTexts getShortTexts() {
		return shortTexts;
	}

	public void setShortTexts(ShortTexts shortTexts) {
		this.shortTexts = shortTexts;
	}

	public JTextArea getPrintResultJTextArea() {
		return printResultJTextArea;
	}

	public void setPrintResultJTextArea(JTextArea printResultJTextArea) {
		this.printResultJTextArea = printResultJTextArea;
	}
    
    

}
