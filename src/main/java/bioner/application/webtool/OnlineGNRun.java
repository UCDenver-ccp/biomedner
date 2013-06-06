package bioner.application.webtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Vector;



import bioner.application.bc3gn.BC3GNFirstRankFeatureBuilder;
import bioner.application.bc3gn.BC3GNGeneIDRerankFeatureBuilder;
import bioner.application.webtool.SpeciesDatabaseReader;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.ProcessImpFilterAfterGetCandidate;
import bioner.normalization.ProcessImpFilterGeneMention;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.normalization.rerank.GeneRerankByLogistic;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class OnlineGNRun {
	public static int rank = 1;
	/**
	 * @param args
	 */
	private static SpeciesDatabaseReader speciesReader = new SpeciesDatabaseReader();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try {
			addDir(GlobalConfig.ROOT_DIR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		StringBuffer textBuffer = new StringBuffer();
		BufferedReader freader = new BufferedReader(new InputStreamReader(System.in));
		
		String line;
		while((line=freader.readLine())!=null)
		{
			textBuffer.append(line);
			
			textBuffer.append("\r\n");
		}
		freader.close();
		GlobalConfig.ReadConfigFile();
		
		String trainingDataFilename = GlobalConfig.ROOT_DIR+"train/TrainData_1.txt";
		String rerankTrainFilename = GlobalConfig.ROOT_DIR+"train/RerankTrainData_1.txt";
		
		StringTextDocumentBuilder docBuilder = new StringTextDocumentBuilder();
		
		
		CandidateFinder finder = new CandidateFinder();
		BioNERDocument document;
		document = StringTextDocumentBuilder.getOneDocument(textBuffer.toString());
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
		String jsonStr;
		jsonStr = sb.toString();
			
		finder.close();
		speciesReader.close();
		System.out.println(jsonStr);
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
	
	public static void addDir(String s) throws IOException {
		try {
		Field field = ClassLoader.class.getDeclaredField("usr_paths");
		field.setAccessible(true);
		String[] paths = (String[])field.get(null);
		for (int i = 0; i < paths.length; i++) {
		if (s.equals(paths[i])) {
		return;
		}
		}
		String[] tmp = new String[paths.length+1];
		System.arraycopy(paths,0,tmp,0,paths.length);
		tmp[paths.length] = s;
		field.set(null,tmp);
		} catch (IllegalAccessException e) {
		throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
		throw new IOException("Failed to get field handle to set library path");
		}
		}  
}
