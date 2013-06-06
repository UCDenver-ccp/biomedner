package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class MaxGMLabelCountFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> entityVector = map.get(candidate.getRecord().getID());
		int max = Integer.MIN_VALUE;
		for(BioNEREntity entity : entityVector)
		{
			int num = entity.getLabelVector().size();
			if(num>max) max=num;
		}
		if(max<0) max = 0;
		return Integer.toString(max);
	}

}
