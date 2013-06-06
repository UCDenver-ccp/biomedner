package bioner.normalization.gmclassification.feature;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class GMFullnameMatchPatternFeatureBuilder implements
GMClassificationFeatureBuilder {

	private Pattern m_pattern;
	public GMFullnameMatchPatternFeatureBuilder(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(entity.getDocument());
		String gmText = entity.getText();
		String fullname = fullNameMap.get(gmText);
		if(fullname==null) return "0";
		Matcher matcher = m_pattern.matcher(gmText);
		if(matcher.matches()) return "1";
		return "0";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "gm_match";
	}

}
