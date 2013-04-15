package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class MaxRankFeatureValueFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private int m_index;
	public MaxRankFeatureValueFeatureBuilder(int index)
	{
		m_index = index;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERCandidate[] candidates = entity.getCandidates();
		double max = -Double.MAX_VALUE;
		for(int i=0; i<candidates.length; i++)
		{
			double value = candidates[i].getFeatures()[m_index];
			if(value>max) max = value;
		}
		if(max<0.0) max = 0.0;
		return Double.toString(max);
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "Max_"+m_index;
	}
	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
