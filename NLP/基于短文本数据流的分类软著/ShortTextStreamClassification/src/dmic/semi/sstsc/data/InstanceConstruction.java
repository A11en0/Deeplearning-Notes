package dmic.semi.sstsc.data;


import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceConstruction {

	private int featureNum;
	private List<String> classLabel;
	private Instances baseInsts;
	public InstanceConstruction(int featureNum, List<String> classLabel) 
	{
		super();
		this.featureNum = featureNum;
		this.classLabel = classLabel;
		init();
	}
	private void init() 
	{
		ArrayList<Attribute> attrs= new ArrayList<Attribute>();
		for (int i=0; i<featureNum; i++)
		{
			attrs.add(new Attribute("attribute"+String.valueOf(i)));			
		}
		attrs.add(new Attribute("@@class@@", classLabel));
		baseInsts = new Instances("data", attrs, 0);
	}
	public Instances getInstances(List<String> data, List<Integer> classId)
	{
		Instances insts = baseInsts.stringFreeStructure();
//		insts.setClassIndex(insts.numAttributes()-1);
		for (int i=0; i<data.size(); i++)
		{
			Instance inst = new DenseInstance(insts.numAttributes());
			String[] values = data.get(i).split(" ");
			for (int j=0; j<featureNum; j++)
			{
				inst.setDataset(insts);
	            inst.setValue(j,Double.parseDouble(values[j]));
			}
			inst.setDataset(insts);
            inst.setValue(featureNum,classId.get(i));
            insts.add(inst);
		}
		return insts;
	}	
	
}
