package bioner.application.webtool;

import bioner.application.api.BioNERProcessFactory;
import bioner.application.bc2gn.ProcessImpGoldStandardNER;
import bioner.normalization.ProcessImpAdjustByChromosome;
import bioner.normalization.ProcessImpFilterAfterGeneIDRerank;
import bioner.normalization.ProcessImpFilterAfterGetCandidate;
import bioner.normalization.ProcessImpFilterAfterRank;
import bioner.normalization.ProcessImpFilterGeneMention;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.ProcessImpRerankGeneIDByListNet;
import bioner.normalization.ProcessImpRerankGeneIDByLogistic;
import bioner.normalization.ProcessImpSecondRankByListNet;
import bioner.normalization.ProcessImpSecondRankByRankNet;
import bioner.normalization.candidate.CandidateFinder;
import bioner.process.BioNERProcess;


import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.crf.ProcessImpGRMMLineCRF;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class BC3GNProcessFactory implements BioNERProcessFactory {

	private CandidateFinder m_finder;
	private String m_trainingDataFilename;
	private String m_rerankTrainFilename;
	private String m_secondRankTrainFilename;
	public BC3GNProcessFactory(CandidateFinder finder, String trainingDataFilename, String secondRankTrainFilename,String rerankTrainFilename)
	{
		m_finder = finder;
		m_trainingDataFilename = trainingDataFilename;
		m_rerankTrainFilename = rerankTrainFilename;
		m_secondRankTrainFilename = secondRankTrainFilename;
	}
	@Override
	public BioNERProcess[] buildProcessPipeline() {
		// TODO Auto-generated method stub
		BioNERProcess[] pipeline = new BioNERProcess[7];
		pipeline[0] = new ProcessImpCRFPP();
		//pipeline[0] = new ProcessImpGRMMLineCRF();
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		pipeline[3] = new ProcessImpFilterGeneMention();
		pipeline[4] = new ProcessImpGetCandidateID(m_finder);
		pipeline[5] = new ProcessImpFilterAfterGetCandidate();
		pipeline[6] = new ProcessImpFirstRankByListNet(m_trainingDataFilename, new BC3GNFirstRankFeatureBuilder());
		//pipeline[6] = new ProcessImpRerankBySVM(m_trainingDataFilename);
		//pipeline[6] = new ProcessImpSecondRankByListNet(m_secondRankTrainFilename);
		//pipeline[6] = new ProcessImpSecondRankByRankNet(m_secondRankTrainFilename);
		//pipeline[7] = new ProcessImpFilterAfterRank();
		//pipeline[7] = new ProcessImpRerankGeneIDByListNet(m_rerankTrainFilename);
		//pipeline[8] = new ProcessImpRerankGeneIDByLogistic(m_rerankTrainFilename);
		//pipeline[9] = new ProcessImpFilterAfterGeneIDRerank();
		/*pipeline = new BioNERProcess[6];
		pipeline[0] = new ProcessImpGoldStandardNER("../../BC3GN/TrainingSet1.gm.txt");
		pipeline[1] = new ProcessImpFilterGeneMention();
		pipeline[2] = new ProcessImpGetCandidateID(m_finder);
		pipeline[3] = new ProcessImpRerankByListNet(m_trainingDataFilename);
		pipeline[4] = new ProcessImpFilterAfterRank();
		pipeline[5] = new ProcessImpRerankGeneIDByListNet(m_rerankTrainFilename);*/
		return pipeline;
	}

}
