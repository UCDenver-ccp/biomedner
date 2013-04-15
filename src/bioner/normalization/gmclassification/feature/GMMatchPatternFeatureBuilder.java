package bioner.normalization.gmclassification.feature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMMatchPatternFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private Pattern m_pattern;
	public GMMatchPatternFeatureBuilder(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		String gmText = entity.getText();
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
