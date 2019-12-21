package dmic.stsc.tools;

import libsvm.*;
import java.io.*;
import java.util.*;

public class svm_train {
	/**/	
	public svm_model model;
	
	private svm_parameter param;	
	private svm_problem prob;	
	private String error_msg;
	private String model_file_name;

	
	public svm_train(svm_parameter param) {
		this.param = param;
	}
	
	public void run(Vector<svm_node[]> vx, Vector<Double> vy, int max_index) throws IOException
	{
		//set prob...
		prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for(int i=0;i<prob.l;i++)
			prob.x[i] = vx.elementAt(i);
		prob.y = new double[prob.l];
		for(int i=0;i<prob.l;i++)
			prob.y[i] = vy.elementAt(i);

		if(param.gamma == 0 && max_index > 0)
			param.gamma = 1.0/max_index;
		
		//check parameter
		error_msg = svm.svm_check_parameter(prob,param);

		if(error_msg != null)
		{
			System.err.print("ERROR: "+error_msg+"\n");
			System.exit(1);
		}
		//svm training...
		model = svm.svm_train(prob,param);
		if(model_file_name != null)
			svm.svm_save_model(model_file_name,model);
	}


	public svm_model get_model() {
		return model;
	}


	public svm_problem get_Prob() {
		return prob;
	}


	public void set_Prob(svm_problem prob) {
		this.prob = prob;
	}

	public String getModel_file_name() {
		return model_file_name;
	}

	public void setModel_file_name(String model_file_name) {
		this.model_file_name = model_file_name;
	}


}
