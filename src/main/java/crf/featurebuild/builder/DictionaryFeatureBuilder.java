package crf.featurebuild.builder;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictner.DictionaryBasedNER;
import crf.featurebuild.TokenFeatureBuilder;

public class DictionaryFeatureBuilder implements TokenFeatureBuilder {

	private BioNERSentence m_currentSentence = null;
	private BioNEREntity[] m_currentEntities = null;
	
	private DictionaryBasedNER m_dictNER = null;
	public DictionaryFeatureBuilder(BioNERDictionary dict)
	{
		m_dictNER = new DictionaryBasedNER(dict);
	}
	
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		if(m_currentSentence != sentence)
		{
			m_currentSentence = sentence;
			m_currentEntities = m_dictNER.SentenceBasedNER(sentence);
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
