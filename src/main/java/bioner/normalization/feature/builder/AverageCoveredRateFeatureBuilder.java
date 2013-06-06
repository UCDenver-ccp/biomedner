package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class AverageCoveredRateFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(candidate.getEntity().getText());
		double max = 0.0;
		String gmText = candidate.getEntity().getText().toLowerCase().replaceAll("\\W+", "");
		for(String synonym : candidate.getRecord().getSynonyms())
		{
			if(gmText.equals(synonym.toLowerCase().replaceAll("\\W+", ""))) return "1.0";
			Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
			double r = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
			double p = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
			double value = 2*p*r/(p+r);
			if(Double.isNaN(value) || Double.isInfinite(value)) value=0.0;
			if(max < value) max = value;
		}
		return Double.toString(max);
	}

}
