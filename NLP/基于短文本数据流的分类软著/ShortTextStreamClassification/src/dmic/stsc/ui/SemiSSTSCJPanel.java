package dmic.stsc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

import dmic.semi.sstsc.data.SemiShortTextStreamClassification;
import dmic.stsc.tools.TypeTools;

public class SemiSSTSCJPanel extends JComponent {

	private int width;
	private int height;	
	private String tab1Title = "数据加载  ";
	private String tab2Title = "半监督分类";	
	private boolean enabled = true;
	private boolean unenabled = false;
	
	private JPanel leftJpanel;
	private JPanel rightJpanel;
	
	//left
	private JTextArea printResultJTextArea;
	public JProgressBar trainJProgressBar;
	
	//right
	private JButton startTrainJButton;
	private JTabbedPane paramsJTabbedPane;
	private JLabel errorMessageJLabel;
	//load file
	private JTextField externalCorpusFileNameJTextField;
	private JTextField shortTextFileNameJTextField;
	private JTextField labelFileNameJTextField;
	// classify
	private JTextField classifierNumJTextField;
	private JTextField clusterNumJTextField;
	private JComboBox svmTypeJComboBox;
	private JComboBox kernelTypeJComboBox;
	private JTextField costJTextField;
	
	private static final int MIN_PROGRESS = 0;
	private static final int MAX_PROGRESS = 100;
	private static int currentProgress = MIN_PROGRESS;
	public SemiSSTSCJPanel(int jframeWidth, int jframeHeight)
	{
		this.width = jframeWidth;
		this.height = jframeHeight;
		this.setLayout(new GridLayout(1, 2));
		
		leftJpanel = new JPanel();
		leftJpanel.setBorder(BorderFactory.createTitledBorder("结果输出"));
		initLeftJpanel();
		
		rightJpanel = new JPanel();
		rightJpanel.setLayout(new GridLayout(1, 1));
		rightJpanel.setBorder(BorderFactory.createTitledBorder("参数设置"));
		initRightJpanel();
		
		this.add(leftJpanel, 0);  // 左边
		this.add(rightJpanel, 1);   //右边
		
		/**传入参数检测**/
		startTrainJButton.addActionListener(new startListener());
	}
	private void initLeftJpanel() 
	{

		int rWidth = width/2 - 100;
		int rtpHeight = height/2 + 100;
		System.out.println("semi-rWidth" + String.valueOf(rWidth));
		System.out.println("semi-rheight" + String.valueOf(rtpHeight));
		
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		JPanel jpp = new JPanel();
		printResultJTextArea = new JTextArea();
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
		leftJpanel.add(jp);					
	}
	private void initRightJpanel()
	{

		int lWidth = width/2 - 100;
		int lHeight = height/2-50;
		System.out.println("semi-lwidth" + String.valueOf(lWidth));
		System.out.println("semi-lheight" + String.valueOf(lHeight));
		JPanel jp = new JPanel();
		paramsJTabbedPane = new JTabbedPane();
		paramsJTabbedPane.setPreferredSize(new Dimension(lWidth, lHeight));
		paramsJTabbedPane.addTab(tab1Title, loadDataJpanel());
		paramsJTabbedPane.addTab(tab2Title, classifyJpanel());
		
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
		
		
		rightJpanel.add(jp);
		
	
		
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
		externalCorpusFileNameJTextField = new JTextField(30);
//		externalCorpusFileNameJTextField.setText("D:/Data/Semi-SupervisedLearning2017/enwiki-20180101-pages-articles.vectors");
		JButton ecJButton = new JButton("浏览");
		ecJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser1 = new JFileChooser(); //文件选择
				chooser1.showOpenDialog(chooser1);        //打开文件选择窗
				File externalCorpusFile = chooser1.getSelectedFile();  	//获取选择的文件
				externalCorpusFileNameJTextField.setText(externalCorpusFile.getPath());	
			}
		});
		jpt.add(ecJLabel);
		jpt.add(externalCorpusFileNameJTextField);
		jpt.add(ecJButton);
		
		JPanel jpb = new JPanel();
		jpb.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel ssJLabel = new JLabel("加载短文本数据  ");
		shortTextFileNameJTextField = new JTextField(30);
//		shortTextFileNameJTextField.setText("D:/Data/Semi-SupervisedLearning2017/snippet-un");
		JButton ssJButton = new JButton("浏览");
		ssJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser2 = new JFileChooser(); //文件选择
				chooser2.showOpenDialog(chooser2);        //打开文件选择窗
				File shortTextsFile = chooser2.getSelectedFile();  	//获取选择的文件
				shortTextFileNameJTextField.setText(shortTextsFile.getPath());	
			}
		});
		jpb.add(ssJLabel);
		jpb.add(shortTextFileNameJTextField);
		jpb.add(ssJButton);
		
		JPanel jpl = new JPanel();
		jpl.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lJLabel = new JLabel("加载标签信息    ");
		labelFileNameJTextField = new JTextField(30);
//		labelFileNameJTextField.setText("D:/Data/Semi-SupervisedLearning2017/islabel");
		JButton lJButton = new JButton("浏览");
		lJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser3 = new JFileChooser(); //文件选择
				chooser3.showOpenDialog(chooser3);        //打开文件选择窗
				File labelFile = chooser3.getSelectedFile();  	//获取选择的文件
				labelFileNameJTextField.setText(labelFile.getPath());	
			}
		});
		jpl.add(lJLabel);
		jpl.add(labelFileNameJTextField);
		jpl.add(lJButton);
		
		jp.add(jpt);
		jp.add(jpb);
		jp.add(jpl);
		return jp;
	}
	private Component classifyJpanel() 
	{
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(5, 1));
		jp.setBorder(BorderFactory.createTitledBorder("Classification"));
		
		JPanel jpc = new JPanel();
		jpc.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel classifierNumJLabel = new JLabel("分类器个数  ");
		classifierNumJTextField = new JTextField(10);
//		classifierNumJTextField.setText("2");
		JLabel remarkJLabel1 = new JLabel("* (0, 50]的整数");
		remarkJLabel1.setForeground(Color.GRAY);
		jpc.add(classifierNumJLabel);
		jpc.add(classifierNumJTextField);
		jpc.add(remarkJLabel1);
		
		JPanel jpcl = new JPanel();
		jpcl.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel clusterNumJLabel = new JLabel("聚类器个数  ");
		clusterNumJTextField = new JTextField(10);
//		clusterNumJTextField.setText("8");
		JLabel remarkJLabel2 = new JLabel("* (0, 50]的整数");
		remarkJLabel1.setForeground(Color.GRAY);
		jpcl.add(clusterNumJLabel);
		jpcl.add(clusterNumJTextField);
		jpcl.add(remarkJLabel2);
		
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
		JLabel remarkJLabel3 = new JLabel("* (0, 100]的整数");
		remarkJLabel3.setForeground(Color.GRAY);
		jpco.add(costJLabel);
		jpco.add(costJTextField);
		jpco.add(remarkJLabel3);
		
		jp.add(jpc);
		jp.add(jpcl);
		jp.add(jpco);
		jp.add(jps);
		jp.add(jpk);
		return jp;
	}

	class startListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {		
			boolean bool = checkFormat();
			currentProgress++;
			trainJProgressBar.setValue(currentProgress);
			if(bool)
			{
				SemiSSTSCThread semiThread = new SemiSSTSCThread();
				semiThread.start();
			}
			
			
		}

		private boolean checkFormat()
		{
			boolean resl = false;
			// 数据加载参数
			errorMessageJLabel.setText("<html>");
			String externalCorpusFileName = externalCorpusFileNameJTextField.getText();
			Boolean ecBool = (externalCorpusFileName==null || 
					externalCorpusFileName.equals("")) ? false : true;
			System.out.println(String.valueOf(ecBool));
			if (!ecBool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "外部语料库错误！");
			}
			String shortTextFileName = shortTextFileNameJTextField.getText();
			Boolean ssBool = (shortTextFileName==null || 
					shortTextFileName.equals("")) ? false : true;
			if (!ssBool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "短文本文件错误!");
			}
			String labelFileName = labelFileNameJTextField.getText();
			Boolean eiBool = (labelFileName==null || 
					labelFileName.equals("")) ? false : true;
			if (!eiBool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "标签文件错误!");
			}
			resl = ecBool && ssBool && eiBool;
			String err = errorMessageJLabel.getText();
			if (err!=null)
			{
				errorMessageJLabel.setText(err + "\n");
			}
			//分类
			boolean clbool = TypeTools.isInteger(classifierNumJTextField.getText(), 0, 50);
			boolean clubool = TypeTools.isInteger(clusterNumJTextField.getText(), 0, 50);
			boolean cobool = TypeTools.isInteger(costJTextField.getText(), 0, 100);
			if (!clbool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "分类器个数输入错误！");
			}
			if (!clubool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "聚类器个数输入错误！");
			}
			if (!cobool)
			{
				errorMessageJLabel.setText(errorMessageJLabel.getText() + "代价值输入错误！");
			}
			errorMessageJLabel.setText(errorMessageJLabel.getText() + "</html>");
			resl = resl && clbool && cobool;
			return resl;
		}
		
	}
	
	class SemiSSTSCThread extends Thread{
		
		public void run()
		{
			startTrainJButton.setEnabled(unenabled);
			String shortTextFileName = shortTextFileNameJTextField.getText();
			String labelFileName = labelFileNameJTextField.getText();
			String externalCorpusFileName = externalCorpusFileNameJTextField.getText();

			printResultJTextArea.setText("短文本数据流和标签信息文件加载..." + "\n");
			printResultJTextArea.append("短文本数据流文件位置："+ shortTextFileName+"\n");
			printResultJTextArea.append("标签信息文件位置："+ labelFileName+"\n");
			SemiShortTextStreamClassification semistsc = new SemiShortTextStreamClassification(printResultJTextArea, 
					shortTextFileName, labelFileName);
			semistsc.setTrainJProgressBar(trainJProgressBar);
			currentProgress = currentProgress + 5;
			trainJProgressBar.setValue(currentProgress);
			
			semistsc.setExternalCorpusFileName(externalCorpusFileName);
			currentProgress = semistsc.vectorShortTexts(currentProgress);
//			currentProgress = currentProgress + 50;
			
			semistsc.setClassifierNum(Integer.parseInt(classifierNumJTextField.getText()));
			semistsc.setClusterNum(Integer.parseInt(clusterNumJTextField.getText()));
			semistsc.setSvmCost(Integer.parseInt(costJTextField.getText()));
			semistsc.setKernelType((String)kernelTypeJComboBox.getSelectedItem());
			semistsc.setSvmType((String)svmTypeJComboBox.getSelectedItem());
			currentProgress = semistsc.classifyShortTexts(currentProgress, MAX_PROGRESS);
			currentProgress = MAX_PROGRESS;
			trainJProgressBar.setValue(currentProgress);
			startTrainJButton.setEnabled(enabled);
		}
	}
	

}
