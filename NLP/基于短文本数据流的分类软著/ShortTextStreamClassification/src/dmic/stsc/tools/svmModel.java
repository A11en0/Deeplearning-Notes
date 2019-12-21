package dmic.stsc.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import libsvm.svm_model;
import libsvm.svm_node;

public class svmModel {

	public svm_model model;
	public int timestamps;
	
	public Vector<Double> vy;
	public Vector<svm_node[]> vx;
	public Map<Double, Vector<svm_node[]>> label_vy;
	
//	public svmModel(svm_model model, int timestamps) 
//	{
//		super();
//		this.model = model;
//		this.timestamps = timestamps;
//	}

	public svmModel() 
	{
	
	}
	public svmModel(svm_model model, int timestamps, Vector<Double> vy, Vector<svm_node[]> vx) 
	{
		super();
		this.model = model;
		this.timestamps = timestamps;
		this.vy = vy;
		this.vx = vx;
		divideLabel();
	}


	public void divideLabel()
	{
		label_vy = new HashMap<Double, Vector<svm_node[]> >();
		for(int i=0; i<vy.size(); i++)
		{
			Vector<svm_node[]> nodes;
			if(!label_vy.containsKey(vy.get(i)))
				nodes = new Vector<svm_node[]>();
			else
				nodes = label_vy.get(vy.get(i));
			
			nodes.add(vx.elementAt(i));
			label_vy.put(vy.get(i), nodes);
		}
		
	}
	public Vector<Double> getVy() {
		return vy;
	}

	public void setVy(Vector<Double> vy) {
		this.vy = vy;
	}

	public Vector<svm_node[]> getVx() {
		return vx;
	}

	public void setVx(Vector<svm_node[]> vx) {
		this.vx = vx;
	}
	
	
	
}
