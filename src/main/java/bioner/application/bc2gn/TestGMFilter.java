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
import bioner.normalization.FilterListBuilder;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.gmclassification.ProcessImpGMClassificationBySVM;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinBANNER;

public class TestGMFilter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		long begintime = System.currentTimeMillis();
		HashMap<String, Vector<String>> idTable = BC2GNBuildNormalizationTrainData.getGeneIDTable("../../BC2GN/data/testing.genelist");
		HashMap<String,Vector<String>> goldTable = getGoldTable("../../BC2GN/data/testing.genelist");
		BioNERDocumentBuilder docBuilder;
		BC2GNOutput output;
		if(args.length==1 && args[0].equals("train"))
		{
			docBuilder = new BC2GNDocumentBuilder("../../BC2GN/data/trainingData");
			output = new BC2GNOutput("../../BC2GN/gn_train.eval");
		}
		else
		{
			docBuilder = new BC2GNDocumentBuilder("../../BC2GN/data/testingData");
			output = new BC2GNOutput();
		}
		output.init();
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		BioNERProcess[] pipeline = new BioNERProcess[3];
		
		pipeline[0] = new ProcessImpGoldStandardNER("../../BC2GN/data/testing.genelist");
		pipeline[1] = new ProcessImpCRFPP(GlobalConfig.CRF_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		//pipeline[2] = new ProcessImpCRFPP(GlobalConfig.CRF_INEXACT_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_INEXACT_CRF);
		pipeline[2] = new ProcessImpProteinBANNER();
		ProcessImpBC2GNGMFilter filter = new ProcessImpBC2GNGMFilter();
		ProcessImpGetCandidateID idFinder = new ProcessImpGetCandidateID(new CandidateFinder());
		ProcessImpBC2GNFilterAfterGetCandidate afterFilter = new ProcessImpBC2GNFilterAfterGetCandidate();
		ProcessImpFirstRankByListNet ranker = new ProcessImpFirstRankByListNet("../../BC2GN/TrainData_50.txt", new BC2GNFirstRankFeatureBuilder());
		ProcessImpGMClassificationBySVM classifier = new ProcessImpGMClassificationBySVM("../../BC2GN/GMClassificationTrainData.txt", new BC2GNGMClassificationFeatureBuilderFactory());
		HashMap<String, Vector<String>> filteredCorrectMap = new HashMap<String, Vector<String>>();
		HashMap<String, Vector<String>> filteredIncorrectMap = new HashMap<String, Vector<String>>();
		HashMap<String, Vector<String>> nonFilteredCorrectMap = new HashMap<String, Vector<String>>();
		HashMap<String, Vector<String>> nonFilteredIncorrectMap = new HashMap<String, Vector<String>>();
		
		int sizeCountArray = 10;
		int[] fp_countArray = new int[sizeCountArray];
		int[] tp_countArray = new int[sizeCountArray];
		for(int i=0; i<sizeCountArray; i++)
		{
			fp_countArray[i] = 0;
			tp_countArray[i] = 0;
		}
		double countDistance = 1.0 / sizeCountArray;
		for(int i=0; i<documents.length; i++)
		{
			//if(!documents[i].getID().equals("11696592")) continue;
			System.out.print("Processing #"+i+"....");
			BioNERDocument document = documents[i];
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			Vector<String>  filteredCorrectVector = new Vector<String>();
			Vector<String>  filteredIncorrectVector = new Vector<String>();
			Vector<String>  nonFilteredCorrectVector = new Vector<String>();
			Vector<String>  nonFilteredIncorrectVector = new Vector<String>();
			filteredCorrectMap.put(document.getID(), filteredCorrectVector);
			filteredIncorrectMap.put(document.getID(), filteredIncorrectVector);
			nonFilteredCorrectMap.put(document.getID(), nonFilteredCorrectVector);
			nonFilteredIncorrectMap.put(document.getID(), nonFilteredIncorrectVector);
			Vector<String> idVector = idTable.get(document.getID());
			for(BioNERSentence sentence : document.getAllSentence())
			{
				
				filter.mergeCoveredEntities(sentence, false);
				filter.extendGeneMentionBoundary(sentence);
				
				filter.addFullnameCombinedGM(sentence);
				
				filter.mergeCoveredEntities(sentence, false);
				filter.removePrefix(sentence);
				
				filter.spliteCombinedGM(sentence);
				filter.filterByPatterns(sentence);
			}
			idFinder.Process(document);
			afterFilter.Process(document);
			ranker.Process(document);
			Vector<String> filterList = FilterListBuilder.getFilterList(document);
			for(BioNERSentence sentence : document.getAllSentence())
			{
				BioNEREntity[] entityArray = sentence.getAllEntities();
				
				for(BioNEREntity entity : entityArray)
				{
					//if(entity.containLabel(GlobalConfig.ENTITY_LABEL_CRF)) continue;
					if(entity.containLabel("gold") && entity.getLabelVector().size()<=1) continue;
					//int correctIndex = BC2GNBuildNormalizationTrainData.haveCorrectID(entity.getCandidates(), 1000, idVector);
					int correctIndex = -1;
					double score = classifier.getGMConfidenceScore(entity);
					
					if(entity.containLabel("gold")) correctIndex=1;
					
					String gmText = entity.getText();
					/*boolean shouldFilter = false;
					for(String filterText : filterList)
					{
						if(gmText.equals(filterText) || filterText.contains(gmText))
						{
							shouldFilter = true;
							if(correctIndex>=0)
							{
								if(!filteredCorrectVector.contains(gmText))
									filteredCorrectVector.add(gmText);
							}
							else
							{
								if(!filteredIncorrectVector.contains(gmText))
									filteredIncorrectVector.add(gmText);
							}
							if(correctIndex>=0)
							{
								if(!nonFilteredCorrectVector.contains(gmText))
									nonFilteredCorrectVector.add(gmText);
							}
							else
							{
								if(!nonFilteredIncorrectVector.contains(gmText))
									nonFilteredIncorrectVector.add(gmText);
							}
							break;
						}
					}
					if(!shouldFilter)
					{
						if(correctIndex>=0)
						{
							if(!nonFilteredCorrectVector.contains(gmText))
								nonFilteredCorrectVector.add(gmText);
						}
						else
						{
							if(!nonFilteredIncorrectVector.contains(gmText))
								nonFilteredIncorrectVector.add(gmText);
						}
					}*/
					
					if(score>0.5)
					{
						if(correctIndex>=0)
						{
							if(!filteredCorrectVector.contains(gmText))
								filteredCorrectVector.add(gmText);
						}
						else
						{
							if(!filteredIncorrectVector.contains(gmText))
								filteredIncorrectVector.add(gmText);
						}
					}
					else
					{
						if(correctIndex>=0)
						{
							if(!nonFilteredCorrectVector.contains(gmText))
								nonFilteredCorrectVector.add(gmText);
						}
						else
						{
							if(!nonFilteredIncorrectVector.contains(gmText))
								nonFilteredIncorrectVector.add(gmText);
						}
					}
					
					int countIndex = (int)Math.floor(score / countDistance);
					if(countIndex>=sizeCountArray) countIndex = sizeCountArray-1;
					if(correctIndex>=0) 
					{
						tp_countArray[countIndex]++;
					}
					else 
					{
						fp_countArray[countIndex]++;
					}
					
				}
			}
			
			
			//documents[i]=null;
			System.out.println("Finished!");
		}
		
		finder.close();
		output.close();
		
		BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../BC2GN/test_GM_filter.txt"));
		
		int filteredCorrectNum = 0;
		fwriter.write("Filter GM Correct:");
		fwriter.newLine();
		for(String docID : filteredCorrectMap.keySet())
		{
			Vector<String> vector = filteredCorrectMap.get(docID);
			for(String gmText : vector)
			{
				fwriter.write(docID+":"+gmText);
				fwriter.newLine();
				filteredCorrectNum++;
			}
		}
		fwriter.newLine();
		
		int filteredIncorrectNum = 0;
		fwriter.write("Filter GM Incorrect:");
		fwriter.newLine();
		for(String docID : filteredIncorrectMap.keySet())
		{
			Vector<String> vector = filteredIncorrectMap.get(docID);
			for(String gmText : vector)
			{
				fwriter.write(docID+":"+gmText);
				fwriter.newLine();
				filteredIncorrectNum++;
			}
		}
		fwriter.newLine();
		
		int nonFilteredCorrectNum = 0;
		fwriter.write("Non filter GM Correct:");
		fwriter.newLine();
		for(String docID : nonFilteredCorrectMap.keySet())
		{
			Vector<String> vector = nonFilteredCorrectMap.get(docID);
			for(String gmText : vector)
			{
				fwriter.write(docID+":"+gmText);
				fwriter.newLine();
				nonFilteredCorrectNum++;
			}
		}
		fwriter.newLine();
		int nonFilteredIncorrectNum = 0;
		fwriter.write("Non filter GM Incorrect:");
		fwriter.newLine();
		for(String docID : nonFilteredIncorrectMap.keySet())
		{
			Vector<String> vector = nonFilteredIncorrectMap.get(docID);
			for(String gmText : vector)
			{
				fwriter.write(docID+":"+gmText);
				fwriter.newLine();
				nonFilteredIncorrectNum++;
			}
		}
		fwriter.newLine();
		
		fwriter.write("Filtered:");
		fwriter.newLine();
		double rate = (double)(filteredCorrectNum)/(double)(filteredCorrectNum+filteredIncorrectNum);
		fwriter.write("Correct:"+filteredCorrectNum+" "+rate);
		fwriter.newLine();
		rate = (double)(filteredIncorrectNum)/(double)(filteredCorrectNum+filteredIncorrectNum);
		fwriter.write("Incorrect:"+filteredIncorrectNum+" "+rate);
		fwriter.newLine();
		
		fwriter.write("Non filtered:");
		fwriter.newLine();
		rate = (double)(nonFilteredCorrectNum)/(double)(nonFilteredCorrectNum+nonFilteredIncorrectNum);
		fwriter.write("Correct:"+nonFilteredCorrectNum+" "+rate);
		fwriter.newLine();
		rate = (double)(nonFilteredIncorrectNum)/(double)(nonFilteredCorrectNum+nonFilteredIncorrectNum);
		fwriter.write("Incorrect:"+nonFilteredIncorrectNum+" "+rate);
		fwriter.newLine();
		
		fwriter.write("GM TP statistic:");
		fwriter.newLine();
		for(int i=0; i<sizeCountArray; i++)
		{
			double floor = i * countDistance;
			double ceil = (i+1) * countDistance;
			fwriter.write(Double.toString(floor)+"--"+Double.toString(ceil)+":"+tp_countArray[i]);
			fwriter.newLine();
		}
		fwriter.newLine();
		
		fwriter.write("GM FP statistic:");
		fwriter.newLine();
		for(int i=0; i<sizeCountArray; i++)
		{
			double floor = i * countDistance;
			double ceil = (i+1) * countDistance;
			fwriter.write(Double.toString(floor)+"--"+Double.toString(ceil)+":"+fp_countArray[i]);
			fwriter.newLine();
		}
		
		fwriter.close();
		
		
		fwriter = new BufferedWriter(new FileWriter("../../BC2GN/color_test_GM_filter.html"));
		
		for(int i=0; i<documents.length; i++)
		{
			String pmcid = documents[i].getID();
			BufferedReader freader = new BufferedReader(new FileReader("../../BC2GN/data/testingData/"+pmcid+".txt"));
			String titleText = freader.readLine();
			freader.readLine();
			String absText = freader.readLine();
			freader.close();
			
			Vector<String> gmVector = filteredCorrectMap.get(pmcid);
			if(gmVector!=null)
			{
				titleText = ColorTagger.getColorLabeledText(titleText, gmVector, "orange");
				absText = ColorTagger.getColorLabeledText(absText, gmVector, "orange");
			}
			
			gmVector = filteredIncorrectMap.get(pmcid);
			if(gmVector!=null)
			{
				titleText = ColorTagger.getColorLabeledText(titleText, gmVector, "green");
				absText = ColorTagger.getColorLabeledText(absText, gmVector, "green");
			}
			
			gmVector = nonFilteredCorrectMap.get(pmcid);
			if(gmVector!=null)
			{
				titleText = ColorTagger.getColorLabeledText(titleText, gmVector, "blue");
				absText = ColorTagger.getColorLabeledText(absText, gmVector, "blue");
			}
			
			gmVector = nonFilteredIncorrectMap.get(pmcid);
			if(gmVector!=null)
			{
				titleText = ColorTagger.getColorLabeledText(titleText, gmVector, "red");
				absText = ColorTagger.getColorLabeledText(absText, gmVector, "red");
			}
			
			
			fwriter.write("#"+(i+1)+" "+pmcid);
			fwriter.newLine();
			fwriter.write("<br>");
			fwriter.write(titleText);
			fwriter.newLine();
			fwriter.write("<br>");
			fwriter.write(absText);
			fwriter.write("<br>");
			
			Vector<String> goldVector = goldTable.get(pmcid);
			if(goldVector!=null)
			{
				fwriter.write("Gold Standard:<br>");
				for(String goldStr : goldVector)
				{
					fwriter.write(goldStr);
					fwriter.write("<br>");
					fwriter.newLine();
				}
			}
			fwriter.write("<br><br>");
			fwriter.newLine();
		}
		
		
		fwriter.close();
		
		long endtime = System.currentTimeMillis();
		long time = endtime - begintime;
		System.out.println("Time spent: "+time+" ms");
	}

	private static boolean isCorrectGM(BioNEREntity entity)
	{
		BioNERSentence sentence = entity.get_Sentence();
		for(BioNEREntity otherEntity : sentence.getAllEntities())
		{
			if(otherEntity.containLabel("gold"))
			{
				if(otherEntity.getTokenBeginIndex()>=entity.getTokenBeginIndex() && otherEntity.getTokenBeginIndex()<=entity.getTokenEndIndex())
					return true;
				if(otherEntity.getTokenEndIndex()>=entity.getTokenBeginIndex() && otherEntity.getTokenEndIndex()<=entity.getTokenEndIndex())
					return true;
			}
		}
		return false;
	}
	public static HashMap<String,Vector<String>> getGoldTable(String filename)
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
				
				entityVector.add(line);
				
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
