package bioner.application.webtool;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERProcessFactory;
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
import bioner.normalization.data.database.DatabaseConfig;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.normalization.rerank.FilterBySpecies;
import bioner.normalization.rerank.GeneRerankByLogistic;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class WebAnnotationTool {
	public static int rank = 1;
	/**
	 * @param args
	 */
	private static SpeciesDatabaseReader speciesReader = new SpeciesDatabaseReader();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length!=1)
		{
			System.err.println("Args error!");
			System.exit(1);
		}
		String pmcid = args[0];
		DatabaseJSONTableReader databaseReader = new DatabaseJSONTableReader();
		databaseReader.connect();
		String jsonStr = databaseReader.serachRecord(pmcid);
		if(jsonStr!=null)
		{
			System.out.println(jsonStr);
			databaseReader.close();
			System.exit(0);
		}
		
		
		long beginTime = System.currentTimeMillis();
		
		PrintStream consoleStream = System.out;
		PrintStream tempStream = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int arg0) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		System.setOut(tempStream);
		
		
		GlobalConfig.ReadConfigFile();
		
		String trainingDataFilename = GlobalConfig.ROOT_DIR+"train/TrainData_1.txt";
		String rerankTrainFilename = GlobalConfig.ROOT_DIR+"train/RerankTrainData_1.txt";
		
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader();
		
		
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument document;
		document = docBuilder.getOneDocument(new File(DatabaseConfig.DATAFILE_DIR+pmcid+".nxml"));
		BioNERProcess[] pipeline = new BioNERProcess[7];
		pipeline[0] = new ProcessImpCRFPP();
		//pipeline[0] = new ProcessImpGRMMLineCRF();
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		pipeline[3] = new ProcessImpFilterGeneMention();
		pipeline[4] = new ProcessImpGetCandidateID(finder);
		pipeline[5] = new ProcessImpFilterAfterGetCandidate();
		pipeline[6] = new ProcessImpFirstRankByListNet(trainingDataFilename, new BC3GNFirstRankFeatureBuilder());
		GeneRerankByLogistic rerank = new GeneRerankByLogistic(rerankTrainFilename, new BC3GNGeneIDRerankFeatureBuilder());
		//GeneRerankBySVM rerank = new GeneRerankBySVM(rerankTrainFilename);
		
		
		
			
		for(int j=0; j<pipeline.length; j++)
		{
			pipeline[j].Process(document);
			//System.err.println("step "+j+" GM="+getGMNumber(document));
		}
		
		HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
		Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
		BuildGeneIDVectorMap.buildGeneIDVectorMap(document, geneIDMap, geneIDVector, rank);
		//FilterBySpecies.filter(geneIDVector, document);
		
		BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
		for(int j=0; j<geneIDVector.size(); j++)
		{
			candidates[j] = geneIDVector.elementAt(j);
		}
		//FrequenceGeneRerank.rerank(candidates, geneIDMap);
		rerank.rerank(document, geneIDMap, candidates);
		
		
		
		
		
		document=null;
		long endTime = System.currentTimeMillis();
		long time = endTime - beginTime;
		
		
		System.setOut(consoleStream);
		speciesReader.connect();
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i=0; i<candidates.length; i++)
		{
			if(i!=0) 
				sb.append(",");
			sb.append(getGeneIDStr(candidates[i], geneIDMap));
		}
		sb.append("]");
		jsonStr = sb.toString();
		System.out.println(jsonStr);
			
		finder.close();
		databaseReader.insertReader(pmcid, jsonStr);
		databaseReader.close();
		speciesReader.close();
		System.err.println("Finished! "+time+" ms");
	}
	
	public static String getGeneIDStr(BioNERCandidate candidate, HashMap<String, Vector<BioNEREntity>> geneIDMap)
	{
		StringBuffer sb = new StringBuffer("{\"annotation_type_cd\":\"gene\"");
		sb.append(",\"annotation_id\":\""+candidate.getRecordID()+"\"");
		
		sb.append(",\"annotation_data\":");
		Vector<BioNEREntity> entityVector = geneIDMap.get(candidate.getRecordID());
		Vector<String> gmVector = new Vector<String>();
		for(BioNEREntity entity : entityVector)
		{
			String gmText = entity.getText();
			if(!gmVector.contains(gmText))
				gmVector.add(gmText);
		}
		sb.append("\"");
		for(int i=0; i<gmVector.size(); i++)
		{
			if(i!=0) sb.append("|");
			String gmStr = gmVector.elementAt(i);
			gmStr = gmStr.replaceAll("\"", "\\\"");
			sb.append(gmVector.elementAt(i));
		}
		sb.append("\"");
		sb.append(",\"annotation_text\":\""+candidate.getRecord().getSymbol()+"\"");
		
		String speciesID = candidate.getRecord().getSpeciesID();
		String speciesName = speciesReader.getScientificName(speciesID);
		
		sb.append(",\"species_id\":\""+speciesID+"\"");
		sb.append(",\"species_name\":\""+speciesName+"\"");
		
		sb.append(",\"score\":\""+candidate.getScore()+"\"");
		sb.append("}");
		return sb.toString();
	}
}
