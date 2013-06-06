package crf.featurebuild;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import bioner.global.GlobalConfig;
import crf.featurebuild.bc2gm.BC2GMFeatureBuildDocumentBuilder;


public class FeatureFileMerger {
	
	public static void BuildCRFPPTrainTestDataFile(File[] files, String outputDIR, int num)
	{
		File outputDIRFile = new File(outputDIR);
		if(!outputDIRFile.exists())
		{
			outputDIRFile.mkdirs();
		}
		
		Vector<File> trainFileVector = new Vector<File>();
		Vector<File> testFileVector = new Vector<File>();
		for(int i=0; i<files.length; i++)
		{
			if(i%1 == num)
			{
				testFileVector.add(files[i]);
			}else
			{
				trainFileVector.add(files[i]);
			}
		}
		int size = trainFileVector.size();
		File[] trainFiles = new File[size];
		for(int i=0; i<size; i++)
		{
			trainFiles[i] = trainFileVector.elementAt(i);
		}
		
		size = testFileVector.size();
		File[] testFiles = new File[size];
		for(int i=0; i<size; i++)
		{
			testFiles[i] = testFileVector.elementAt(i);
		}
		
		if(!outputDIR.endsWith("/"))
		{
			outputDIR += "/";
		}
		
		//MergeFileList(trainFiles, outputDIR+"TrainData."+num+".crf");
		//MergeFileList(testFiles, outputDIR+"TestData."+num+".crf");
		MergeFileList(testFiles, outputDIR+"TrainData.crfpp");
	}
	
	public static void MergeFileList(File[] files, String outputFilename)
	{
		Vector<String> lineVector = new Vector<String>();
		try {
			
			
			for(File file : files)
			{
				BufferedReader freader = new BufferedReader(new FileReader(file));
				String line;
				while((line=freader.readLine()) != null)
				{
					lineVector.add(line);
				}
				freader.close();
			}
			int size = lineVector.size();
			
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename+".forward"));
			for(int i=0; i<size; i++)
			{
				String line = lineVector.elementAt(i);
				fwriter.write(line);
				fwriter.newLine();
			}
			fwriter.close();
			
			fwriter = new BufferedWriter(new FileWriter(outputFilename+".backward"));
			for(int i=size-1; i>=0; i--)
			{
				String line = lineVector.elementAt(i);
				fwriter.write(line);
				fwriter.newLine();
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*File root = new File(GlobalConfig.BC2_GM_TRAIN_OUTPUT_PATH);
		File[] files = root.listFiles();
		BuildCRFPPTrainTestDataFile(files, GlobalConfig.BC2_GM_CRF_TRAIN_TEST_OUTPUT_DIR,0);*/
		File root = new File(args[0]);
		File[] files = root.listFiles();
		BuildCRFPPTrainTestDataFile(files, args[1],0);
	}
}
