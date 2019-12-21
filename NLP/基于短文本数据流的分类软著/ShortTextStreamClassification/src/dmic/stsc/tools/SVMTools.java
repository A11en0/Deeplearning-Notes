package dmic.stsc.tools;

import java.util.List;
import java.util.Vector;

import libsvm.svm_node;
import libsvm.svm_print_interface;

public class SVMTools {

	public static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};
	
	public static svm_print_interface svm_print_stdout = new svm_print_interface()
	{
		public void print(String s)
		{
			System.out.print(s);
		}
	};
	
	public static double atof(String s)
	{
		double d = Double.valueOf(s).doubleValue();
		if (Double.isNaN(d) || Double.isInfinite(d))
		{
			System.err.print("NaN or Infinity in input\n");
			System.exit(1);
		}
		return(d);
	}

	public static int atoi(String s)
	{
		return Integer.parseInt(s);
	}
	public static int getClassifyData(List<String> data, List<Integer> ids, Vector<Double> vy, Vector<svm_node[]> vx)
	{
		int max_index = 0;
		for (int i=0; i<data.size(); i++)
		{
			String line = data.get(i);
			String[] values = line.split(" ");
			svm_node[] x = new svm_node[values.length];
			for(int k=0;k<values.length;k++)
			{
				x[k] = new svm_node();
				x[k].index = k;
				x[k].value = atof(values[k]);
			}
			if(values.length>0)
			{
				max_index = Math.max(max_index, x[values.length-1].index);
			}
			vx.addElement(x);
			vy.addElement((double)ids.get(i));
		}
		return max_index;
	}
}
