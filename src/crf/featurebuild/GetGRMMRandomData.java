package crf.featurebuild;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class GetGRMMRandomData {

	public static Vector<Vector<String>> dataSet = new Vector<Vector<String>>();
	public static Vector<Vector<String>> trainSet = new Vector<Vector<String>>();
	public static Vector<Vector<String>> testSet = new Vector<Vector<String>>();
	
	public static void readAllDataFile(String filename)
	{
		dataSet.clear();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			
			String line;
			Vector<String> currentVector = new Vector<String>();
			int num = 0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					currentVector.add(line);
				}
				else if(!currentVector.isEmpty())
				{
					System.out.println("#"+num);
					num++;
					dataSet.add(currentVector);
					currentVector = new Vector<String>();
				}
			}
			freader.close();
			if(!currentVector.isEmpty())
			{
				dataSet.add(currentVector);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getTrainTestSet(int trainNum, int testNum)
	{
		Random random = new Random();
		int size = dataSet.size();
		for(int i=0; i<size*5; i++)
		{
			int num_1 = random.nextInt(size);
			int num_2 = random.nextInt(size);
			Vector<String> temp = dataSet.elementAt(num_2);
			dataSet.set(num_2, dataSet.elementAt(num_1));
			dataSet.set(num_1, temp);
		}
		trainSet.clear();
		for(int i=0; i<trainNum; i++)
		{
			trainSet.add(dataSet.elementAt(i));
		}
		testSet.clear();
		for(int i=trainNum; i<trainNum+testNum; i++)
		{
			testSet.add(dataSet.elementAt(i));
		}
	}
	
	public static void writeSetToFile(String filename, Vector<Vector<String>> dataSet, boolean isSep)
	{
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(filename));
			for(Vector<String> documentVector : dataSet)
			{
				int size = documentVector.size();
				for(int i=0; i<size; i++)
				{
					String line = documentVector.elementAt(i);
					if(!line.startsWith("<SENTENCE>"))
					{
						fwriter.write(line);
						fwriter.newLine();
					}
					else
					{
						if(isSep)
						{
							fwriter.newLine();
						}
						else
						{
							fwriter.write("<SENTENCE> ----");
							fwriter.newLine();
						}
					}
				}
				fwriter.newLine();
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getCrossValidationData(int crossNum, String outputRoot)
	{
		Random random = new Random();
		int size = dataSet.size();
		for(int i=0; i<size*5; i++)
		{
			int num_1 = random.nextInt(size);
			int num_2 = random.nextInt(size);
			Vector<String> temp = dataSet.elementAt(num_2);
			dataSet.set(num_2, dataSet.elementAt(num_1));
			dataSet.set(num_1, temp);
		}
		
		for(int i=0; i<crossNum; i++)
		{
			trainSet.clear();
			testSet.clear();
			for(int j=0; j<size; j++)
			{
				if(j%crossNum == i)
				{
					testSet.add(dataSet.elementAt(j));
				}else
				{
					trainSet.add(dataSet.elementAt(j));
				}
			}
			writeSetToFile(outputRoot+"TrainData.sep.grmm."+(i+1), trainSet, true);
			writeSetToFile(outputRoot+"TrainData.join.grmm."+(i+1), trainSet, false);
			writeSetToFile(outputRoot+"TestData.sep.grmm."+(i+1), testSet, true);
			writeSetToFile(outputRoot+"TestData.join.grmm."+(i+1), testSet, false);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readAllDataFile(args[0]);
		if(args.length==4)
		{
			int trainNum = Integer.parseInt(args[1]);
			int testNum = Integer.parseInt(args[2]);
			getTrainTestSet(trainNum, testNum);
			String outputRoot = args[3];
			if(!outputRoot.endsWith("\\") && !outputRoot.endsWith("/")) outputRoot += "/";
			writeSetToFile(outputRoot+"TrainData.sep.grmm", trainSet, true);
			writeSetToFile(outputRoot+"TrainData.join.grmm", trainSet, false);
			writeSetToFile(outputRoot+"TestData.sep.grmm", testSet, true);
			writeSetToFile(outputRoot+"TestData.join.grmm", testSet, false);
		}else
		{
			int crossNum = Integer.parseInt(args[1]);
			String outputRoot = args[2];
			if(!outputRoot.endsWith("\\") && !outputRoot.endsWith("/")) outputRoot += "/";
			getCrossValidationData(crossNum, outputRoot);
		}
	}

}
