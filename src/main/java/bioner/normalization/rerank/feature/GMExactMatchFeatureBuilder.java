package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.GMCoveredRateFeatureBuilder;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class GMExactMatchFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> gmVector = map.get(candidate.getRecord().getID());
		for(BioNEREntity gmEntity : gmVector)
		{
			String gmText = gmEntity.getText();
			Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(gmText);
			for(String synonym : candidate.getRecord().getSynonyms())
			{
				Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
				double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
				
				double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
				if(valueP>0.9 && valueR>0.9) return "1";
			}
		}
		
		return "0";
	}

}
