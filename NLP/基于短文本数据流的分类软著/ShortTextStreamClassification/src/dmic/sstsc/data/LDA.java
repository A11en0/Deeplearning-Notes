package dmic.sstsc.data;

import java.io.File;
import java.util.List;

import dmic.sstsc.jgibblda.Inferencer;
import dmic.sstsc.jgibblda.LDACmdOption;
import dmic.sstsc.jgibblda.Model;

public class LDA {
	
	private String modelName = "model-final";
	private File modelDir;
	private List<String> data;
	private double[][] infTheta;
	private int K;
	
	public LDA(File modelDir, List<String> data) 
	{
		this.modelDir = modelDir;
		this.data = data;
		init();
	}

	private void init() 
	{
		LDACmdOption ldaOption = new LDACmdOption();
		ldaOption.est = false;
		ldaOption.inf = true;
		ldaOption.dir = modelDir.getPath();
		ldaOption.modelName = modelName;
		
		Inferencer inferencer = new Inferencer();
		inferencer.init(ldaOption);			
		Model newModel = inferencer.inference(data);
		infTheta = newModel.theta;
		K = newModel.K;
	}

	public double[][] getInfTheta() {
		return infTheta;
	}

	public int getK() {
		return K;
	}
	
	

}
