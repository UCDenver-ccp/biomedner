package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SynonymCoveredRateFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(candidate.getEntity().getText());
		String gmText = candidate.getEntity().getText().toLowerCase().replaceAll("\\W+", "");
		double max = 0.0;
		for(String synonym : candidate.getRecord().getSynonyms())
		{
			if(gmText.equals(synonym.toLowerCase().replaceAll("\\W+", ""))) return "1.0";
			Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
			double value = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
			if(max < value) max = value;
		}
		return Double.toString(max);
	}

}
