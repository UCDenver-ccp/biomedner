package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.tools.nlptools.CombinedNameRecognizer;

public class CombinedWordFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String gmText = candidate.getEntity().getText();
		HashMap<String, String> combinedNameMap = CombinedNameRecognizer.getCombinedNameMap(candidate.getEntity().getDocument());
		String combinedText = combinedNameMap.get(gmText);
		if(combinedText==null) return "0.0";
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(combinedText);
		double max = 0.0;
		for(String synonym : candidate.getRecord().getSynonyms())
		{
			Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
			double value = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
			if(max < value) max = value;
		}
		return Double.toString(max);
	}

}
