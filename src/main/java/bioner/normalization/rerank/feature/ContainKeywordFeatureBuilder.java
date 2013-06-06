package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class ContainKeywordFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> vector = map.get(candidate.getRecord().getID());
		Pattern pattern = Pattern.compile("\\b.*[A-Z].*\\b|\\b[a-z]+\\d+\b");
		for(BioNEREntity entity : vector)
		{
			String text = entity.getText();
			
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()) return "1";
		}
		return "0";
	}

}
