package dmic.stsc.ui;

import java.awt.Color;

import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.ch2_tab.BETabbedPaneUI;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("sun.java2d.noddraw", "true");
		try
		{
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible", false);
			UIManager.put("TabbedPane.tabAreaInsets", new javax.swing.plaf.InsetsUIResource(3,10,2,10));
			
		}
		catch(Exception e)
		{
			//TODO exception
			e.printStackTrace();
		}
		STSCJFrame stsc = new STSCJFrame("短文本数据流分类");

	}
}
