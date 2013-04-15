package bioner.normalization.gmclassification.feature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMSameGMExtendMatchPatternFeatureBuilder implements
		GMClassificationFeatureBuilder {

	private Pattern m_pattern;
	public GMSameGMExtendMatchPatternFeatureBuilder(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		String gmText = entity.getText();
		for(BioNERSentence sentence : entity.getDocument().getAllSentence())
		{
			for(BioNEREntity otherEntity : sentence.getAllEntities())
			{
				if(otherEntity.getText().equals(gmText))
				{
					String otherGMText = GMExtendedMatcherPattern.getExtendedText_byChunk(otherEntity);
					Matcher matcher = m_pattern.matcher(otherGMText);
					if(matcher.matches()) return "1";
				}
			}
		}
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
		return "gm_extend_match";
	}

}
