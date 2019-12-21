package dmic.stsc.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileTools {
	
	public static List<String> readFile(String fileName)
	{
		List<String> data = null;
		data = readFile(new File(fileName));
		return data;
	}

	public static List<String> readFile(File file)
	{
		List<String> data = new ArrayList<String>();
		String line;
		int inx = -1;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(file));
			while((line=br.readLine())!=null)
			{
				if (inx%10000==0)
				{
					System.out.println("已读"+String.valueOf(inx));
				}
				inx ++;
				data.add(line);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void writeFile(String filename, String[] data) {
		if(data==null)
		{
			System.exit(1);
		}
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(filename)));
			for(int i=0; i<data.length; i++)
			{
				bw.write(data[i]);
				bw.write("\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFiles(String dir, String[][] data) {
		if(data==null)
		{
			System.exit(1);
		}
		isChartPathExist(dir);
		for (int i=0; i<data.length; i++)
		{
			String fileName = dir+String.valueOf(i)+".txt";
			writeFile(fileName, data[i]);
		}
	}
	
	public static void isChartPathExist(String dirPath)
	{
		File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
	}
}
