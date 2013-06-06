package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class IsInTitleFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> entityVector = map.get(candidate.getRecord().getID());
		for(BioNEREntity entity : entityVector)
		{
			if(entity.get_Sentence()==document.getTitle()) return "1";
		}
		return "0";
	}

}
