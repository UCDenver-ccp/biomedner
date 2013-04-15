package bioner.application.bc3gn.rank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.ProcessImpGoldStandardNER;
import bioner.application.bc3gn.BC3GNDataFileReader;
import bioner.application.bc3gn.BC3GNFirstRankFeatureBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.FirstRankFeatureBuilder;
import bioner.normalization.ProcessImpFilterAfterGetCandidate;
import bioner.normalization.ProcessImpFilterAfterRank;
import bioner.normalization.ProcessImpFilterGeneMention;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.ProcessImpSecondRankByListNet;
import bioner.normalization.ProcessImpSecondRankByRankNet;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationFeatureBuilder;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.crf.ProcessImpGRMMLineCRF;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC3GNBuildGMRerankTrainData {

	public static void writerDataFile(String dataDir, String genelistFilename, String candidateTrainDataFilename, String secondRankTrainData,String outputFilename, int maxNum) throws IOException
	{
		GlobalConfig.ReadConfigFile();
		
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		FirstRankFeatureBuilder firstFeatureBuilder = new BC3GNFirstRankFeatureBuilder();
		BioNERProcess[] pipeline = new BioNERProcess[9];
		//pipeline[0] = new ProcessImpGoldStandardNER(genelistFilename);
		pipeline[0] = new ProcessImpCRFPP();
		//pipeline[0] = new ProcessImpGRMMLineCRF();
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		
		//pipeline[0] = new ProcessImpGoldStandardNER("../../BC3GN/TrainingSet1.gm.txt");
		pipeline[3] = new ProcessImpFilterGeneMention();
		pipeline[4] = new ProcessImpGetCandidateID(finder);
		pipeline[5] = new ProcessImpFilterAfterGetCandidate();
		pipeline[6] = new ProcessImpFirstRankByListNet(candidateTrainDataFilename, firstFeatureBuilder);
		//pipeline[6] = new ProcessImpSecondRankByListNet(secondRankTrainData);
		pipeline[7] = new ProcessImpSecondRankByRankNet(secondRankTrainData);
		pipeline[8] = new ProcessImpFilterAfterRank();
		
		
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
		
		String[] fileAttributeHeads = GeneIDRerankFeatureBuilderFactory.getWekaAttributeFileHead();
		
		fwriter.write("@relation gene_normalization");
		fwriter.newLine();
		fwriter.write("@attribute class {1,0}");
		fwriter.newLine();
		
		for(int i=0; i<fileAttributeHeads.length; i++)
		{
			fwriter.write(fileAttributeHeads[i]);
			fwriter.newLine();
		}
		
		fwriter.write("@data");
		fwriter.newLine();
		fwriter.newLine();
		
		
		int rank = maxNum;
		for(int i=0; i<documents.length ; i++)
		{
			//if(i!=9) continue;
			long beginTime = System.currentTimeMillis();
			System.out.print("Build rerank train data. Processing #"+i+" "+documents[i].getID()+"....");
			BioNERDocument document = documents[i];
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			Vector<String> idVector = idTable.get(document.getID());
			if(idVector==null) continue;
			
			int correctTopNum = 0;
			for(BioNERSentence sentence : document.getAllSentence())
			{
				
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					BioNERCandidate[] candidates = entity.getCandidates();
					int correctIndex = haveCorrectID(candidates, rank, idVector);
					if(correctIndex==0) correctTopNum++;
				}
			}
			
			if(correctTopNum==0) continue;
			
			
			
			int correctNum=0;
			Vector<String> lineVector = new Vector<String>();
			for(BioNERSentence sentence : document.getAllSentence())
			{
				
				
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					StringBuffer sb = new StringBuffer();
					BioNERCandidate[] candidates = entity.getCandidates();
					if(candidates.length<=0) continue;
					int correctIndex = haveCorrectID(candidates, rank, idVector);
					
					if(correctIndex == 0)
					{
						sb.append("1");
					}
					else
					{
						sb.append("0");
					}
					String[] features = GeneIDRerankFeatureBuilderFactory.getFeatures(candidates[0]);
					for(int k=0; k<features.length; k++)
					{
						sb.append(","+features[k]);
					}
					String line = sb.toString();
					if(!lineVector.contains(line))
					{
						lineVector.add(line);
						if(correctIndex==0) correctNum++;
					}
					
				}//entity
				
			}//sentence
			
			
			fwriter.write("%"+document.getID()+" "+correctNum);
			fwriter.newLine();
			for(String line : lineVector)
			{
				fwriter.write(line);
				fwriter.newLine();
			}
			fwriter.newLine();
			documents[i]=null;
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			System.out.println("Finished! "+time+" ms");
		}
		fwriter.close();
		finder.close();
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String genelistFilename = "../../BC3GN/data/TrainingSet2.txt";
		String dataDir = "../../BC3GN/xmls/";
		String outputFilename = "../../BC3GN/TrainData_10.txt";
		String candidateTrainDataFilename = "../../BC3GN/TrainData_50.txt";
		//writerDataFile(dataDir, genelistFilename, outputFilename, 10);
		outputFilename = "../../BC3GN/RerankTrainData.txt";
		String secondRankTrainData = "../../BC3GN/secondRankTrainData.txt";
		if(args.length==5)
		{
			dataDir = args[0];
			genelistFilename = args[1];
			candidateTrainDataFilename = args[2];
			secondRankTrainData = args[3];
			outputFilename = args[4];
		}
		
		writerDataFile(dataDir, genelistFilename, candidateTrainDataFilename, secondRankTrainData, outputFilename, 50);
		
		/*
		genelistFilename = "../../BC2GN/data/testing.genelist";
		dataDir = "../../BC2GN/data/testingData";
		
		outputFilename = "../../BC2GN/TestData_10.txt";
		//writerDataFile(dataDir, genelistFilename, outputFilename, 10);
		outputFilename = "../../BC2GN/TestData_50.txt";
		//writerDataFile(dataDir, genelistFilename, outputFilename, 50);*/
		
	}
	
	public static int haveCorrectID(BioNERCandidate[] candidates, int rank, Vector<String> idVector)
	{
		for(int i=0; i<rank && i<candidates.length; i++)
		{
			String id= candidates[i].getRecord().getID();
			if(idVector.contains(id)) return i;
		}
		return -1;
	}
	
	
	public static HashMap<String,Vector<String>> getGeneIDTable(String filename)
	{
		HashMap<String,Vector<String>> table = new HashMap<String, Vector<String>>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\t+");
				if(parts.length<2) continue;
				String docID = parts[0];
				Vector<String> entityVector = table.get(docID);
				if(entityVector==null)
				{
					entityVector = new Vector<String>();
					table.put(docID, entityVector);
				}
				
				entityVector.add(parts[1]);
				
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;
	}
}
