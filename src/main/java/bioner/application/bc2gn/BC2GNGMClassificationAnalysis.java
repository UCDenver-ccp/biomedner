package bioner.application.bc2gn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERProcessFactory;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.gmclassification.ProcessImpGMClassificationBySVM;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.normalization.rerank.GeneRerankByLogistic;
import bioner.normalization.rerank.GeneRerankBySVM;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC2GNGMClassificationAnalysis {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String genelistFilename = "../../BC2GN/data/testing.genelist";
		String dataDir = "../../BC2GN/data/testingData";
		String outputFilename = "../../BC2GN/analysis_GM_Classification.txt";
		String gmClassificationTrainFile = "../../BC2GN/GMClassificationTrainData.txt";
		
		GlobalConfig.ReadConfigFile();
		HashMap<String, Vector<String>> idTable = BC2GNBuildNormalizationTrainData.getGeneIDTable(genelistFilename);
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		BioNERProcessFactory processFactory = new BC2GNProcessFactory(finder);
		BioNERProcess[] pipeline = processFactory.buildProcessPipeline();
		
		
		
		BC2GNGMClassificationFeatureBuilderFactory featureBuilder = new BC2GNGMClassificationFeatureBuilderFactory(); 
		//ProcessImpGMClassificationBySVM classifier =  new ProcessImpGMClassificationBySVM(gmClassificationTrainFile, featureBuilder);
		
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
		int rank = 50;
		
		int sizeCountArray = 10;
		int[] fp_countArray = new int[sizeCountArray];
		int[] tp_countArray = new int[sizeCountArray];
		for(int i=0; i<sizeCountArray; i++)
		{
			fp_countArray[i] = 0;
			tp_countArray[i] = 0;
		}
		double countDistance = 1.0 / sizeCountArray;
		
		int tpNum=0;
		int fpNum=0;
		int rankErrorNum=0;
		int[] id_fp_countArray = new int[sizeCountArray];
		int[] id_tp_countArray = new int[sizeCountArray];
		for(int i=0; i<sizeCountArray; i++)
		{
			id_fp_countArray[i] = 0;
			id_tp_countArray[i] = 0;
		}
		
		
		int id_tpNum=0;
		int id_fpNum=0;
		int id_rankErrorNum=0;
		
		Vector<String> tpGeneIDResultVector = new Vector<String>();
		Vector<String> fpGeneIDResultVector = new Vector<String>();
		
		//GeneRerankByLogistic rerank = new GeneRerankByLogistic("../../BC2GN/RerankTrainData.txt", new BC2GNGeneIDRerankFeatureBuilder());
		GeneRerankBySVM rerank = new GeneRerankBySVM("../../BC2GN/RerankTrainData.txt", new BC2GNGeneIDRerankFeatureBuilder());
		for(int i=0; i<documents.length ; i++)
		{
			System.out.print("Analysis GM Classification. Processing #"+i+"....");
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
			for(BioNERSentence sentence : document.getAllSentence())
			{
				if(sentence.getAllEntities().length==0) continue;
				fwriter.write(document.getID());
				fwriter.newLine();
				fwriter.write(sentence.getSentenceText());
				fwriter.newLine();
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					BioNERCandidate[] candidates = entity.getCandidates();
					int correctIndex = BC2GNBuildNormalizationTrainData.haveCorrectID(candidates, rank, idVector);
					double score = entity.getScore();
					
					int countIndex = (int)Math.floor(score / countDistance);
					if(countIndex>=sizeCountArray) countIndex = sizeCountArray-1;
					if(correctIndex>=0) 
					{
						fwriter.write("TP:");
						tp_countArray[countIndex]++;
						tpNum++;
						if(correctIndex>0) rankErrorNum++;
					}
					else 
					{
						fwriter.write("FP:");
						fp_countArray[countIndex]++;
						fpNum++;
					}
					fwriter.write(entity.getText());
					fwriter.write("|");
					
					fwriter.write(Double.toString(score));
					fwriter.write("|");
					StringBuffer sb = new StringBuffer();
					String[] featureStrs = featureBuilder.getFeatures(entity);
					for(int j=0; j<featureStrs.length; j++)
					{
						if(!featureStrs[j].equals("0") && !featureStrs[j].equals("0.0"))
						{
							sb.append(BC2GNGMClassificationFeatureBuilderFactory.m_pipeline[j].getInfo()+" "+featureStrs[j]+",");
						}
					}
					fwriter.write(sb.toString());
					fwriter.newLine();
					
					if(correctIndex>0)
					{
						fwriter.write("rank error:"+entity.getText());
						fwriter.newLine();
						for(int j=0; j<candidates.length; j++)
						{
							if(j==correctIndex) fwriter.write("1");
							else fwriter.write("0");
							double[] features = candidates[j].getFeatures();
							for(int k=0; k<features.length; k++)
							{
								fwriter.write(" "+k+":"+features[k]);
							}
							fwriter.write("|");
							fwriter.write(candidates[j].toString());
							fwriter.newLine();
						}
					}
				}
				fwriter.newLine();
			}
			
			
			
			HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
			Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
			BuildGeneIDVectorMap.buildGeneIDVectorMap(document, geneIDMap, geneIDVector, 1);
			//FilterBySpecies.filter(geneIDVector, document);
			
			BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
			for(int j=0; j<geneIDVector.size(); j++)
			{
				candidates[j] = geneIDVector.elementAt(j);
			}
			//FrequenceGeneRerank.rerank(candidates, geneIDMap);
			rerank.rerank(document, geneIDMap, candidates);
			
			for(int j=0; j<geneIDVector.size(); j++)
			{
				if(candidates[j]==null) continue;
				double score = candidates[j].getScore();
				int countIndex = (int)Math.floor(score / countDistance);
				if(countIndex>=sizeCountArray) countIndex = sizeCountArray-1;
				if(idVector.contains(candidates[j].getRecord().getID()))
				{
					Vector<BioNEREntity> gmEntityVector = geneIDMap.get(candidates[j].getRecord().getID());
					id_tp_countArray[countIndex]++;
					id_tpNum++;
					if(countIndex>=0 && countIndex<=2)
					{
						String geneIDStr = getGeneIDResultStr(candidates[j], gmEntityVector, document);
						tpGeneIDResultVector.add(geneIDStr);
					}
				}
				else
				{
					id_fp_countArray[countIndex]++;
					id_fpNum++;
					Vector<BioNEREntity> gmEntityVector = geneIDMap.get(candidates[j].getRecord().getID());
					for(BioNEREntity gmEntity : gmEntityVector)
					{
						int gmCorrectIndex = BC2GNBuildNormalizationTrainData.haveCorrectID(gmEntity.getCandidates(), rank, idVector);
						if(gmCorrectIndex>=0)
						{
							id_rankErrorNum++;
							break;
						}
					}
					if(countIndex>=8 && countIndex<=9)
					{
						String geneIDStr = getGeneIDResultStr(candidates[j], gmEntityVector, document);
						fpGeneIDResultVector.add(geneIDStr);
					}
				}
			}
			
			
			FilterGeneIDArray.filterGeneIDArray(document, geneIDMap, candidates);
			
			
			
			System.out.println("Finished!");
		}
		
		fwriter.write("GM TP statistic:");
		fwriter.newLine();
		for(int i=0; i<sizeCountArray; i++)
		{
			double floor = i * countDistance;
			double ceil = (i+1) * countDistance;
			fwriter.write(Double.toString(floor)+"--"+Double.toString(ceil)+":"+tp_countArray[i]+" "+Double.toString((double)tp_countArray[i]/(double)tpNum));
			fwriter.newLine();
		}
		fwriter.newLine();
		
		fwriter.write("GM FP statistic:");
		fwriter.newLine();
		for(int i=0; i<sizeCountArray; i++)
		{
			double floor = i * countDistance;
			double ceil = (i+1) * countDistance;
			fwriter.write(Double.toString(floor)+"--"+Double.toString(ceil)+":"+fp_countArray[i]+" "+Double.toString((double)fp_countArray[i]/(double)fpNum));
			fwriter.newLine();
		}
		fwriter.write("Total TP:"+tpNum+" rank correct:"+(tpNum-rankErrorNum)+" rank error:"+rankErrorNum);
		fwriter.newLine();
		fwriter.write("Total FP:"+fpNum);
		fwriter.newLine();
		double precision = (double)tpNum /(double)(tpNum+fpNum);
		fwriter.write("Total precision:"+Double.toString(precision));
		fwriter.newLine();
		fwriter.newLine();
		
		fwriter.write("ID TP statistic:");
		fwriter.newLine();
		for(int i=0; i<sizeCountArray; i++)
		{
			double floor = i * countDistance;
			double ceil = (i+1) * countDistance;
			fwriter.write(Double.toString(floor)+"--"+Double.toString(ceil)+":"+id_tp_countArray[i]+" "+Double.toString((double)id_tp_countArray[i]/(double)id_tpNum));
			fwriter.newLine();
		}
		fwriter.newLine();
		
		fwriter.write("ID FP statistic:");
		fwriter.newLine();
		for(int i=0; i<sizeCountArray; i++)
		{
			double floor = i * countDistance;
			double ceil = (i+1) * countDistance;
			fwriter.write(Double.toString(floor)+"--"+Double.toString(ceil)+":"+id_fp_countArray[i]+" "+Double.toString((double)id_fp_countArray[i]/(double)id_fpNum));
			fwriter.newLine();
		}
		
		
		fwriter.write("Total TP:"+id_tpNum+" rank error:"+id_rankErrorNum);
		fwriter.newLine();
		fwriter.write("Total FP:"+id_fpNum);
		fwriter.newLine();
		precision = (double)id_tpNum /(double)(id_tpNum+id_fpNum);
		fwriter.write("Total precision:"+Double.toString(precision));
		fwriter.newLine();
		fwriter.newLine();
		
		fwriter.write("TP detail:");
		fwriter.newLine();
		for(String geneIDStr : tpGeneIDResultVector)
		{
			fwriter.write(geneIDStr);
			fwriter.newLine();
		}
		fwriter.newLine();
		fwriter.write("FP detail:");
		fwriter.newLine();
		for(String geneIDStr : fpGeneIDResultVector)
		{
			fwriter.write(geneIDStr);
			fwriter.newLine();
		}
		fwriter.newLine();
		
		fwriter.close();
	}
	
	public static String getGeneIDResultStr(BioNERCandidate candidate, Vector<BioNEREntity> entityVector, BioNERDocument document)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(document.getID()+"|"+candidate.getRecordID()+"|"+candidate.getScore()+"|");
		double[] features = candidate.getFeatures();
		for(int i=0; i<features.length; i++)
		{
			if(Math.abs(features[i]-0.0)>0.0000000000000000001)
				sb.append(Integer.toString(i)+":"+Double.toString(features[i])+" ");
		}
		for(BioNEREntity entity : entityVector)
		{
			sb.append("|");
			sb.append(entity.getText());
		}
		return sb.toString();
	}
}
