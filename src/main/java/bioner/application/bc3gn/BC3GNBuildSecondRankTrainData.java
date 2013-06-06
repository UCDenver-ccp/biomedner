package bioner.application.bc3gn;

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
import bioner.application.bc3gn.rank.SecondRankFeatureBuilderFactory;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.ProcessImpFilterAfterGetCandidate;
import bioner.normalization.ProcessImpFilterGeneMention;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationFeatureBuilder;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.crf.ProcessImpGRMMLineCRF;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC3GNBuildSecondRankTrainData {

	public static void writerDataFile(String dataDir, String genelistFilename, String firstRankTrainDataFilename, String outputFilename, int maxNum) throws IOException
	{
		GlobalConfig.ReadConfigFile();
		
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		BioNERProcess[] pipeline = new BioNERProcess[7];
		//pipeline[0] = new ProcessImpGoldStandardNER(genelistFilename);
		pipeline[0] = new ProcessImpCRFPP();
		//pipeline[0] = new ProcessImpGRMMLineCRF();
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		
		
		//pipeline[0] = new ProcessImpGoldStandardNER("../../BC3GN/TrainingSet1.gm.txt");
		pipeline[3] = new ProcessImpFilterGeneMention();
		pipeline[4] = new ProcessImpGetCandidateID(finder);
		pipeline[5] = new ProcessImpFilterAfterGetCandidate();
		pipeline[6] = new ProcessImpFirstRankByListNet(firstRankTrainDataFilename, new BC3GNFirstRankFeatureBuilder());
		
		NormalizationFeatureBuilder featureBuilder = new NormalizationFeatureBuilder();
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
		
		String[] fileAttributeHeads = SecondRankFeatureBuilderFactory.getWekaAttributeFileHead();
		
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
		Vector<String> instanceStrVector = new Vector<String>();
		for(int i=0; i<documents.length ; i++)
		{
			//if(i!=19) continue;
			//if(!documents[i].getID().equals("2730050")) continue;
			long beginTime = System.currentTimeMillis();
			System.out.print("Build second rank train data. Processing #"+i+" "+documents[i].getID()+"....");
			BioNERDocument document = documents[i];
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			int sentence_num = 0;
			int entityNum = 0;
			Vector<String> idVector = idTable.get(document.getID());
			if(idVector==null) continue;
			for(BioNERSentence sentence : document.getAllSentence())
			{
				sentence_num++;
				//System.out.println("Sentence #"+sentence_num);
				//fwriter.write(sentence.getSentenceText());
				//fwriter.newLine();
				
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					StringBuffer sb = new StringBuffer();
					BioNERCandidate[] candidates = entity.getCandidates();
					int correctIndex = haveCorrectID(candidates, rank, idVector);
					
					if(correctIndex >= 0)
					{
						entityNum++;
						String correctID = candidates[correctIndex].getRecord().getID();
						
						Vector<String> lineVector = new Vector<String>();
						int correctNum = 0;
						for(int j=0; j<rank && j<candidates.length; j++)
						{
							String recordID = candidates[j].getRecord().getID();
							StringBuffer sbLine = new StringBuffer();
							if(idVector.contains(recordID))
							{
								sbLine.append("1");
								correctNum++;
							}
							else
							{
								sbLine.append("0");
							}
							String[] features = SecondRankFeatureBuilderFactory.getFeatures(candidates[j]);
							for(int k=0; k<features.length; k++)
							{
								sbLine.append(","+features[k]);
							}
							
							String line = sbLine.toString();
							if(!lineVector.contains(line)) lineVector.add(line);
							//fwriter.write("|"+candidates[j].getRecord().toString());
							
							
						}//candidate
						for(String line : lineVector)
						{
							sb.append(line);
							sb.append("\n");
						}
						String instanceStr = sb.toString();
						if(!instanceStrVector.contains(instanceStr))
						{
							instanceStrVector.add(instanceStr);
							fwriter.write("%"+correctID+"_"+document.getID()+"_"+entityNum+" "+correctNum);
							fwriter.newLine();
							
							fwriter.write(instanceStr);
							fwriter.newLine();
						}
					}
				}//entity
				
			}//sentence
			
			
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
		String outputFilename = "../../BC3GN/SecondRankTrainData.txt";
		//writerDataFile(dataDir, genelistFilename, outputFilename, 10);
		//outputFilename = "../../BC3GN/TrainData_50.txt";
		String firstRankTrainData = "../../BC3GN/TrainData_50.txt";
		
		if(args.length==4)
		{
			dataDir = args[0];
			genelistFilename = args[1];
			firstRankTrainData = args[2];
			outputFilename = args[3];
		}
		else {
			System.out.println("BC3GNBuildSecondRankTrainData got " + args.length +  " args, using defaults.");
            for (String s: args) {
			    System.out.println("  " + s);
            }
		}
        System.out.println("BC3GNBuildSecondRankTrainingData: " );
        System.out.println("  dataDir" + dataDir );
        System.out.println("  genelist" + genelistFilename );
        System.out.println("  firstRank" + firstRankTrainData );
        System.out.println("  output"  + outputFilename);

		
		writerDataFile(dataDir, genelistFilename, firstRankTrainData, outputFilename, 5);
		
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
            System.out.println("BC3GNBuildSecondRankTrainData error: " + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
            System.out.println("BC3GNBuildSecondRankTrainData error: " + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		return table;
	}
}
