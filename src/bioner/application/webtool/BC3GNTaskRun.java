package bioner.application.webtool;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERApplication;
import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERDocumentOutput;
import bioner.application.api.BioNERProcessFactory;
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
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		String dataDir = "../../BC3GN/xmls/";
		String outputFilename = "../../BC3GN/gn.eval";
		String trainingDataFilename = "../../BC3GN/TrainData_50.txt";
		String rerankTrainFilename = "../../BC3GN/RerankTrainData.txt";
		String filterFilename = GlobalConfig.ENTITYFILTER_TABULIST_PATH;
		if(args.length==5)
		{
			dataDir = args[0];
			trainingDataFilename = args[1];
			filterFilename = args[2];
			rerankTrainFilename = args[3];
			outputFilename = args[4];
			
		}
		
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(dataDir);
		File[] files = (new File(dataDir)).listFiles();
		BC3GNOutput output = new BC3GNOutput(outputFilename);
		output.init();
		CandidateFinder finder = new CandidateFinder();
		BioNERProcessFactory processFactory = new BC3GNProcessFactory(finder,trainingDataFilename, 
            filterFilename, rerankTrainFilename);
		
		BioNERProcess[] pipeline = processFactory.buildProcessPipeline();
		GeneRerankByLogistic rerank = new GeneRerankByLogistic(rerankTrainFilename, new BC3GNGeneIDRerankFeatureBuilder());
		//GeneRerankBySVM rerank = new GeneRerankBySVM(rerankTrainFilename);
		for(int i=0; i<files.length; i++)
		{
			long beginTime = System.currentTimeMillis();
			BioNERDocument document = docBuilder.getOneDocument(files[i]);
			System.out.print("Processing #"+i+" "+document.getID()+"....");
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
			Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
			BuildGeneIDVectorMap.buildGeneIDVectorMap(document, geneIDMap, geneIDVector, rank);
			FilterBySpecies.filter(geneIDVector, document, geneIDMap);
			
			BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
			for(int j=0; j<geneIDVector.size(); j++)
			{
				candidates[j] = geneIDVector.elementAt(j);
			}
			//FrequenceGeneRerank.rerank(candidates, geneIDMap);
			rerank.rerank(document, geneIDMap, candidates);
			
			
			
			output.outputByGeneID(candidates, document);
			document=null;
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			System.out.println("Finished! "+time+" ms");
		}
		
		finder.close();
		output.close();
	}

}
