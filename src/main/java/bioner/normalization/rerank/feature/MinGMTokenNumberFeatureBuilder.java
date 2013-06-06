package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class MinGMTokenNumberFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> vector = map.get(candidate.getRecord().getID());
		int min = Integer.MAX_VALUE;
		for(BioNEREntity entity : vector)
		{
			String text = entity.getText();
			String[] words = text.split("\\W");
			if(words.length<min) min = words.length;
		}
		return Integer.toString(min);
	}

}
