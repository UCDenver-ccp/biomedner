package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class MinRankFeatureValueFeatureBuilder implements
		GMClassificationFeatureBuilder {

	private int m_index;
	public MinRankFeatureValueFeatureBuilder(int index)
	{
		m_index = index;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERCandidate[] candidates = entity.getCandidates();
		double min = 100;
		for(int i=0; i<candidates.length; i++)
		{
			double value = candidates[i].getFeatures()[m_index];
			if(value<min) min = value;
		}
		return Double.toString(min);
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
