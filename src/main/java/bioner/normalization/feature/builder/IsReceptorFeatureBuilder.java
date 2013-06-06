package bioner.normalization.feature.builder;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class IsReceptorFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String gmStr = candidate.getEntity().getText().toLowerCase().trim();
		if(gmStr.contains("receptor")) return "0";
		for(String synonym : candidate.getRecord().getSynonyms())
		{
			if(synonym.contains("receptor") && synonym.contains(gmStr)) return "1";
		}
		return "0";
	}

}
