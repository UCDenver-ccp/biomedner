package crf.featurebuild.builder;


import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.tools.linnaeus.LinnaeusSpeciesNER;
import crf.featurebuild.TokenFeatureBuilder;

public class LinnaeusSpeciesFeatureBuilder implements TokenFeatureBuilder {

	private BioNERSentence m_currentSentence = null;
	private BioNEREntity[] m_currentEntities = null;
	private LinnaeusSpeciesNER m_NER = null;
	public LinnaeusSpeciesFeatureBuilder()
	{
		m_NER = LinnaeusSpeciesNER.getLinneausSpeciesNER();
	}
	
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		if(m_currentSentence != sentence)
		{
			m_currentSentence = sentence;
			m_currentEntities = m_NER.recognizeSentence(sentence);
		}
		for(BioNEREntity entity : m_currentEntities)
		{
			if(index<=entity.getTokenEndIndex() && index>=entity.getTokenBeginIndex())
			{
				return "1";
			}
		}
		return "0";
	}

}
