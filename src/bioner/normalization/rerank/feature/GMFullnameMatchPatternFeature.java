package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class GMFullnameMatchPatternFeature implements RerankFeatureBuilder {
	private Pattern m_pattern;
	public GMFullnameMatchPatternFeature(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> entityVector = map.get(candidate.getRecord().getID());
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(BioNEREntity entity : entityVector)
		{
			
			String gmText = entity.getText();
			String fullname = fullNameMap.get(gmText);
			if(fullname==null) continue;
			Matcher matcher = m_pattern.matcher(gmText);
			if(matcher.matches()) return "1";
		}
		return "0";
	}

}
