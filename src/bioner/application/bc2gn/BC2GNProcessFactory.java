package bioner.application.bc2gn;

import bioner.application.api.BioNERProcessFactory;
import bioner.global.GlobalConfig;
import bioner.normalization.ProcessImpAdjustByChromosome;
import bioner.normalization.ProcessImpFilterAfterGetCandidate;
import bioner.normalization.ProcessImpFilterGeneMention;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.gmclassification.ProcessImpGMClassificationByLogistic;
import bioner.normalization.gmclassification.ProcessImpGMClassificationByOther;
import bioner.normalization.gmclassification.ProcessImpGMClassificationBySVM;
import bioner.process.BioNERProcess;


import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinBANNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC2GNProcessFactory implements BioNERProcessFactory {

	private CandidateFinder m_finder;
	public BC2GNProcessFactory(CandidateFinder finder)
	{
		m_finder = finder;
	}
	@Override
	public BioNERProcess[] buildProcessPipeline() {
		// TODO Auto-generated method stub
		BioNERProcess[] pipeline = new BioNERProcess[7];
		pipeline[0] = new ProcessImpCRFPP(GlobalConfig.CRF_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		pipeline[1] = new ProcessImpCRFPP(GlobalConfig.CRF_INEXACT_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_INEXACT_CRF);
		//pipeline[1] = new ProcessImpProteinBANNER();
		//pipeline[0] = new ProcessImpGRMMLineCRF();
		//pipeline[1] = new ProcessImpProteinIndexNER();
		//pipeline[2] = new ProcessImpProteinABNER();
		//pipeline[0] = new ProcessImpGoldStandardNER("../../BC2GN/bc2GNtest.genelist");
		pipeline[2] = new ProcessImpBC2GNGMFilter();
		pipeline[3] = new ProcessImpGetCandidateID(m_finder);
		
		
		pipeline[4] = new ProcessImpBC2GNFilterAfterGetCandidate();
		pipeline[5] = new ProcessImpFirstRankByListNet("../../BC2GN/TrainData_50.txt", new BC2GNFirstRankFeatureBuilder());
		pipeline[6] = new ProcessImpGMClassificationBySVM("../../BC2GN/GMClassificationTrainData.txt", new BC2GNGMClassificationFeatureBuilderFactory());
		//pipeline[6] = new ProcessImpGMClassificationByOther("../../BC2GN/GMClassificationTrainData.txt", new BC2GNGMClassificationFeatureBuilderFactory());
		//pipeline[6] = new ProcessImpGMClassificationByLogistic("../../BC2GN/GMClassificationTrainData.txt", new BC2GNGMClassificationFeatureBuilderFactory());
		//pipeline[2] = new ProcessImpRerankByRankNet("../../BC2GN/TrainData_50.txt");
		//pipeline[3] = new ProcessImpAdjustByChromosome();
		/*BioNERProcess[] pipeline = new BioNERProcess[3];
		pipeline[0] = new ProcessImpPreprocess();
		//pipeline[1] = new ProcessImpEntityFilter();
		pipeline[1] = new ProcessImpSetLable();
		pipeline[2] = new ProcessImpGetEntityFromLabel();*/
		return pipeline;
	}

}
