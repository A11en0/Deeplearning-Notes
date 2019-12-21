package dmic.stsc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.jb2011.lnf.beautyeye.ch2_tab.BETabbedPaneUI;

import dmic.sstsc.data.ShortTextExpansion;
import dmic.sstsc.data.ShortTextStreamClassification;
import dmic.stsc.tools.TypeTools;

public class SSTSCJPanel extends JPanel 
{
	private int width;
	private int height;
	private int x = 10;
	private int y = 10;
	private String tab1Title = "数据加载";
	private String tab2Title = "文本扩展";
	private String tab3Title = "特征表示";
	private String tab4Title = "分  类";	
	
	private boolean enabled = true;
	private boolean unenabled = false;

        private File externalCorpusFile;
        private File shortTextsFile;
        private File externalIntervalFile;
	
	JPanel leftJpanel;
	JPanel rightJpanel;
	JButton startTrainJButton;
	JTextArea printResultJTextArea;
	JProgressBar trainJProgressBar;
	
	JTabbedPane paramsJTabbedPane;
	JFileChooser externalCorpusJFileChooser;
	JFileChooser shortTextsJFileChooser;
	
	JComboBox etopicNumJComboBox;
	JComboBox ewordsNumJComboBox;

	
	JTextField aJTextField;
	JTextField bJTextField;
	JTextField lamadaJTextField;
	JComboBox iterJComboBox;
	JComboBox vtopicNumJComboBox;
	
	JTextField classifierNumJTextField;
//	JTextField conceptDriftJTextField;
	JComboBox svmTypeJComboBox;
	JComboBox kernelTypeJComboBox;
	JTextField costJTextField;
	
	JLabel errorMessageJLabel;
	
	private static final int MIN_PROGRESS = 0;
	private static final int MAX_PROGRESS = 100;
	private static int currentProgress = MIN_PROGRESS;
	
	
	public SSTSCJPanel(int width, int height) 
	{
		/**界面设计**/
		super();
		this.width = width;
		this.height = height;
		this.setLayout(new GridLayout(1, 2));
//		this.setBackground(Color.BLACK);
		
		leftJpanel = new JPanel();
//		leftJpanel.setBorder(BorderFactory.createTitledBorder("leftJpanel"));
		leftJpanel.setLayout(new GridLayout(1, 1));
//		leftJpanel.setBackground(Color.ORANGE);
		initLeftJpanel();
		
		rightJpanel = new JPanel();
		rightJpanel.setBorder(BorderFactory.createTitledBorder("结果输出"));
//		rightJpanel.setBackground(Color.YELLOW);
		initRightJpanel();
		
		this.add(leftJpanel, 0);  // 左边
		this.add(rightJpanel, 1);   //右边
		
		/**传入参数检测**/
		startTrainJButton.addActionListener(new startTrainActionListener());
		
	}
	
	private void initLeftJpanel()
	{
		int lWidth = width/2 - 100;
		int lHeight = height/2-50;
		System.out.println("lwidth" + String.valueOf(lWidth));
		System.out.println("lheight" + String.valueOf(lHeight));
		JPanel jp = new JPanel();
		jp.setBorder(BorderFactory.createTitledBorder("参数设置"));
		paramsJTabbedPane = new JTabbedPane();
//		paramsJTabbedPane.setBorder(BorderFactory.createTitledBorder("JTabbedPane"));
		paramsJTabbedPane.setPreferredSize(new Dimension(lWidth, lHeight));
		paramsJTabbedPane.addTab(tab1Title, loadDataJpanel());
		paramsJTabbedPane.addTab(tab2Title, extendTextJpanel());
		paramsJTabbedPane.addTab(tab3Title, representFeatureJpanel());
		paramsJTabbedPane.addTab(tab4Title, classifyJpanel());
		
		JPanel jpse = new JPanel();
//		jpse.setBorder(BorderFactory.createTitledBorder("XXX"));
		jpse.setLayout(new GridLayout(2, 1));
		
		JPanel jps = new JPanel();
		startTrainJButton = new JButton("开始训练");
		startTrainJButton.setPreferredSize(new Dimension(100, 25));
		jps.add(startTrainJButton);
		
		errorMessageJLabel = new JLabel();
		errorMessageJLabel.setPreferredSize(new Dimension(lWidth, 20));
		errorMessageJLabel.setForeground(Color.RED);
		jpse.add(jps);
		jpse.add(errorMessageJLabel);
		
		jp.add(paramsJTabbedPane);
		jp.add(jpse);
		
		
		leftJpanel.add(jp);
		
	}
	
	private Component loadDataJpanel()
	{
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(5, 1));
		jp.setBorder(BorderFactory.createTitledBorder("Data"));
//		jp.setPreferredSize(new Dimension(300, 200));
		
		JPanel jpt = new JPanel();	
		jpt.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel ecJLabel = new JLabel("加载外部语料库  ");
		JTextField ecJTextField = new JTextField(30);
//		ecJTextField.setText("D:/Data/SoftWorkData/Snippets/External Corpus");
		JButton ecJButton = new JButton("浏览");
		ecJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser1 = new JFileChooser(); //文件选择
				chooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser1.showOpenDialog(chooser1);        //打开文件选择窗
				externalCorpusFile = chooser1.getSelectedFile();  	//获取选择的文件
				ecJTextField.setText(externalCorpusFile.getPath());	
			}
		});
		jpt.add(ecJLabel);
		jpt.add(ecJTextField);
		jpt.add(ecJButton);
		
		JPanel jpb = new JPanel();
		jpb.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel ssJLabel = new JLabel("加载短文本数据  ");
		JTextField ssJTextField = new JTextField(30);
//		ssJTextField.setText("D:/Data/SoftWorkData/Snippets/snippets.txt.ReOrgConcept-1_uninder_t");
		JButton ssJButton = new JButton("浏览");
		ssJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser2 = new JFileChooser(); //文件选择
				chooser2.showOpenDialog(chooser2);        //打开文件选择窗
				shortTextsFile = chooser2.getSelectedFile();  	//获取选择的文件
				ssJTextField.setText(shortTextsFile.getPath());	
			}
		});
		jpb.add(ssJLabel);
		jpb.add(ssJTextField);
		jpb.add(ssJButton);
		
		JPanel jpi = new JPanel();
		jpi.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel eiJLabel = new JLabel("加载扩展区间信息");
		JTextField eiJTextField = new JTextField(30);
//		eiJTextField.setText("D:/Data/SoftWorkData/Snippets/interval.txt");
		JButton eiJButton = new JButton("浏览");
		eiJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser3 = new JFileChooser(); //文件选择
				chooser3.showOpenDialog(chooser3);        //打开文件选择窗
				externalIntervalFile = chooser3.getSelectedFile();  	//获取选择的文件
				eiJTextField.setText(externalIntervalFile.getPath());	
			}
		});
		jpi.add(eiJLabel);
		jpi.add(eiJTextField);
		jpi.add(eiJButton);
		
		jp.add(jpt);
		jp.add(jpb);
		jp.add(jpi);
		return jp;
	}
	
	private Component extendTextJpanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(5, 1));
		jp.setBorder(BorderFactory.createTitledBorder("Expansion"));
//		jp.setPreferredSize(new Dimension(300, 200));
		
		JPanel jpt = new JPanel();
		jpt.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel etopicNumJLabel = new JLabel("主 题 数  ");
		String[] etopicNum = {"1", "2", "3", "4", "5", "6", "7", "8" , "9", "10"};
		etopicNumJComboBox = new JComboBox<>(etopicNum);
		etopicNumJComboBox.setSelectedIndex(2);
		etopicNumJComboBox.setPreferredSize(new Dimension(70, 25));
		etopicNumJComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(etopicNumJComboBox.getSelectedItem());
				
			}
		});
		jpt.add(etopicNumJLabel);
		jpt.add(etopicNumJComboBox);
		
		JPanel jpw = new JPanel();
		jpw.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel wordsNumJLabel = new JLabel("单 词 数  ");
		String[] wordsNum = {"1", "2", "3", "4", "5", "6", "7", "8" , "9", "10"};
		ewordsNumJComboBox = new JComboBox<>(wordsNum);
		ewordsNumJComboBox.setSelectedIndex(1);
		ewordsNumJComboBox.setPreferredSize(new Dimension(70, 25));
		ewordsNumJComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(ewordsNumJComboBox.getSelectedItem());
				
			}
		});
		jpw.add(wordsNumJLabel);
		jpw.add(ewordsNumJComboBox);
		
		jp.add(jpt);
		jp.add(jpw);
		return jp;
	}

	private Component representFeatureJpanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(5, 1));
		jp.setBorder(BorderFactory.createTitledBorder("online BTM"));
//		jp.setPreferredSize(new Dimension(300, 200));
		
		JPanel jpt = new JPanel();
		jpt.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel vtopicNumJLabel = new JLabel("主 题 数  ");
		String[] vtopicNum = {"10", "20", "30", "40", "50", "60", "70", "80" , "90", "100", "150", "200"};
		vtopicNumJComboBox = new JComboBox<>(vtopicNum);
		vtopicNumJComboBox.setPreferredSize(new Dimension(70, 25));
		vtopicNumJComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(vtopicNumJComboBox.getSelectedItem());
				
			}
		});
		jpt.add(vtopicNumJLabel);
		jpt.add(vtopicNumJComboBox);
		
		JPanel jpa = new JPanel();
		jpa.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel aJLabel = new JLabel("apha 值   ");
		aJTextField = new JTextField(10);
//		aJTextField.setText("0.5");
		JLabel remarkJLabel1 = new JLabel("* 0-1之间的浮点数 ");
		remarkJLabel1.setForeground(Color.GRAY);
		jpa.add(aJLabel);
		jpa.add(aJTextField);
		jpa.add(remarkJLabel1);
		
		JPanel jpb = new JPanel();
		jpb.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel bJLabel = new JLabel("beta 值   ");
		bJTextField = new JTextField(10);
//		bJTextField.setText("0.1");
		JLabel remarkJLabel2 = new JLabel("* 0-1之间的浮点数 ");
		remarkJLabel2.setForeground(Color.GRAY);
		jpb.add(bJLabel);
		jpb.add(bJTextField);
		jpb.add(remarkJLabel2);
		
		JPanel jpi = new JPanel();
		jpi.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel iterJLabel = new JLabel("迭 代 数  ");
		String[] iterNum = { "50", "100", "200", "300", "400", "500", "600" , "700", "800", "900", "1000"};
		iterJComboBox = new JComboBox<>(iterNum);
		iterJComboBox.setPreferredSize(new Dimension(70, 25));
		iterJComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(iterJComboBox.getSelectedItem());
				
			}
		});
//		JLabel remarkJLabel3 = new JLabel("* 大于0的数 ");
//		remarkJLabel3.setForeground(Color.GRAY);
		jpi.add(iterJLabel);
		jpi.add(iterJComboBox);
//		jpi.add(remarkJLabel3);
		
		JPanel jpl = new JPanel();
		jpl.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lamadaJLabel = new JLabel("衰 退 值  ");
		lamadaJTextField = new JTextField(10);
//		lamadaJTextField.setText("0.5");
		JLabel remarkJLabel4 = new JLabel("* 0-1之间的浮点数 ");
		remarkJLabel4.setForeground(Color.GRAY);
		jpl.add(lamadaJLabel);
		jpl.add(lamadaJTextField);
		jpl.add(remarkJLabel4);
		
		jp.add(jpa);
		jp.add(jpb);
		jp.add(jpl);
		jp.add(jpi);
		jp.add(jpt);
		return jp;
	}

	private Component classifyJpanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(5, 1));
		jp.setBorder(BorderFactory.createTitledBorder("Classification"));
//		jp.setPreferredSize(new Dimension(300, 200));
		
		JPanel jpc = new JPanel();
		jpc.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel classifierNumJLabel = new JLabel("分类器个数  ");
		classifierNumJTextField = new JTextField(10);
//		classifierNumJTextField.setText("5");
		JLabel remarkJLabel1 = new JLabel("* (0, 50]的整数");
		remarkJLabel1.setForeground(Color.GRAY);
		jpc.add(classifierNumJLabel);
		jpc.add(classifierNumJTextField);
		jpc.add(remarkJLabel1);
		
		JPanel jps = new JPanel();
		jps.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel svmTypeJLabel = new JLabel("SVM 类  型  ");
		String[] svmType = {"C-SVC"};
		svmTypeJComboBox = new JComboBox<>(svmType);
		svmTypeJComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(svmTypeJComboBox.getSelectedItem());
				
			}
		});
		jps.add(svmTypeJLabel);
		jps.add(svmTypeJComboBox);
		
		JPanel jpk = new JPanel();
		jpk.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel kernelTypeJLabel = new JLabel("核函数类型  ");
		String[] kernelType = {"linear", "polynomial", "radial basis function", "sigmoid", "precomputed kernel"};
		kernelTypeJComboBox = new JComboBox<>(kernelType);
		kernelTypeJComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(kernelTypeJComboBox.getSelectedItem());
				
			}
		});
		jpk.add(kernelTypeJLabel);
		jpk.add(kernelTypeJComboBox);
		
		JPanel jpco = new JPanel();
		jpco.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel costJLabel = new JLabel("代  价  值  ");
		costJTextField = new JTextField(10);
//		costJTextField.setText("32");
		JLabel remarkJLabel2 = new JLabel("* (0, 100]的整数");
		remarkJLabel2.setForeground(Color.GRAY);
		jpco.add(costJLabel);
		jpco.add(costJTextField);
		jpco.add(remarkJLabel2);
		
//		JPanel jpcp = new JPanel();
//		jpcp.setLayout(new FlowLayout(FlowLayout.LEFT));
//		JLabel conceptDriftJLabel = new JLabel("漂移检测的阈值  ");
//		conceptDriftJTextField = new JTextField(10);
//		JLabel remarkJLabel3 = new JLabel("* 0.5-1之间的浮点数");
//		remarkJLabel3.setForeground(Color.GRAY);
//		jpcp.add(conceptDriftJLabel);
//		jpcp.add(conceptDriftJTextField);
//		jpcp.add(remarkJLabel3);
		
		jp.add(jpc);
		jp.add(jpco);
//		jp.add(jpcp);
		jp.add(jps);
		jp.add(jpk);
		return jp;
	}



	private void initRightJpanel()
	{
		int rWidth = width/2 - 100;
		int rtpHeight = height/2 + 100;
		System.out.println("rWidth" + String.valueOf(rWidth));
		System.out.println("rheight" + String.valueOf(rtpHeight));
		
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
//		jp.setBorder(BorderFactory.createTitledBorder("JPanel"));
		JPanel jpp = new JPanel();
		printResultJTextArea = new JTextArea();
//		printResultJTextArea.setPreferredSize(new Dimension(rWidth, rtpHeight));
		printResultJTextArea.setLineWrap(true);
		printResultJTextArea.setEditable(unenabled);
		JScrollPane scroll = new JScrollPane(printResultJTextArea);
		//分别设置水平和垂直滚动条自动出现 
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(rWidth, rtpHeight));
		jpp.add(scroll);
		
		JPanel jpt = new JPanel();
		trainJProgressBar = new JProgressBar();
		trainJProgressBar.setMinimum(MIN_PROGRESS);
		trainJProgressBar.setMaximum(MAX_PROGRESS);
		trainJProgressBar.setStringPainted(true);
		trainJProgressBar.setValue(currentProgress);
		trainJProgressBar.setPreferredSize(new Dimension(rWidth, 20));
		jpt.add(trainJProgressBar);
		
		jp.add(jpp, BorderLayout.CENTER);
		jp.add(jpt, BorderLayout.SOUTH);
		rightJpanel.add(jp);
		
	}

	class startTrainActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// error check
			boolean bool = checkFormat();
			if(bool)
			{
//				startTrainJButton.setEnabled(unenabled);
				SSTSCThread sthread = new SSTSCThread();
				sthread.start();
				
//				startTrainJButton.setEnabled(enabled);
			}
			
			
		}

		/**
		 * 判断参数格式是否正确
		 * @return
		 */
		private boolean checkFormat() 
		{
			boolean resl = false;
			// 数据加载参数
			errorMessageJLabel.setText("<html>");
			Boolean ecBool = isExternalCorpus(externalCorpusFile);
			System.out.println(String.valueOf(ecBool));
			if (!ecBool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "外部语料库错误！");
			}
			Boolean ssBool = (shortTextsFile==null) ? false : true;
			if (!ssBool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "短文本文件错误!");
			}
			Boolean eiBool = (externalIntervalFile==null) ? false : true;
			if (!eiBool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "区间文件错误!");
			}
			resl = ecBool && ssBool && eiBool;
			String err = errorMessageJLabel.getText();
			if (err!=null)
			{
				errorMessageJLabel.setText(err + "\n");
			}
			//文本扩展参数
			//......
			//特征表示参数
			boolean abool = TypeTools.isDouble(aJTextField.getText());
			boolean bbool = TypeTools.isDouble(bJTextField.getText());
			boolean lbool = TypeTools.isDouble(lamadaJTextField.getText());
			if (!abool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "apha输入错误！");
			}
			if (!bbool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "beta输入错误！");
			}
			if (!lbool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "衰退值输入错误！");
			}
			resl = resl && abool && bbool && lbool;
			err = errorMessageJLabel.getText();
			if (err!=null)
			{
				errorMessageJLabel.setText(err + "\n");
			}
			//分类参数
			boolean clbool = TypeTools.isInteger(classifierNumJTextField.getText(), 0, 50);
			boolean cobool = TypeTools.isInteger(costJTextField.getText(), 0, 100);
			if (!clbool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "分类器个数输入错误！");
			}
			if (!cobool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "代价值输入错误！");
			}
			errorMessageJLabel.setText(errorMessageJLabel.getText() + "</html>");
			resl = resl && clbool && cobool;
			return resl;
		}

		

		private Boolean isExternalCorpus(File file) 
		{
			if ((file==null) || (!file.isDirectory()))
			{
				return false;
			}
			Map<String, Boolean> map = new HashMap<String, Boolean>()
			{
				{
					put("model-final.theta", false);
				    put("model-final.twords", false);
				    put("model-final.others", false);
				    put("model-final.phi", false);
				    put("model-final.tassign", false);
				    put("model-final.tassign", false);
				    put("wordmap.txt", false);
				}
			};
			String[] list = file.list();
			for (int i=0; i<list.length; i++)
			{
				String key = list[i];
				if (map.containsKey(key))
				{
					map.put(key, true);
				}
			}
			
			for(boolean bool:map.values())
			{
				if (!bool)
				{
					return false;
				}
			}
			return true;
		}
		
	}
	class SSTSCThread extends Thread
	{
		public void run()
		{
			startTrainJButton.setEnabled(unenabled);
			System.out.println("主题数和单词数分别为" + etopicNumJComboBox.getSelectedItem() + " XX " + ewordsNumJComboBox.getSelectedItem());
			printResultJTextArea.setText("短文本数据流文件位置："+shortTextsFile.getPath()+"\n");
			
			ShortTextStreamClassification stsc = new ShortTextStreamClassification(shortTextsFile);
			stsc.setPrintResultJTextArea(printResultJTextArea);

			currentProgress = (int) ((MAX_PROGRESS - MIN_PROGRESS) * 0.2);
			trainJProgressBar.setValue(currentProgress);
			//LDA inference and short text expansion
			System.out.println("短文本扩展");
			stsc.setExternalCorpusFile(externalCorpusFile);
			stsc.setExternalIntervalFile(externalIntervalFile);
			stsc.setEtopicNum(Integer.parseInt((String) etopicNumJComboBox.getSelectedItem()));
			stsc.setEwordsNum(Integer.parseInt((String)ewordsNumJComboBox.getSelectedItem()));
			stsc.extendShortText();
							
			// handle data format and vectorization
			currentProgress = currentProgress + (int) ((MAX_PROGRESS - MIN_PROGRESS) * 0.3);
			trainJProgressBar.setValue(currentProgress);
			System.out.println("短文本向量化");
			stsc.setValpha(Double.parseDouble(aJTextField.getText()));
			stsc.setVbeta(Double.parseDouble(bJTextField.getText()));
			stsc.setVlamda(Double.parseDouble(lamadaJTextField.getText()));
			stsc.setViter(Integer.parseInt((String)iterJComboBox.getSelectedItem()));
			stsc.setVtopicNum(Integer.parseInt((String)vtopicNumJComboBox.getSelectedItem()));
			stsc.vectorShortText();
			
			// classification
			currentProgress = currentProgress + (int) ((MAX_PROGRESS - MIN_PROGRESS) * 0.4);
			trainJProgressBar.setValue(currentProgress);
			System.out.println("短文本分类");
			stsc.setClassifierNum(Integer.parseInt(classifierNumJTextField.getText()));
			stsc.setSvmCost(Integer.parseInt(costJTextField.getText()));
			stsc.setSvmType((String)svmTypeJComboBox.getSelectedItem());
			stsc.setKernelType((String)kernelTypeJComboBox.getSelectedItem());
//			stsc.setThreshold(Double.valueOf(conceptDriftJTextField.getText()));
			stsc.classifyShortText();
			startTrainJButton.setEnabled(enabled);

			trainJProgressBar.setValue(MAX_PROGRESS);
		}
	}
}
