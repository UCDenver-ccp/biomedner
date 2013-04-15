package bioner.normalization.feature.builder;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SynonymNumberSimilarityFeatureBuilder extends
		SymbolNumberSimilarityFeatureBuilder implements
		NormalizationPairFeatureBuilder {
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String entityStr = candidate.getEntity().getText();
		String recordStr = candidate.getRecord().getSymbol();
		double max = getNumberSimilarityOfTwoString(entityStr, recordStr);
		
		for(String synonymStr : candidate.getRecord().getSynonyms())
		{
			double sim = getNumberSimilarityOfTwoString(entityStr, synonymStr);
			if(sim > max) max = sim;
		}
		
		return Double.toString(max);
	}
}
