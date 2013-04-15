package bioner.application.webtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;

import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationFeatureBuilder;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.normalization.rerank.FilterBySpecies;
import bioner.normalization.rerank.GeneRerankByLogistic;
import bioner.process.BioNERProcess;


public class BC3GNAnalysisRankData {

	public static void writerDataFile(String dataDir, String genelistFilename, String trainingDataFilename, String secondRankTrainData, String rerankTrainFilename,String outputFilename, String gmOutputFilename, String gmPrecisionOutputFilename, int maxNum) throws IOException
	{
		GlobalConfig.ReadConfigFile();
		
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		BC3GNProcessFactory pipelineFactory = new BC3GNProcessFactory(finder,trainingDataFilename, secondRankTrainData, rerankTrainFilename);
		BioNERProcess[] pipeline = pipelineFactory.buildProcessPipeline();
		
		
		NormalizationFeatureBuilder featureBuilder = new NormalizationFeatureBuilder();
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
		
		String[] fileAttributeHeads = NormalizationFeatureBuilder.getWekaAttributeFileHead();
		
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
		
		BufferedWriter gmfwriter = new BufferedWriter(new FileWriter(gmOutputFilename));
		BufferedWriter precisionFwriter = new BufferedWriter(new FileWriter(gmPrecisionOutputFilename));
		
		BufferedWriter rerankFwriter = new BufferedWriter(new FileWriter(gmOutputFilename+".rerank"));
		
		int tpID_5=0;
		int tpID_50=0;
		int tpID_1=0;
		int tpID_10 = 0;
		int tpID_20 = 0;
		int totalTP = 0;
		int totalFP = 0;
		
		int inner_species_errorNum = 0;
		int inter_species_errorNum = 0;
		
		HashMap<String, Integer> allTPTable = new HashMap<String, Integer>();
		HashMap<String, Integer> allFPTable = new HashMap<String, Integer>();
		int rank = maxNum;
		GeneRerankByLogistic logRerank = new GeneRerankByLogistic(rerankTrainFilename, new BC3GNGeneIDRerankFeatureBuilder());
		for(int i=0; i<documents.length ; i++)
		{
			int doc_inner_species_errorNum = 0;
			int doc_inter_species_errorNum = 0;
			int doc_tpID_5=0;
			int doc_tpID_10=0;
			int doc_tpID_20=0;
			int doc_tpID_50=0;
			int doc_tpID_1=0;
			Vector<String> innerErrorIDVector = new Vector<String>();
			Vector<String> interErrorIDVector = new Vector<String>();
			long beginTime = System.currentTimeMillis();
			System.out.print("Analysis Processing #"+i+" "+documents[i].getID()+"....");
			BioNERDocument document = documents[i];
			HashMap<String, Integer> docTPTable = new HashMap<String, Integer>();
			HashMap<String, Integer> docFPTable = new HashMap<String, Integer>();
			gmfwriter.write("Doc ID:"+document.getID());
			gmfwriter.newLine();
			//if(!document.getID().equals("9462743")) continue;
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			
			
			int sentence_num = 0;
			int entityNum = 0;
			Vector<String> idVector = idTable.get(document.getID());
			if(idVector==null) continue;
			
			doc_tpID_1 += getGeneIDTP(document, idVector, 1);
			doc_tpID_5 += getGeneIDTP(document, idVector, 5);
			doc_tpID_10 += getGeneIDTP(document, idVector, 10);
			doc_tpID_20 += getGeneIDTP(document, idVector, 20);
			doc_tpID_50 += getGeneIDTP(document, idVector, 50);
			tpID_1 += doc_tpID_1;
			tpID_5 += doc_tpID_5;
			tpID_10 += doc_tpID_10;
			tpID_20 += doc_tpID_20;
			tpID_50 += doc_tpID_50;
			
			
			for(BioNERSentence sentence : document.getAllSentence())
			{
				sentence_num++;
				//System.out.println("Sentence #"+sentence_num);
				//fwriter.write(sentence.getSentenceText());
				//fwriter.newLine();
				
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					
					BioNERCandidate[] candidates = entity.getCandidates();
					if(candidates.length<=0) continue;
					int correctIndex = haveCorrectID(candidates, rank, idVector);
					
					if(correctIndex<0)
					{
						totalFP++;
						for(String label : entity.getLabelVector())
						{
							Integer fp = allFPTable.get(label);
							if(fp==null) fp = 0;
							fp++;
							allFPTable.put(label, fp);
							fp = docFPTable.get(label);
							if(fp==null) fp = 0;
							fp++;
							docFPTable.put(label, fp);
						}
					}else
					{
						totalTP++;
						for(String label : entity.getLabelVector())
						{
							Integer tp = allTPTable.get(label);
							if(tp==null) tp = 0;
							tp++;
							allTPTable.put(label, tp);
							tp = docTPTable.get(label);
							if(tp==null) tp = 0;
							tp++;
							docTPTable.put(label, tp);
						}
					}
					
					
					if(correctIndex<0)
					{
						gmfwriter.write("GM_FP:"+entity.getText()+"|"+candidates[0].toString());
						gmfwriter.write("|labels:");
						for(String label : entity.getLabelVector())
						{
							gmfwriter.write(label+" ");
						}
						gmfwriter.newLine();
					}
					else if (correctIndex==0)
					{
						gmfwriter.write("GM_TP_GN_TP:"+entity.getText()+"|"+candidates[0].toString());
						gmfwriter.write("|labels:");
						for(String label : entity.getLabelVector())
						{
							gmfwriter.write(label+" ");
						}
						gmfwriter.newLine();
					}
					
					else if (correctIndex > 0)
					{
						entityNum++;
						String correctID = candidates[correctIndex].getRecord().getID();
						if(candidates[0].getRecord().getSpeciesID().equals(candidates[correctIndex].getRecord().getSpeciesID()))
						{
							if(!innerErrorIDVector.contains(correctID))
							{
								innerErrorIDVector.add(correctID);
								inner_species_errorNum++;
								doc_inner_species_errorNum++;
							}
						}
						else
						{
							if(!interErrorIDVector.contains(correctID))
							{
								interErrorIDVector.add(correctID);
								inter_species_errorNum++;
								doc_inter_species_errorNum++;
							}
						}
						
						fwriter.write(sentence.getSentenceText());
						fwriter.newLine();
						
						fwriter.write(entity.getText());
						fwriter.newLine();
						
						
						
						gmfwriter.write("GM_TP_GN_FP:"+entity.getText()+"|"+candidates[0].toString());
						gmfwriter.write("|labels:");
						for(String label : entity.getLabelVector())
						{
							gmfwriter.write(label+" ");
						}
						gmfwriter.newLine();
						
						
						fwriter.write("%"+correctID+"_"+document.getID()+"_"+entityNum+" 1");
						fwriter.newLine();
						for(int j=0; j<rank && j<candidates.length; j++)
						{
							if(j==correctIndex)
							{
								fwriter.write("1");
							}
							else
							{
								fwriter.write("0");
							}
							String[] features = featureBuilder.getFeatures(candidates[j]);
							for(int k=0; k<features.length; k++)
							{
								fwriter.write(","+features[k]);
							}
							
							
							fwriter.write("\t"+candidates[j].getRecord().toString());
							fwriter.newLine();
						}//candidate
						fwriter.newLine();
					}
				}//entity
				
			}//sentence
			
			
			
			gmfwriter.newLine();
			precisionFwriter.write(document.getID()+":");
			precisionFwriter.newLine();
			for(String label : docFPTable.keySet())
			{
				Integer tp = docTPTable.get(label);
				if(tp==null) tp=0;
				Integer fp = docFPTable.get(label);
				double precision = (double)tp / (double)(tp + fp);
				precisionFwriter.write(label+": TP="+tp+" FP="+fp+" Precision="+precision);
				
				precisionFwriter.newLine();
			}
			//precisionFwriter.newLine();
			int totalNum = idVector.size();
			precisionFwriter.write("Gene ID num:"+totalNum);
			precisionFwriter.newLine();
			precisionFwriter.write("inner-species error:"+doc_inner_species_errorNum+" inter-species error:"+doc_inter_species_errorNum);
			precisionFwriter.newLine();
			precisionFwriter.write("Top50:tp="+doc_tpID_50+" cover rate:"+((double)doc_tpID_50/(double)totalNum));
			precisionFwriter.newLine();
			precisionFwriter.write("Top20:tp="+doc_tpID_20+" cover rate:"+((double)doc_tpID_20/(double)totalNum));
			precisionFwriter.newLine();
			precisionFwriter.write("Top10:tp="+doc_tpID_10+" cover rate:"+((double)doc_tpID_10/(double)totalNum));
			precisionFwriter.newLine();
			precisionFwriter.write("Top5:tp="+doc_tpID_5+" cover rate:"+((double)doc_tpID_5/(double)totalNum));
			precisionFwriter.newLine();
			precisionFwriter.write("Top1:tp="+doc_tpID_1+" cover rate:"+((double)doc_tpID_1/(double)totalNum));
			precisionFwriter.newLine();
			precisionFwriter.newLine();
			
			
			
			HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
			Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
			BuildGeneIDVectorMap.buildGeneIDVectorMap(documents[i], geneIDMap, geneIDVector, BC3GNTaskRun.rank);
			FilterBySpecies.filter(geneIDVector, documents[i], geneIDMap);
			
			BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
			for(int j=0; j<geneIDVector.size(); j++)
			{
				candidates[j] = geneIDVector.elementAt(j);
			}
			//FrequenceGeneRerank.rerank(candidates, geneIDMap);
			logRerank.rerank(document, geneIDMap, candidates);
			for(int j=0; j<candidates.length; j++)
			{
				rerankFwriter.write(document.getID());
				rerankFwriter.write("\t"+(j+1)+"\t"+candidates[j].getRecord().getID()+"\t"+candidates[j].getScore()+"\t");
				if(idVector.contains(candidates[j].getRecord().getID()))
				{
					rerankFwriter.write("TP");
				}
				else
				{
					rerankFwriter.write("FP");
				}
				
				double[] feature = candidates[j].getFeatures();
				for(int k=0; k<feature.length; k++)
				{
					rerankFwriter.write("\t"+k+":"+Double.toString(feature[k]));
				}
				rerankFwriter.newLine();
			}
			
			
			
			
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			documents[i]=null;
			System.out.println("Finished! "+time+" ms");
		}
		fwriter.close();
		gmfwriter.close();
		rerankFwriter.close();
		
		precisionFwriter.write("Overall:");
		precisionFwriter.newLine();
		for(String label : allFPTable.keySet())
		{
			Integer tp = allTPTable.get(label);
			if(tp==null) tp=0;
			Integer fp = allFPTable.get(label);
			double precision = (double)tp / (double)(tp + fp);
			precisionFwriter.write(label+": TP="+tp+" FP="+fp+" Precision="+precision);
			precisionFwriter.newLine();
		}
		double precision = (double)totalTP / (double)(totalTP + totalFP);
		precisionFwriter.write("Total: TP="+totalTP+" FP="+totalFP+" Precision="+precision);
		precisionFwriter.newLine();
		int totalNum = 0;
		for(String key : idTable.keySet())
		{
			totalNum += idTable.get(key).size();
		}
		precisionFwriter.write("total Gene ID num:"+totalNum);
		precisionFwriter.newLine();
		precisionFwriter.write("inner-species error:"+inner_species_errorNum+" inter-species error:"+inter_species_errorNum);
		precisionFwriter.newLine();
		precisionFwriter.write("Top50:tp="+tpID_50+" cover rate:"+((double)tpID_50/(double)totalNum));
		precisionFwriter.newLine();
		precisionFwriter.write("Top20:tp="+tpID_20+" cover rate:"+((double)tpID_20/(double)totalNum));
		precisionFwriter.newLine();
		precisionFwriter.write("Top10:tp="+tpID_10+" cover rate:"+((double)tpID_10/(double)totalNum));
		precisionFwriter.newLine();
		precisionFwriter.write("Top5:tp="+tpID_5+" cover rate:"+((double)tpID_5/(double)totalNum));
		precisionFwriter.newLine();
		precisionFwriter.write("Top1:tp="+tpID_1+" cover rate:"+((double)tpID_1/(double)totalNum));
		precisionFwriter.newLine();
		
		precisionFwriter.close();
		finder.close();
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		String genelistFilename = "../../BC3GN/TrainingSet1.txt";
		String dataDir = "../../BC3GN/xmls/";
		
		String outputFilename = "../../BC3GN/TestData_analysis_50.txt";
		String trainingDataFilename = "../../BC3GN/TrainData_50.txt";
		String gmOutputFilename = "../../BC3GM/GM_analysis.txt";
		String rerankTrainFilename = "";
		String gmPrecisionOutputFilename = "../../BC3GM/GM_Precision.txt";
		String secondRankTrainFilename = "../../BC3GN/secondRankTrainData.txt";
		if(args.length==8)
		{
			dataDir = args[0];
			trainingDataFilename = args[1];
			secondRankTrainFilename = args[2];
			rerankTrainFilename = args[3];
			
			genelistFilename = args[4];
			outputFilename = args[5];
			gmOutputFilename = args[6];
			gmPrecisionOutputFilename = args[7];
		}
		
		
		writerDataFile(dataDir, genelistFilename,trainingDataFilename, secondRankTrainFilename, rerankTrainFilename, outputFilename, gmOutputFilename, gmPrecisionOutputFilename, 50);
		
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
	public static int getGeneIDTP(BioNERDocument document, Vector<String> vector, int rank)
	{
		int num=0;
		for(String id : vector)
		{
			boolean isCovered = false;
			for(BioNERSentence sentence : document.getAllSentence())
			{
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					BioNERCandidate[] candidates = entity.getCandidates();
					for(int i=0; i<rank&&i<candidates.length; i++)
					{
						if(candidates[i].getRecord().getID().equals(id))
						{
							isCovered = true;
							break;
						}
					}
					if(isCovered) break;
				}
				if(isCovered) break;
			}
			if(isCovered) num++;
		}
		
		return num;
	}
}
