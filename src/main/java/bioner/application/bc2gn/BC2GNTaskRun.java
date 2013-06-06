package bioner.application.bc2gn;

import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERApplication;
import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERDocumentOutput;
import bioner.application.api.BioNERProcessFactory;
import bioner.application.bc3gn.BC3GNGeneIDRerankFeatureBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.normalization.rerank.FilterBySpecies;
import bioner.normalization.rerank.GeneRerankByLogistic;
import bioner.normalization.rerank.GeneRerankBySVM;
import bioner.process.BioNERProcess;

public class BC2GNTaskRun {

	public static int top_num=1;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		long begintime = System.currentTimeMillis();
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
		BioNERProcessFactory processFactory = new BC2GNProcessFactory(finder);
		BioNERDocument[] documents = docBuilder.buildDocuments();
		BioNERProcess[] pipeline = processFactory.buildProcessPipeline();
		
		//GeneRerankByLogistic rerank = new GeneRerankByLogistic("../../BC2GN/RerankTrainData.txt", new BC2GNGeneIDRerankFeatureBuilder());
		GeneRerankBySVM rerank = new GeneRerankBySVM("../../BC2GN/RerankTrainData.txt", new BC2GNGeneIDRerankFeatureBuilder());
		for(int i=0; i<documents.length; i++)
		{
			//if(!documents[i].getID().equals("12878160")) continue;
			System.out.print("Processing #"+i+"....");
			BioNERDocument document = documents[i];
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			
			HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
			Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
			BuildGeneIDVectorMap.buildGeneIDVectorMap(document, geneIDMap, geneIDVector, top_num);
			//FilterBySpecies.filter(geneIDVector, document);
			
			BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
			for(int j=0; j<geneIDVector.size(); j++)
			{
				candidates[j] = geneIDVector.elementAt(j);
			}
			//FrequenceGeneRerank.rerank(candidates, geneIDMap);
			rerank.rerank(document, geneIDMap, candidates);
			
			FilterGeneIDArray.filterGeneIDArray(document, geneIDMap, candidates);
			
			output.outputGeneIDs(document, geneIDMap, candidates);
			documents[i]=null;
			System.out.println("Finished!");
		}
		
		finder.close();
		output.close();
		long endtime = System.currentTimeMillis();
		long time = endtime - begintime;
		System.out.println("Time spent: "+time+" ms");
	}

}
