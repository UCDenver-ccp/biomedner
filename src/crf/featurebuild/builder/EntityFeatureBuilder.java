package crf.featurebuild.builder;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import crf.featurebuild.TokenFeatureBuilder;

public class EntityFeatureBuilder implements TokenFeatureBuilder {

	private String m_type;
	public EntityFeatureBuilder(String type)
	{
		m_type = type;
	}
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			if(entity.get_Type().equals(m_type))
			{
				if(index<=entity.getTokenEndIndex() && index>=entity.getTokenBeginIndex())
				{
					return "1";
				}
			}
		}
		return "0";
	}

}
