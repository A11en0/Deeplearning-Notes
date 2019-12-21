package dmic.stsc.tools;
import libsvm.*;
import java.util.*;

public class svm_predict {
	
	private svm_model model;
	private int classNum;
	private Vector<Double> vy;
	private Vector<svm_node[]> vx;
		
	private double[][] predict_result;
	
	public svm_predict(svm_model model, int classNum) {
		this.model = model;
		this.classNum = classNum;
		svm_print_string = SVMTools.svm_print_stdout;
       
        if (model == null)
		{
			System.err.print("model is null\n");
			System.exit(1);
		}
        if(model.param.probability == 1)
		{
			if(svm.svm_check_probability_model(model)==0)
			{
				System.err.print("Model does not support probabiliy estimates\n");
				System.exit(1);
			}
		}
		else
		{
			if(svm.svm_check_probability_model(model)!=0)
			{
				svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
			}
		}
	}

	public void predict(Vector<Double> tvy, Vector<svm_node[]> tvx)
	{
		this.vy = tvy;
		this.vx = tvx;

		predict_result = new double[vy.size()][classNum];
		
		for(int i=0; i<vy.size(); i++)
		{
			double v = svm.svm_predict(model,vx.elementAt(i));
			double[] probs = new double[classNum];
			svm.svm_predict_probability(model, vx.elementAt(i), probs);
			int[] labels = new int[classNum];
			for(int j=0; j <classNum; j++) labels[j]=-1;
			svm.svm_get_labels(model, labels);
			for (int j=0; j<classNum; j++)
			{
				if (labels[j]==-1)
				{
					break;
				}
				predict_result[i][labels[j]] = probs[j];
			}
		}
	}

	
	public double[][] get_predict_result() {
		return predict_result;
	}

	public void set_model(svm_model model) {
		this.model = model;
	}


	private static svm_print_interface svm_print_string = SVMTools.svm_print_stdout;

	static void info(String s) 
	{
		svm_print_string.print(s);
	}

	protected static svm_print_interface svm_print_stdout = new svm_print_interface()
	{
		public void print(String s)
		{
			System.out.print(s);
		}
	};
}
