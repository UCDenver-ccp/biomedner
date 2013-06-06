package bioner.normalization.feature;

import bioner.normalization.data.BioNERCandidate;

public class NormalizationFeatureBuilder {
	
	private NormalizationPairFeatureBuilder[] m_pipeline = PairFeatureBuilderFactory.getFeatureBuilderPipeline();
	
	public String[] getFeatures(BioNERCandidate candidate)
	{
		String[] features = new String[m_pipeline.length];
		
		for(int i=0; i<features.length; i++)
		{
			features[i] = m_pipeline[i].extractFeature(candidate);
		}
		
		return features;
	}
	
	public static String[] getWekaAttributeFileHead()
	{
		return PairFeatureBuilderFactory.getWekaAttributeFileHead();
	}
}
