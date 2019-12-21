package dmic.stsc.ui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;

/**
 * 
 */
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jb2011.lnf.beautyeye.ch2_tab.BETabbedPaneUI;

public class STSCJFrame extends JFrame {
	
	String titleName;
	String sstscTitleName = "有监督短文本数据流分类";
	String semisstscitleName = "半监督短文本数据流分类";
	
	int jframeWidth = 1000;
	int jframeHeight = 600;
	
	/*组件定义*/
	Container container;
	JTabbedPane jtabbedPane;
	
	
	public STSCJFrame(String titleName) throws HeadlessException 
	{
		super();
		this.titleName = titleName;
		this.setTitle(titleName);
		this.setSize(jframeWidth, jframeHeight);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		container=getContentPane();  //获取Container对象		
		setComponent(container);
		
		this.setVisible(true);
	}

	private void setComponent(Container container)
	{
		jtabbedPane = new JTabbedPane();
		jtabbedPane.addTab(sstscTitleName, new SSTSCJPanel(jframeWidth, jframeHeight));
		jtabbedPane.addTab(semisstscitleName, new SemiSSTSCJPanel(jframeWidth, jframeHeight));
		
		jtabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				System.out.println("当前选中的选项卡: " + jtabbedPane.getSelectedIndex());
			}
		});
		jtabbedPane.setSelectedIndex(0);
		container.add(jtabbedPane);
	}
	
}
