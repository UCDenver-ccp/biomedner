package temp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.BC2GNDocumentBuilder;
import bioner.application.bc2gn.BC2GNProcessFactory;
import bioner.application.bc2gn.ProcessImpGoldStandardNER;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationFeatureBuilder;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;

public class BC2GNAnalysisRankData {

	public static void writerDataFile(String dataDir, String genelistFilename, String outputFilename, int maxNum) throws IOException
	{
		GlobalConfig.ReadConfigFile();
		
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		BC2GNProcessFactory pipelineFactory = new BC2GNProcessFactory(finder);
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
		
		
		int rank = maxNum;
		for(int i=0; i<documents.length ; i++)
		{
			System.out.print("Processing #"+i+"....");
			BioNERDocument document = documents[i];
			//if(!document.getID().equals("9462743")) continue;
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
					
					BioNERCandidate[] candidates = entity.getCandidates();
					int correctIndex = haveCorrectID(candidates, rank, idVector);
					
					
					
					if(correctIndex > 0)
					{
						entityNum++;
						
						fwriter.write(sentence.getSentenceText());
						fwriter.newLine();
						
						fwriter.write(entity.getText());
						fwriter.newLine();
						
						String correctID = candidates[correctIndex].getRecord().getID();
						fwriter.write("%"+correctID+"_"+document.getID()+"_"+entityNum+" 1");
						fwriter.newLine();
						for(int j=0; j<rank && j<candidates.length; j++)
						{
							String recordID = candidates[j].getRecord().getID();
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
			
			
			documents[i]=null;
			System.out.println("Finished!");
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
		
		
		String genelistFilename = "../../BC2GN/data/testing.genelist";
		String dataDir = "../../BC2GN/data/testingData";
		
		String outputFilename = "../../BC2GN/TestData_analysis_50.txt";
		writerDataFile(dataDir, genelistFilename, outputFilename, 50);
		
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
				if(parts.length<3) continue;
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
