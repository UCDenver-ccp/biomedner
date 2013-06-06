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

import bioner.application.api.BioNERApplication;
import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERDocumentOutput;
import bioner.application.api.BioNERProcessFactory;
import bioner.application.bc3gn.FilterGeneIDArray;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.normalization.rerank.FilterBySpecies;
import bioner.normalization.rerank.FrequenceGeneRerank;
import bioner.normalization.rerank.GeneRerankByLogistic;
import bioner.normalization.rerank.GeneRerankBySVM;
import bioner.process.BioNERProcess;

public class BC3GNTaskRun {

	public static int rank = 10;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		String dataDir = "../../BC3GN/xmls/";
		String outputFilename = "../../BC3GN/gn.eval";
		String trainingDataFilename = "../../BC3GN/TrainData_50.txt";
		String rerankTrainFilename = "../../BC3GN/RerankTrainData.txt";
		String secondRankTrainFilename = "../../BC3GN/secondRankTrainData.txt";
		if(args.length==5)
		{
			dataDir = args[0];
			trainingDataFilename = args[1];
			secondRankTrainFilename = args[2];
			rerankTrainFilename = args[3];
			outputFilename = args[4];
			
		}
		GlobalConfig.BC3GN_DATADIR = dataDir;
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(dataDir);
		File[] files = (new File(dataDir)).listFiles();
		Vector<String> finishedDocIDVector = readFinishedResultFile(outputFilename);
		BC3GNOutput output = new BC3GNOutput(outputFilename);
		output.init();
		CandidateFinder finder = new CandidateFinder();
		BioNERProcessFactory processFactory = new BC3GNProcessFactory(finder,trainingDataFilename, secondRankTrainFilename, rerankTrainFilename);
		
		BioNERProcess[] pipeline = processFactory.buildProcessPipeline();
		//GeneRerankByLogistic rerank = new GeneRerankByLogistic(rerankTrainFilename, new BC3GNGeneIDRerankFeatureBuilder());
		GeneRerankBySVM rerank = new GeneRerankBySVM(rerankTrainFilename, new BC3GNGeneIDRerankFeatureBuilder());
		
		for(int i=0; i<files.length; i++)
		{
			long beginTime = System.currentTimeMillis();
			BioNERDocument document = docBuilder.getOneDocument(files[i]);
			//if(!document.getID().equals("2883592")) continue;
			System.out.print("Processing #"+i+" "+document.getID()+"....");
			if(finishedDocIDVector.contains(document.getID()))
			{
				System.out.println("already finished!");
				continue;
			}
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
			Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
			BuildGeneIDVectorMap.buildGeneIDVectorMap(document, geneIDMap, geneIDVector, rank);
			FilterBySpecies.filter(geneIDVector, document,geneIDMap);
			
			BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
			for(int j=0; j<geneIDVector.size(); j++)
			{
				candidates[j] = geneIDVector.elementAt(j);
			}
			//FrequenceGeneRerank.rerank(candidates, geneIDMap);
			rerank.rerank(document, geneIDMap, candidates);
			
			
			FilterGeneIDArray.filterGeneIDArray(document, geneIDMap, candidates);
			
			output.outputByGeneID(candidates, document);
			output.outputDetail(candidates, document, geneIDMap);
			document=null;
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			System.out.println("Finished! "+time+" ms");
		}
		
		finder.close();
		output.close();
	}
	
	public static Vector<String> readFinishedResultFile(String filename) throws IOException
	{
		File file = new File(filename);
		//if the output file dose not exist, return a empty vector.
		if(!file.exists()) return new Vector<String>();
		BufferedReader freader = new BufferedReader(new FileReader(filename));
		String line;
		Vector<String> lineVector = new Vector<String>();
		Vector<String> docIDVector = new Vector<String>();
		while((line=freader.readLine())!=null)
		{
			String[] parts = line.split("\\s+");
			if(parts.length<1) continue;
			lineVector.add(line);
			if(!docIDVector.contains(parts[0]))
				docIDVector.add(parts[0]);
		}
		freader.close();
		//Remove the last doc ID, as it maybe not finished.
		if(!docIDVector.isEmpty()) docIDVector.remove(docIDVector.size()-1);
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(filename));
		int size = lineVector.size();
		for(int i=0; i<size; i++)
		{
			line = lineVector.elementAt(i);
			String[] parts = line.split("\\s+");
			if(parts.length<1) continue;
			if(docIDVector.contains(parts[0]))
			{
				fwriter.write(line);
				fwriter.newLine();
			}
		}
		fwriter.close();
		return docIDVector;
	}
	

}
