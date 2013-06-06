package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMCandidadateHighestScoreFeatureBuilder implements
		GMClassificationFeatureBuilder {

	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERCandidate[] candidates = entity.getCandidates();
		double max = -Double.MAX_VALUE;
		for(int i=0; i<candidates.length; i++)
		{
			if(max < candidates[i].getScore())
			{
				max = candidates[i].getScore();
			}
		}
		if(max<0) max = 0;
		return Double.toString(max);
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "HighestScore";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
