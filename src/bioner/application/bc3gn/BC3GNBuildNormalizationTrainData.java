package bioner.application.bc3gn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.ProcessImpGoldStandardNER;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.ProcessImpFilterAfterGetCandidate;
import bioner.normalization.ProcessImpFilterGeneMention;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationFeatureBuilder;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.crf.ProcessImpGRMMLineCRF;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinBANNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC3GNBuildNormalizationTrainData {

	public static void writerDataFile(String dataDir, String genelistFilename, String outputFilename, int maxNum) 
	throws IOException {
		writerDataFile(dataDir, genelistFilename, outputFilename, maxNum, GlobalConfig.CRF_INEXACT_MODEL_FILEPATH);
	}

	public static void writerDataFile(String dataDir, String genelistFilename, String outputFilename, int maxNum,
		String modelFilepath) 
	throws IOException
	{
		GlobalConfig.ReadConfigFile();
		
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		BioNERProcess[] pipeline = new BioNERProcess[6];
		//pipeline[0] = new ProcessImpCRFPP(GlobalConfig.CRF_INEXACT_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		pipeline[0] = new ProcessImpCRFPP(modelFilepath, GlobalConfig.ENTITY_LABEL_CRF);
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		
		
		//pipeline[0] = new ProcessImpGoldStandardNER("../../BC3GN/TrainingSet1.gm.txt");
		pipeline[3] = new ProcessImpFilterGeneMention();
		pipeline[4] = new ProcessImpGetCandidateID(finder);
		pipeline[5] = new ProcessImpFilterAfterGetCandidate();
		
		BC3GNFirstRankFeatureBuilder featureBuilder = new BC3GNFirstRankFeatureBuilder();
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
		
		String[] fileAttributeHeads = featureBuilder.getWekaAttributeFileHead();
		
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
		Vector<String> goldDocIDVector = new Vector<String>();
		for(int i=0; i<documents.length ; i++)
		{
			//if(i>=0) continue;
			//if(!documents[i].getID().equals("2730050")) continue;
			long beginTime = System.currentTimeMillis();
			System.out.print("Build first rank train data. Processing #"+i+" "+documents[i].getID()+"....");
			BioNERDocument document = documents[i];
			goldDocIDVector.add(document.getID());
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
					
					if(correctIndex >= 0 && candidates.length>1)
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
							String[] features = featureBuilder.getFeatures(candidates[j]);
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
		
		/*GlobalConfig.BC3GN_DATADIR = "../../BC3GN/525_data/";
		File[] files = (new File("../../BC3GN/525_data/")).listFiles();
		idTable = getGeneIDTable("../../BC3GN/TrainingSet2.txt");
		for(int i=0; i<files.length; i++)
		{
			long beginTime = System.currentTimeMillis();
			BioNERDocument document = docBuilder.getOneDocument(files[i]);
			//if(!document.getID().equals("2883592")) continue;
			System.out.print("Build first rank train data for 523 data. #"+i+" "+document.getID()+"....");
			Vector<String> idVector = idTable.get(document.getID());
			if(idVector==null) 
			{
				System.out.println("no gold standard.");
				continue;
			}
			if(goldDocIDVector.contains(document.getID()))
			{
				System.out.println("in 32 gold standard. Skip.");
				continue;
			}
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			int sentence_num = 0;
			int entityNum = 0;
			idVector = idTable.get(document.getID());
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
					
					if(correctIndex >= 0 && candidates.length>1)
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
							String[] features = featureBuilder.getFeatures(candidates[j]);
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
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			System.out.println("Finished! "+time+" ms");
		}*/
		
		fwriter.close();
		finder.close();
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String dataDir = "../../BC3GN/xmls/";
		String genelistFilename = "../../BC3GN/data/TrainingSet2.txt";
		String outputFilename = "../../BC3GN/TrainData_10.txt";
		String modelFilepath = GlobalConfig.CRF_INEXACT_MODEL_FILEPATH;
	

        System.out.println("Java LIbrary Path:" + System.getProperty("java.library.path"));
	
		if(args.length==4) {
			dataDir = args[0];
			genelistFilename = args[1];
			outputFilename = args[2];
			modelFilepath = args[3];
		}

		System.out.println("running with dataDir: " + dataDir);
		System.out.println("running with genelistFilename: " + genelistFilename);
		System.out.println("running with output filename: " + outputFilename);
		System.out.println("running modelFilepath: " + modelFilepath);


		GlobalConfig.BC3GN_DATADIR = dataDir;
		writerDataFile(dataDir, genelistFilename, outputFilename, 50, modelFilepath);
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
            System.err.println("BC3GNBuildNormalizationTrainData error:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
            System.err.println("BC3GNBuildNormalizationTrainData error:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		return table;
	}
}
