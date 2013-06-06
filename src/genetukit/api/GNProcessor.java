package genetukit.api;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

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
import bioner.process.common.ProcessImpDebug;
/**
 * This is the processor for the gn task. Please first run the open() to init the processor, then run process() for one single document. Don't forget to close() when your task is done.
 * @author Jingchen Liu
 *
 */
public class GNProcessor {
	public static int rank = 1;
	GeneRerankByLogistic rerank = null;
	CandidateFinder finder = null;
	BC3GNDataFileReader docBuilder;
    Collection<BioNERProcess> pipeline = new ArrayList<BioNERProcess>();


	public enum FileType {
		NXML, PLAIN
	}

	String trainingDataFilename;
	String rerankTrainFilename;
    String modelFilename;
    String filterFilename;
    
    public GNProcessor() {
	    trainingDataFilename = GlobalConfig.ROOT_DIR+"train/TrainData_1.txt";
	    rerankTrainFilename = GlobalConfig.ROOT_DIR+"train/RerankTrainData_1.txt";
        modelFilename = GlobalConfig.CRF_MODEL_FILEPATH;
        filterFilename = GlobalConfig.ENTITYFILTER_TABULIST_PATH; 
    }
        
    public GNProcessor(String trainingDataFilename, String rerankTrainFilename, 
        String modelFilename, String filterFilename ){

	    this.trainingDataFilename = trainingDataFilename;
	    this.rerankTrainFilename = rerankTrainFilename;
        this.modelFilename = modelFilename;
        this.filterFilename = filterFilename;
    }

	public void open(boolean useBanner) {
		GlobalConfig.ReadConfigFile();
		docBuilder = new BC3GNDataFileReader();
		finder = new CandidateFinder();
	
		if (useBanner) {
			pipeline.add(new ProcessImpProteinBANNER());
		}
		else {
			pipeline.add(new ProcessImpCRFPP(modelFilename, GlobalConfig.ENTITY_LABEL_CRF));
        }


		//pipeline[0] = new ProcessImpGRMMLineCRF();
		

        boolean _debug_ = false;
		pipeline.add(new ProcessImpProteinIndexNER());
        if (_debug_) pipeline.add(new ProcessImpDebug("after index NER"));	

        pipeline.add(new ProcessImpProteinABNER());
        if (_debug_) pipeline.add(new ProcessImpDebug("after ABNER"));	

		//pipeline.add(new ProcessImpProteinBANNER());
        //if (_debug_) pipeline.add(new ProcessImpDebug("after BANNER"));	

		pipeline.add(new ProcessImpFilterGeneMention(filterFilename));
        if (_debug_) pipeline.add(new ProcessImpDebug("after filter gene mention"));	

		pipeline.add(new ProcessImpGetCandidateID(finder));
        if (_debug_) pipeline.add(new ProcessImpDebug("after get candidate ID"));	

		pipeline.add(new ProcessImpFilterAfterGetCandidate());
        if (_debug_) pipeline.add(new ProcessImpDebug("after filter after get canddiate"));	

		pipeline.add(new ProcessImpFirstRankByListNet(trainingDataFilename, new BC3GNFirstRankFeatureBuilder()));
        if (_debug_) pipeline.add(new ProcessImpDebug("after RankByListNext"));	

       
        {
            // check to see if this file exists since the error messages from in there aren't so good
            File testFile = new File(rerankTrainFilename);
            if (!testFile.canRead()) {
                System.err.println("GNProcessor.open() can't read:" + rerankTrainFilename);
            }
        } 
		rerank = new GeneRerankByLogistic(rerankTrainFilename, new BC3GNGeneIDRerankFeatureBuilder());
	}

	public GNResultItem[] process(String filename, FileType type) {
		BioNERDocument document = null;
		if (type == FileType.NXML) {
			document = docBuilder.getOneDocument(new File(filename));
		}
		else if (type == FileType.PLAIN) {
			document = PlainTextDocumentBuilder.getOneDocument(filename);
		}

		return process(document);
	}

	public GNResultItem[] process(BioNERDocument document) {
		for (BioNERProcess process : pipeline) {
			process.Process(document);
		}
		
		
		HashMap<String, Vector<BioNEREntity>> geneIDMap = new HashMap<String, Vector<BioNEREntity>>();
		Vector<BioNERCandidate> geneIDVector = new Vector<BioNERCandidate>();
		BuildGeneIDVectorMap.buildGeneIDVectorMap(document, geneIDMap, geneIDVector, rank);
		//FilterBySpecies.filter(geneIDVector, document);
		
		BioNERCandidate[] candidates = new BioNERCandidate[geneIDVector.size()];
		for (int j=0; j<geneIDVector.size(); j++) {
			candidates[j] = geneIDVector.elementAt(j);
		}
		//FrequenceGeneRerank.rerank(candidates, geneIDMap);
		rerank.rerank(document, geneIDMap, candidates);
		GNResultItem[] items = new GNResultItem[candidates.length];
		for (int i=0; i<items.length; i++) {
			items[i] = new GNResultItem();
			items[i].setID(candidates[i].getRecord().getID());
			items[i].setSpeciesID(candidates[i].getRecord().getSpeciesID());
			items[i].setScore(candidates[i].getScore());

			Vector<BioNEREntity> gmVector = geneIDMap.get(candidates[i].getRecord().getID());
			for (BioNEREntity gmEntity : gmVector) {
				items[i].addGeneMention(gmEntity.getText());
                items[i].addGNSpan(new GNSpan(
                    gmEntity.getText(), 
                    //gmEntity.get_Sentence().getBegin() + gmEntity.get_Begin(), 
                    //gmEntity.get_Sentence().getBegin() + gmEntity.get_End(),
                    gmEntity.get_Begin(), 
                    gmEntity.get_End(),
                    gmEntity.get_Sentence().getSentenceText(),
                    gmEntity.get_Sentence() ) );
			}
		}
		return items;
	}

	public void close() {
		finder.close();
	}
}


