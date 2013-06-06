package bioner.application.webtool;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.GeneIDRerankFeatureBuilder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;
import bioner.normalization.rerank.feature.ContainKeywordFeatureBuilder;
import bioner.normalization.rerank.feature.GMCountFeatureBuilder;
import bioner.normalization.rerank.feature.GMCountRateFeatureBuilder;
import bioner.normalization.rerank.feature.GMHasLabelFeatureBuilder;
import bioner.normalization.rerank.feature.HighestRankFeatureBuilder;
import bioner.normalization.rerank.feature.IsSpeciesInTitleFeatureBuilder;
import bioner.normalization.rerank.feature.MaxFeatureValueFeatureBuilder;
import bioner.normalization.rerank.feature.MinFeatureValueFeatureBuilder;
import bioner.normalization.rerank.feature.MinGMTokenNumberFeatureBuilder;

public class BC3GNGeneIDRerankFeatureBuilder implements
		GeneIDRerankFeatureBuilder {

	
	/**
	 * Get a pipeline made up by a sequence of feature builders.
	 * @return NormalizationPairFeatureBuilder[]
	 */
	public static RerankFeatureBuilder[] getFeatureBuilderPipeline()
	{
		RerankFeatureBuilder[] pipeline = new RerankFeatureBuilder[20];
		pipeline[0] = new MaxFeatureValueFeatureBuilder(0);
		pipeline[1] = new MaxFeatureValueFeatureBuilder(1);
		pipeline[2] = new MaxFeatureValueFeatureBuilder(2);
		pipeline[3] = new MaxFeatureValueFeatureBuilder(3);
		pipeline[4] = new MaxFeatureValueFeatureBuilder(4);
		pipeline[5] = new MaxFeatureValueFeatureBuilder(5);
		pipeline[6] = new MaxFeatureValueFeatureBuilder(6);
		pipeline[7] = new MinFeatureValueFeatureBuilder(7);
		pipeline[8] = new MinFeatureValueFeatureBuilder(8);
		//pipeline[9] = new MaxFeatureValueFeatureBuilder(9);
		pipeline[9] = new MaxFeatureValueFeatureBuilder(10);
		//pipeline[11] = new MaxFeatureValueFeatureBuilder(11);
		pipeline[10] = new MaxFeatureValueFeatureBuilder(12);
		pipeline[11] = new MaxFeatureValueFeatureBuilder(13);
		pipeline[12] = new MaxFeatureValueFeatureBuilder(14);
		//pipeline[15] = new MaxFeatureValueFeatureBuilder(15);
		pipeline[13] = new GMCountRateFeatureBuilder();
		//pipeline[16] = new SpeciesCountFeautureBuilder();
		
		//pipeline[17] = new IsInAbstractFeatureBuilder();
		//pipeline[15] = new IsInTitleFeatureBuilder();
		//pipeline[20] = new IsSpeciesInAbstractFeatureBuilder();
		pipeline[14] = new IsSpeciesInTitleFeatureBuilder();
		pipeline[15] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ITATIC);
		pipeline[16] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_CRF);
		//pipeline[19] = new MaxGMLabelCountFeatureBuilder();
		//pipeline[20] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_BC);
		//pipeline[26] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_PROTEIN);
		//pipeline[27] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_DNA);
		pipeline[17] = new HighestRankFeatureBuilder();
		pipeline[18] = new MinGMTokenNumberFeatureBuilder();
		pipeline[19] = new ContainKeywordFeatureBuilder();
		return pipeline;
	}
	
	private static RerankFeatureBuilder[] m_pipeline = getFeatureBuilderPipeline();
	
	@Override
	public String[] getFeatures(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String[] features = new String[m_pipeline.length];
		
		for(int i=0; i<features.length; i++)
		{
			features[i] = m_pipeline[i].extractFeature(document, map, candidate);
		}
		
		return features;
	}

	@Override
	public String[] getWekaAttributeFileHead() {
		// TODO Auto-generated method stub
		String[] lines = new String[m_pipeline.length];
		for(int i=0; i<lines.length; i++)
		{
			lines[i] = "@attribute feautre_"+i+" real";
		}
		return lines;
	}

}
