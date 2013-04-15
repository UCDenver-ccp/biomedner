package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SynonymExactMatchFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String gmText = candidate.getEntity().getText();
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(gmText);
		for(String synonym : candidate.getRecord().getSynonyms())
		{
			Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
			double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
			
			double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
			if(valueP>0.9 && valueR>0.9) return "1";
		}
		return "0";
	}

}
