package dmic.sstsc.data;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ShortTexts {
	public Map<String, Integer> classMap = new HashMap<String, Integer>();
	public List<Integer> classId = new ArrayList<Integer>();
	public List<String> originalShortTexts = new ArrayList<String>();
	public List<String> extendedShortTexts = new ArrayList<String>();
	public List<String>[] VectorShortTexts;
	public int length = -1;
	public int classNum = -1;
	
	/**
	 * 
	 * @param data 输入数据以'\t'分割短文本与标签
	 */
	public void getOriginalShortTexts(List<String> data)
	{
		if ((data==null) || (data.size()==0))
		{
			System.err.println("原始短文本为空");
			System.exit(1);
		}
		for(int i=0; i<data.size(); i++)
		{
			String[] v = data.get(i).split("\t");
			if (v.length == 2)
			{
				int inx = classMap.size();
				if (classMap.containsKey(v[1]))
				{
					inx = classMap.get(v[1]);
				} else {
					classMap.put(v[1], inx);
				}
				originalShortTexts.add(v[0]);
				classId.add(inx);				
			}
		}
		length = originalShortTexts.size();
		classNum = classMap.size();
	}
	
	/**
	 * 
	 * @param extendedTexts 用以扩展原始短文本的文本
	 */
	public void getExtendedShortTexts(String[] extendedTexts)
	{
		if (extendedTexts.length != length)
		{
			System.err.println("扩展后的项数与原短文本数不一致");
			System.exit(1);
		}
		for (int i=0; i<length; i++)
		{
			if (extendedTexts[i].equals(""))
			{
				extendedShortTexts.add(originalShortTexts.get(i));
			} else {
				extendedShortTexts.add(originalShortTexts.get(i) + " " + extendedTexts[i]);
			}
		}
	}

}
