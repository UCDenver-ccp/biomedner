package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class MaxGMClassificationScoreFeatureBuilder implements
		RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> vector = map.get(candidate.getRecord().getID());
		double max = -Double.MAX_VALUE;
		for(BioNEREntity entity : vector)
		{
			if(entity.getScore()>max) max = entity.getScore();
		}
		if(max<0) max = 0.0;
		return Double.toString(max);
	}

}
