package bioner.normalization.gmclassification.feature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMHaveSpecialWordFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private Pattern m_pattern;
	private String m_str;
	public GMHaveSpecialWordFeatureBuilder(String word)
	{
		m_pattern = Pattern.compile(".*\\b"+word+"s?\\b.*");
		m_str = word;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		
		String text = entity.getText();
		Matcher matcher = m_pattern.matcher(text);
		if(matcher.matches())
		{
			return "1";
		}
		
		return "0";
	}
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMContainsWord";
	}
	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
