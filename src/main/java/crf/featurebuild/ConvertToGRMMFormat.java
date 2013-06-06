package crf.featurebuild;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class ConvertToGRMMFormat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int formerGram = Integer.parseInt(args[2]);
		int afterGram = Integer.parseInt(args[3]);
		convert(args[0], args[1], formerGram, afterGram);
	}
	
	public static void convert(String inputFilename, String outputFilename, int formerGram, int afterGram)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(inputFilename));
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
			String line;
			Vector<String> lineVector = new Vector<String>();
			int num = 0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					lineVector.add(line);
				}
				else
				{
					if(!lineVector.isEmpty() )
					{
						System.out.println("#"+num);
						num++;
						String[] grmmFormatFeatures = convertToGRMMFormat(lineVector, formerGram, afterGram);
						for(int i=0; i<grmmFormatFeatures.length; i++)
						{
							fwriter.write(grmmFormatFeatures[i]);
							fwriter.newLine();
						}
						fwriter.newLine();
					}
					lineVector.clear();
				}
			}
			freader.close();
			fwriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String[] convertToGRMMFormat(Vector<String> lineVector, int formerGram, int afterGram)
	{
		int size = lineVector.size();
		String[][] featureMatrix = new String[size][];
		for(int i=0; i<size; i++)
		{
			featureMatrix[i] = lineVector.elementAt(i).split("\\s+");
		}
		String[] grmmFeatures = new String[size];
		for(int i=0; i<size; i++)
		{
			int length = featureMatrix[i].length-1;
			String line = featureMatrix[i][length]+" ----";
			if(i==0||featureMatrix[i-1].length==1) line+= " <START>";
			if(i==size-1||featureMatrix[i+1].length==1) line+= " <END>";
			for(int j=0; j<length; j++)
			{
				if(shouldWrite(featureMatrix[i][j], j))
				{
					line += " "+j+"@0"+"="+featureMatrix[i][j];
				}
				for(int k=1; k<=formerGram; k++)
				{
					int index = i-k;
					if(index >= 0 && featureMatrix[index].length>1 && featureMatrix[index+1].length>1)
					{
						if(shouldWrite(featureMatrix[index][j],j))
						{
							line += " "+j+"@-"+k+"="+featureMatrix[index][j];
						}
					}
					else
					{
						//line += " "+j+"@-"+k+"="+"<START>";
					}
				}
				for(int k=1; k<=afterGram; k++)
				{
					int index = i+k;
					if(index < size && featureMatrix[index].length>1 && featureMatrix[index-1].length>1)
					{
						if(shouldWrite(featureMatrix[index][j],j))
						{
							line += " "+j+"@"+k+"="+featureMatrix[index][j];
						}
					}
					else
					{
						//line += " "+j+"@"+k+"="+"<END>";
					}
				}
			}
			grmmFeatures[i] = line+getNGram(featureMatrix[i][0],2,0)+getNGram(featureMatrix[i][0],3,0)+getNGram(featureMatrix[i][0],4,0);
			for(int k=1; k<=formerGram; k++)
			{
				int index = i-k;
				if(index >= 0 && featureMatrix[index].length>1 && featureMatrix[index+1].length>1)
				{
					grmmFeatures[i] += getNGram(featureMatrix[index][0],2,-k)+getNGram(featureMatrix[index][0],3,-k)+getNGram(featureMatrix[index][0],4,-k);
				}
				else
				{
					//line += " "+j+"@-"+k+"="+"<START>";
				}
			}
			for(int k=1; k<=afterGram; k++)
			{
				int index = i+k;
				if(index < size && featureMatrix[index].length>1 && featureMatrix[index-1].length>1)
				{
					grmmFeatures[i] += getNGram(featureMatrix[index][0],2,k)+getNGram(featureMatrix[index][0],3,k)+getNGram(featureMatrix[index][0],4,k);
				}
				else
				{
					//line += " "+j+"@"+k+"="+"<END>";
				}
			}
		}
		return grmmFeatures;
	}
	private static boolean shouldWrite(String featureValue, int colNum)
	{
		if(colNum==0) return true;
		if(featureValue.equals("0") || featureValue.equals("N") || featureValue.equals("NULL")) return false;
		return true;
	}
	private static String getNGram(String word, int n, int pos)
	{
		String result = "";
		int end = word.length() - n;
		for(int i=0; i<=end; i++)
		{
			result += " "+pos+"="+word.substring(i, i+n);
		}
		return result;
	}

}
