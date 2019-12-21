package dmic.stsc.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeTools {

	public static boolean isInteger(String str, int s, int e) 
	{
		// TODO Auto-generated method stub
		boolean bool = false;
		if (isdigit(str))
		{
			int v = Integer.parseInt(str);
			if((v>s) && (v<=e))
			{
				bool = true;
			}
		}
		return bool;
	}

	public static  boolean isdigit(String str)
	{
		//是否是大于0的数
		boolean bool = false;
		if (str != null)
		{
			Pattern p = Pattern.compile("[1-9][0-9]*");
			Matcher m = p.matcher(str);
			bool = m.matches();
		}
		return bool;
	}

	public static  boolean isDouble(String str) 
	{
		//判断是否包含非数字
		//判断是否为0-1之间的数
		boolean bool = false;
		if (str!=null)
		{
			Pattern p1 = Pattern.compile("^1.0+");
			Pattern p2 = Pattern.compile("^0.[0-9]+");
			if (str.equals("1") || str.equals("0"))
			{
				bool = true;
			} else{
				Matcher m1 = p1.matcher(str);
				Matcher m2 = p2.matcher(str);
				bool = m1.matches() || m2.matches();				
			}	
		}
				
		return bool;
	}
}
