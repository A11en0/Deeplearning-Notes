package dmic.semi.sstsc.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Doc2Vector {
	private String externalCorpusFileName;
	
	private Map<String, Integer> words = null;
	public double[][] word2vec;
	public int wordsize;

	public Doc2Vector(String externalCorpusFileName) 
	{
		super();
		this.externalCorpusFileName = externalCorpusFileName;
		init();
	}
	
	
	private void init() 
	{

		String line;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(externalCorpusFileName)));
			String[] parms = null;
			if((line=br.readLine())!=null)
			{
				parms = line.split(" ");				
			}
			wordsize = Integer.parseInt(parms[1]);
			words = new HashMap<String, Integer>(Integer.parseInt(parms[0]));
			word2vec = new double[Integer.parseInt(parms[0])][wordsize];
			int i = 0;
			while((line=br.readLine())!=null)
			{
				String[] values = line.split(" ");
				words.put(values[0], i);
				for(int j=1; j<values.length; j++)
				{
					word2vec[i][j-1] = Double.valueOf(values[j]);
				}
				i++;
				if(i%10000 == 0)
					System.out.println(String.valueOf(i));
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void run(SemiShortTexts shortTexts, int chunkNum, int chunkSize)
	{
		List<String> data = shortTexts.originalShortTexts;
		shortTexts.VectorShortTexts = new List[chunkNum];
		shortTexts.featureNum = wordsize;
		int inx = 0;
		for (int i=0; i<chunkNum; i++)
		{
			shortTexts.VectorShortTexts[i] = new ArrayList<String>();
			for (int j=0; (j<chunkSize) &&(inx<shortTexts.length); j++)
			{
				StringBuilder sb = new StringBuilder("");
				String line = data.get(inx++);
				String[] values = line.split(" ");
				double[] probs = new double[wordsize];
				for(int k=0; k<values.length; k++)
				{
					if(!words.containsKey(values[k]))
					{
						System.err.println(String.valueOf(inx+1)+" "+values[k]+" No matching words!");
						continue;
					}
					int wordinx = words.get(values[k]);
					for(int m=0; m<wordsize; m++)
					{
						probs[m] += word2vec[wordinx][m];
					}
				}
				for(int k=0; k<wordsize; k++)
				{
					sb.append(probs[k] /= wordsize);
					sb.append(" ");
				}				
				String str = sb.toString();
				if (sb.length() != 0)
				{
					shortTexts.VectorShortTexts[i].add(str.substring(0,sb.length()-1));			
				}
			}
		}
		
	}
}
