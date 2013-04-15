package crf.featurebuild.builder;

import java.util.Vector;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.index.LuceneIndexNER;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictner.DictionaryBasedNER;
import crf.featurebuild.TokenFeatureBuilder;

public class IndexNERLabelFeatureBuilder implements TokenFeatureBuilder {

	private BioNERSentence m_currentSentence = null;
	private Vector<BioNEREntity> m_currentEntities = null;
	private LuceneIndexNER m_indexNER = null;
	public IndexNERLabelFeatureBuilder(String indexDir)
	{
		m_indexNER = new LuceneIndexNER(indexDir);
	}
	
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		if(m_currentSentence != sentence)
		{
			m_currentSentence = sentence;
			m_currentEntities = m_indexNER.indexNER(sentence);
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
