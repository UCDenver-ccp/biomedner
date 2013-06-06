package bioner.application.webtool;

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
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC3GNBuildNormalizationTrainData {

	public static void writerDataFile(String dataDir, String genelistFilename, String outputFilename, 
            int maxNum, String modelFilepath, String filterFilepath) 
    throws IOException {
		GlobalConfig.ReadConfigFile();
		
		HashMap<String, Vector<String>> idTable = getGeneIDTable(genelistFilename);

		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(dataDir);
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		BioNERProcess[] pipeline = new BioNERProcess[6];
		//pipeline[0] = new ProcessImpGoldStandardNER(genelistFilename);
		//pipeline[0] = new ProcessImpCRFPP();
        pipeline[0] = new ProcessImpCRFPP(modelFilepath, GlobalConfig.ENTITY_LABEL_CRF);

		//pipeline[0] = new ProcessImpGRMMLineCRF();
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		
		
		//pipeline[0] = new ProcessImpGoldStandardNER("../../BC3GN/TrainingSet1.gm.txt");
		pipeline[3] = new ProcessImpFilterGeneMention(filterFilepath);
		pipeline[4] = new ProcessImpGetCandidateID(finder);
		pipeline[5] = new ProcessImpFilterAfterGetCandidate();
		
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
		Vector<String> instanceStrVector = new Vector<String>();
		for(int i=0; i<documents.length ; i++)
		{
			//if(i!=9) continue;
			//if(!documents[i].getID().equals("2730050")) continue;
			long beginTime = System.currentTimeMillis();
			System.err.print("Build first rank train data. Processing #"+i+" "+documents[i].getID()+"....\n");
			BioNERDocument document = documents[i];
			for (int j=0; j<pipeline.length; j++) {
				pipeline[j].Process(document);
			}
			
			int sentence_num = 0;
			int entityNum = 0;
			Vector<String> idVector = idTable.get(document.getID());
			if (idVector==null) {
                System.err.println("skipping this doc. because it's idvector  (gold standard list of genes) is null. ID is :\"" + document.getID() + "\"");
                 continue;
            }
            
			for (BioNERSentence sentence : document.getAllSentence()) {
				sentence_num++;
                if (sentence.getAllEntities().length > 0) {
                    System.out.println("\nconsidering sentence  #" + sentence_num + " entities  " + sentence.getAllEntities().length);
                }

                // DEBUG
                if (false)  {
                boolean sentenceHasCandidates=false;
				for (BioNEREntity entity : sentence.getAllEntities()) {
					StringBuffer sb = new StringBuffer();
					BioNERCandidate[] candidates = entity.getCandidates();
                    for (BioNERCandidate cand : candidates) {
                        System.err.print("   candidate score:" + cand.getScore() 
                                + " species: " + cand.getRecord().getSpeciesID()
                                + " symbol: "  + cand.getRecord().getSymbol()
                        );
                        //System.out.print(" synonyms:");
                        //for (String syn : cand.getRecord().getSynonyms()) {
                        //    System.out.print(syn + ", ");
                        //}
                        System.out.println("");
                        sentenceHasCandidates=true;
                    }
                }
                }
    
				//fwriter.write(sentence.getSentenceText());
				//fwriter.newLine();
				for (BioNEREntity entity : sentence.getAllEntities()) {
					StringBuffer sb = new StringBuffer();
					BioNERCandidate[] candidates = entity.getCandidates();

					int correctIndex = haveCorrectID(candidates, rank, idVector);
					
					if (correctIndex >= 0) {
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
							if (!lineVector.contains(line)) lineVector.add(line);
							//fwriter.write("|"+candidates[j].getRecord().toString());
							
							
						}//candidate


						for (String line : lineVector) {
							sb.append(line);
							sb.append("\n");
						}


						String instanceStr = sb.toString();
						if (!instanceStrVector.contains(instanceStr)) {
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
		String dataDir = "../../BC3GN/xmls/";
		String genelistFilename = "../../BC3GN/data/TrainingSet2.txt";
        String modelFilepath = "";
		String outputFilename = "../../BC3GN/TrainData_10.txt";
        String filterFilepath = GlobalConfig.ENTITYFILTER_TABULIST_PATH;
		//writerDataFile(dataDir, genelistFilename, outputFilename, 10);
		outputFilename = "../../BC3GN/TrainData_50.txt";
		
		if (args.length==5) {
			dataDir = args[0];
			genelistFilename = args[1];
			outputFilename = args[2];
            modelFilepath = args[3];
            filterFilepath = args[4];
		}
        else {
            System.out.println("need 4 arguments:");
            System.out.println("usage: <java...> dataDir genelist output model ");
            System.exit(-1);
        }
		
		writerDataFile(dataDir, genelistFilename, outputFilename, 50, modelFilepath, filterFilepath);
	}

    /**
     * checks to see if the idVector contains at least one candidate's record ID
     */
	public static int haveCorrectID(BioNERCandidate[] candidates, int rank, Vector<String> idVector) {
        // TODO use SETS!!
        StringBuilder sbVector = new StringBuilder();
        for (String s : idVector) {
            sbVector.append(s + ", ");
        }


        StringBuilder sbCandidates = new StringBuilder();
		for (int i=0; i<rank && i<candidates.length; i++) {
			String id= candidates[i].getRecord().getID();
            sbCandidates.append(id + ", ");
			if (idVector.contains(id)) {
                System.out.println("accepting correct id:" + sbCandidates + "\n    <-->" + idVector);
                return i;
            }
		}
        //System.out.println("rejecting incorrect id:" + sbCandidates + "\n    <-->" + idVector);
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
				if (parts.length > 1) {
				    String docID = parts[0];
				    Vector<String> entityVector = table.get(docID);
				    if (entityVector==null) {
					    entityVector = new Vector<String>();
					    table.put(docID, entityVector);
				    }
				
				    entityVector.add(parts[1]);
                }
				
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
