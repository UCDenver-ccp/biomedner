package bioner.application.bc2gn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.FirstRankFeatureBuilder;
import bioner.normalization.GeneIDRerankFeatureBuilder;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilderFactory;
import bioner.normalization.gmclassification.ProcessImpGMClassificationByLogistic;
import bioner.normalization.gmclassification.ProcessImpGMClassificationByOther;
import bioner.normalization.gmclassification.ProcessImpGMClassificationBySVM;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinBANNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC2GNBuildNormalizationTrainData {

	public static void writerDataFile(String dataDir, String genelistFilename, String gmClassificationTrainData, String firstRankTrainFile, String rerankTrainDataFile, int maxNum) throws IOException
	{
		GlobalConfig.ReadConfigFile();
		long begintime = System.currentTimeMillis();
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		BioNERProcess[] pipeline = new BioNERProcess[5];
		pipeline[0] = new ProcessImpCRFPP(GlobalConfig.CRF_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		pipeline[1] = new ProcessImpCRFPP(GlobalConfig.CRF_INEXACT_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_INEXACT_CRF);
		//pipeline[1] = new ProcessImpProteinBANNER();
		//pipeline[0] = new ProcessImpGoldStandardNER(genelistFilename);
		//pipeline[1] = new ProcessImpProteinIndexNER();
		//pipeline[2] = new ProcessImpProteinABNER();
		pipeline[2] = new ProcessImpBC2GNGMFilter();
		pipeline[3] = new ProcessImpGetCandidateID(finder);
		pipeline[4] = new ProcessImpBC2GNFilterAfterGetCandidate();
		FirstRankFeatureBuilder featureBuilder = new BC2GNFirstRankFeatureBuilder();
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(firstRankTrainFile));
		
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
		
		for(int i=0; i<documents.length; i++)
		{
			System.out.print("Build first rank TrainData. Processing #"+i+"....");
			BioNERDocument document = documents[i];
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			int sentence_num = 0;
			int entityNum = 0;
			
			Vector<String> idVector = idTable.get(document.getID());
			if(idVector==null)
			{
				System.out.println("No gold standard!");
				continue;
			}
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
					if(candidates.length==1) continue;
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
						//if(!instanceStrVector.contains(instanceStr))
						//{
							instanceStrVector.add(instanceStr);
							fwriter.write("%"+correctID+"_"+document.getID()+"_"+entityNum+" "+correctNum);
							fwriter.newLine();
							
							fwriter.write(instanceStr);
							fwriter.newLine();
						//}
					}
				}//entity
				
			}//sentence
			
			
			
			
			System.out.println("Finished!");
		}
		fwriter.close();
		long endtime = System.currentTimeMillis();
		long time = endtime - begintime;
		System.out.println("Time spent: "+time+" ms");
		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
	}
	public static void writerGMClassificationDataFile(String dataDir, String genelistFilename, String gmClassificationTrainData, String firstRankTrainFile, String rerankTrainDataFile, int maxNum) throws IOException
	{
		long begintime = System.currentTimeMillis();
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder(dataDir);
		CandidateFinder finder = new CandidateFinder();
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		HashMap<String, Vector<String>> gmTable = getGoldGMTable(genelistFilename);
		BioNERDocument[] documents = docBuilder.buildDocuments();
		int rank = maxNum;
		BioNERProcess[] pipeline = new BioNERProcess[6];
		pipeline[0] = new ProcessImpCRFPP(GlobalConfig.CRF_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		pipeline[1] = new ProcessImpCRFPP(GlobalConfig.CRF_INEXACT_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_INEXACT_CRF);
		//pipeline[1] = new ProcessImpProteinBANNER();
		//pipeline[0] = new ProcessImpGoldStandardNER(genelistFilename);
		//pipeline[1] = new ProcessImpProteinIndexNER();
		//pipeline[2] = new ProcessImpProteinABNER();
		pipeline[2] = new ProcessImpBC2GNGMFilter();
		pipeline[3] = new ProcessImpGetCandidateID(finder);
		pipeline[4] = new ProcessImpBC2GNFilterAfterGetCandidate();
		pipeline[5] = new ProcessImpFirstRankByListNet(firstRankTrainFile, new BC2GNFirstRankFeatureBuilder());
		BufferedWriter gmClassificationFwriter = new BufferedWriter(new FileWriter(gmClassificationTrainData));
		GMClassificationFeatureBuilderFactory builderFactory = new BC2GNGMClassificationFeatureBuilderFactory();
		String[] fileAttributeHeads = builderFactory.getWekaAttributeFileHead();
		
		gmClassificationFwriter.write("@relation gm_classification");
		gmClassificationFwriter.newLine();
		gmClassificationFwriter.write("@attribute class {1,0}");
		gmClassificationFwriter.newLine();
		
		for(int i=0; i<fileAttributeHeads.length; i++)
		{
			gmClassificationFwriter.write(fileAttributeHeads[i]);
			gmClassificationFwriter.newLine();
		}
		
		gmClassificationFwriter.write("@data");
		gmClassificationFwriter.newLine();
		gmClassificationFwriter.newLine();
		
		
		
		
		
		for(int i=0; i<documents.length; i++)
		{
			System.out.print("Build gm classification TrainData. Processing #"+i+"....");
			BioNERDocument document = documents[i];
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			
			
			Vector<String> idVector = idTable.get(document.getID());
			if(idVector==null)
			{
				System.out.println("No gold standard!");
				continue;
			}
			Vector<String> goldGMTable = gmTable.get(document.getID());
			for(BioNERSentence sentence : document.getAllSentence())
			{
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					
					StringBuffer sb = new StringBuffer();
					BioNERCandidate[] candidates = entity.getCandidates();
					int correctIndex = haveCorrectID(candidates, rank, idVector);
					
					if(isCorrectGM(entity, goldGMTable) || correctIndex>=0)
					{
						sb.append("{0 1");
						//sb.append("1");
					}
					else
					{
						sb.append("{0 0");
						//sb.append("0");
					}
					String[] featureStrs = builderFactory.getFeatures(entity);
					for(int j=0; j<featureStrs.length; j++)
					{
						if(!featureStrs[j].equals("0") && !featureStrs[j].equals("0.0"))
						{
							
							sb.append(",");
							sb.append(Integer.toString(j+1));
							sb.append(" ");
							sb.append(featureStrs[j]);
						}
						//sb.append(",");
						//sb.append(featureStrs[j]);
					}
					sb.append("}");
					String line = sb.toString();
					//if(!lineVector.contains(line))
					//{
						//lineVector.add(line);
						gmClassificationFwriter.write(line);
						gmClassificationFwriter.newLine();
					//}
					
				}//entity
				
			}//sentence
			
			
			
			
			System.out.println("Finished!");
		}
		gmClassificationFwriter.close();
		
		long endtime = System.currentTimeMillis();
		long time = endtime - begintime;
		System.out.println("Time spent: "+time+" ms");
		///////////////////////////////////////////////////////////////////////////////////////////////////
	}
		
	public static void writerGeneIDRerankDataFile(String dataDir, String genelistFilename, String gmClassificationTrainData, String firstRankTrainFile, String rerankTrainDataFile, int maxNum) throws IOException
	{
		long begintime = System.currentTimeMillis();
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(rerankTrainDataFile));
		
		GeneIDRerankFeatureBuilder rerankFeatureBuilder = new BC2GNGeneIDRerankFeatureBuilder();
		
		
		String[] fileAttributeHeads = rerankFeatureBuilder.getWekaAttributeFileHead();
		
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

		
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		BioNERProcess[] pipeline = new BioNERProcess[7];
		//pipeline[0] = new ProcessImpGoldStandardNER(genelistFilename);
		pipeline[0] = new ProcessImpCRFPP(GlobalConfig.CRF_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		pipeline[1] = new ProcessImpCRFPP(GlobalConfig.CRF_INEXACT_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_INEXACT_CRF);
		//pipeline[1] = new ProcessImpProteinBANNER();
		//pipeline[0] = new ProcessImpGoldStandardNER(genelistFilename);
		//pipeline[1] = new ProcessImpProteinIndexNER();
		//pipeline[2] = new ProcessImpProteinABNER();
		pipeline[2] = new ProcessImpBC2GNGMFilter();
		pipeline[3] = new ProcessImpGetCandidateID(finder);
		pipeline[4] = new ProcessImpBC2GNFilterAfterGetCandidate();
		pipeline[5] = new ProcessImpFirstRankByListNet(firstRankTrainFile, new BC2GNFirstRankFeatureBuilder());
		pipeline[6] = new ProcessImpGMClassificationBySVM(gmClassificationTrainData, new BC2GNGMClassificationFeatureBuilderFactory());
		//pipeline[6] = new ProcessImpGMClassificationByLogistic("../../BC2GN/GMClassificationTrainData.txt", new BC2GNGMClassificationFeatureBuilderFactory());
		//pipeline[6] = new ProcessImpGMClassificationByOther("../../BC2GN/GMClassificationTrainData.txt", new BC2GNGMClassificationFeatureBuilderFactory());
		int totalEntityNum=0;
		int correctEntityNum=0;
		for(int i=0; i<documents.length ; i++)
		{
			System.out.print("Build rerank TrainData. Processing #"+i+"....");
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(documents[i]);
			}
			HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
			Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
			BuildGeneIDVectorMap.buildGeneIDVectorMap(documents[i], geneIDMap, geneIDVector, BC2GNTaskRun.top_num);
			//FilterBySpecies.filter(geneIDVector, documents[i]);
			
			BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
			for(int j=0; j<geneIDVector.size(); j++)
			{
				candidates[j] = geneIDVector.elementAt(j);
			}
			int correctNum=0;
			Vector<String> lineVector = new Vector<String>();
			Vector<String> idVector = idTable.get(documents[i].getID());
			if(idVector==null)
			{
				System.out.println("No gold standard!");
				continue;
			}
			
			for(BioNERCandidate candidate : candidates)
			{
				String id = candidate.getRecord().getID();
				//if(idVector.contains(id)) correctNum++;
				StringBuffer sb = new StringBuffer();
				if(candidates.length<=0) continue;
				totalEntityNum += geneIDMap.get(candidate.getRecord().getID()).size();
				if(idVector.contains(id))
				{
					sb.append("{0 1");
					correctEntityNum += geneIDMap.get(candidate.getRecord().getID()).size();
				}
				else
				{
					sb.append("{0 0");
				}
				String[] features = rerankFeatureBuilder.getFeatures(documents[i], geneIDMap, candidate);
				for(int k=0; k<features.length; k++)
				{
					if(!features[k].equals("0") && !features[k].equals("0.0"))
					{
						
						sb.append(",");
						sb.append(Integer.toString(k+1));
						sb.append(" ");
						sb.append(features[k]);
					}
				}
				sb.append("}");
				String line = sb.toString();
				//if(!lineVector.contains(line))
				//{
					lineVector.add(line);
					if(idVector.contains(id)) correctNum++;
				//}
			}
			if(correctNum>0)
			{
				fwriter.write("%"+documents[i].getID()+" "+correctNum);
				fwriter.newLine();
				for(String line : lineVector)
				{
					fwriter.write(line);
					fwriter.newLine();
				}
				fwriter.newLine();
			}
			documents[i]=null;
			
			System.out.println("Finished!");
		}
		
		fwriter.close();
		finder.close();
		
		long endtime = System.currentTimeMillis();
		long time = endtime - begintime;
		System.out.println("Time spent: "+time+" ms");
		double precision = (double)correctEntityNum/(double)totalEntityNum;
		System.out.println("GM total Num:"+totalEntityNum+" GM correct Num:"+correctEntityNum+" Precision:"+precision);
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String genelistFilename = "../../BC2GN/data/training.genelist";
		String dataDir = "../../BC2GN/data/trainingData";
		String outputFilename = "../../BC2GN/TrainData_10.txt";
		//writerDataFile(dataDir, genelistFilename, outputFilename, 10);
		outputFilename = "../../BC2GN/TrainData_50.txt";
		String rerankTrainFile = "../../BC2GN/RerankTrainData.txt";
		String gmClassificationTrainFile = "../../BC2GN/GMClassificationTrainData.txt";
		//writerDataFile(dataDir, genelistFilename, gmClassificationTrainFile, outputFilename, rerankTrainFile, 50);
		//writerGMClassificationDataFile(dataDir, genelistFilename, gmClassificationTrainFile, outputFilename, rerankTrainFile, 50);
		writerGeneIDRerankDataFile(dataDir, genelistFilename, gmClassificationTrainFile, outputFilename, rerankTrainFile, 50);
		genelistFilename = "../../BC2GN/data/testing.genelist";
		dataDir = "../../BC2GN/data/testingData";
		
		outputFilename = "../../BC2GN/TestData_10.txt";
		//writerDataFile(dataDir, genelistFilename, outputFilename, 10);
		outputFilename = "../../BC2GN/TestData_50.txt";
		//writerDataFile(dataDir, genelistFilename, outputFilename, 50);
		
	}
	
	public static int haveCorrectID(BioNERCandidate[] candidates, int rank, Vector<String> idVector)
	{
		if(idVector==null) return -1;
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
	
	public static HashMap<String,Vector<String>> getGoldGMTable(String filename)
	{
		HashMap<String,Vector<String>> table = new HashMap<String, Vector<String>>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\t+");
				if(parts.length<3) continue;
				String docID = parts[0];
				Vector<String> entityVector = table.get(docID);
				if(entityVector==null)
				{
					entityVector = new Vector<String>();
					table.put(docID, entityVector);
				}
				
				for(int i=2; i<parts.length; i++)
				{
					if(!entityVector.contains(parts[i]))
						entityVector.add(parts[i]);
				}
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
	
	private static boolean isCorrectGM(BioNEREntity entity, Vector<String> goldGMVector)
	{
		String gmText = entity.getText().toLowerCase().replaceAll("\\W+", "");
		for(String goldGMText : goldGMVector)
		{
			String normalGoldGMText = goldGMText.toLowerCase().replaceAll("\\W+", "");
			if(normalGoldGMText.equals(gmText))
					//|| normalGoldGMText.contains(gmText)
					//|| gmText.contains(normalGoldGMText))
				return true;
		}
		return false;
	}
}
