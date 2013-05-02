package genetukit.api;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.webtool.BC3GNDataFileReader;
import bioner.application.webtool.BC3GNFirstRankFeatureBuilder;
import bioner.application.webtool.BC3GNGeneIDRerankFeatureBuilder;
import bioner.application.webtool.PlainTextDocumentBuilder;
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
import bioner.process.proteinner.ProcessImpProteinBANNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;
/**
 * This is the processor for the gn task. Please first run the open() to init the processor, then run process() for one single document. Don't forget to close() when your task is done.
 * @author Jingchen Liu
 *
 */
public class GNProcessor {
	public static int rank = 1;
	BioNERProcess[] pipeline = null;
	GeneRerankByLogistic rerank = null;
	CandidateFinder finder = null;
	BC3GNDataFileReader docBuilder;
	public enum FileType{
		NXML, PLAIN
	}
	public void open(boolean useBanner)
	{
		GlobalConfig.ReadConfigFile();
		String trainingDataFilename = GlobalConfig.ROOT_DIR+"train/TrainData_1.txt";
		String rerankTrainFilename = GlobalConfig.ROOT_DIR+"train/RerankTrainData_1.txt";
		docBuilder = new BC3GNDataFileReader();
		finder = new CandidateFinder();
		pipeline = new BioNERProcess[7];
		if(useBanner)
		{
			pipeline[0] = new ProcessImpProteinBANNER();
		}
		else
			pipeline[0] = new ProcessImpCRFPP(GlobalConfig.CRF_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		//pipeline[0] = new ProcessImpGRMMLineCRF();
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		pipeline[3] = new ProcessImpFilterGeneMention();
		pipeline[4] = new ProcessImpGetCandidateID(finder);
		pipeline[5] = new ProcessImpFilterAfterGetCandidate();
		pipeline[6] = new ProcessImpFirstRankByListNet(trainingDataFilename, new BC3GNFirstRankFeatureBuilder());
		rerank = new GeneRerankByLogistic(rerankTrainFilename, new BC3GNGeneIDRerankFeatureBuilder());
	}
	public GNResultItem[] process(String filename, FileType type)
	{
		BioNERDocument document = null;
		if(type == FileType.NXML)
		{
			document = docBuilder.getOneDocument(new File(filename));
		}
		else if(type == FileType.PLAIN)
		{
			document = PlainTextDocumentBuilder.getOneDocument(filename);
		}

		return process(document);
	}

	public GNResultItem[] process(BioNERDocument document) {
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
		GNResultItem[] items = new GNResultItem[candidates.length];
		for(int i=0; i<items.length; i++)
		{
			items[i] = new GNResultItem();
			items[i].setID(candidates[i].getRecord().getID());
			items[i].setSpeciesID(candidates[i].getRecord().getSpeciesID());
			items[i].setScore(candidates[i].getScore());
			Vector<BioNEREntity> gmVector = geneIDMap.get(candidates[i].getRecord().getID());
			for(BioNEREntity gmEntity : gmVector)
			{
				items[i].addGeneMention(gmEntity.getText());
			}
		}
		return items;
	}
	public void close()
	{
		finder.close();
	}
}


