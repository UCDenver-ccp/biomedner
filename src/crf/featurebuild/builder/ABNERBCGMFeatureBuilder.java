package crf.featurebuild.builder;

import java.util.Vector;

import abner.Tagger;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import crf.featurebuild.TokenFeatureBuilder;

public class ABNERBCGMFeatureBuilder implements TokenFeatureBuilder {

	private static Tagger m_taggerBioCreative = new Tagger(Tagger.BIOCREATIVE);
	private static BioNERSentence m_currentSentence = null;
	private static Vector<BioNEREntity> m_currentEntityVector = null;
	
	private static Vector<BioNEREntity> getEntityVector(BioNERSentence sentence)
	{
		Vector<BioNEREntity> vector = new Vector<BioNEREntity>();
		String text = sentence.getSentenceText();
		String[][] entities = m_taggerBioCreative.getEntities(text);
		for(String gmStr : entities[0])
		{
			int pos = text.indexOf(gmStr);
			while(pos>=0)
			{
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				entity.set_position(pos, pos+gmStr.length()-1);
				entity.addLabel(GlobalConfig.ENTITY_LABEL_ABNER_BC);
				vector.add(entity);
				pos = text.indexOf(gmStr, pos+gmStr.length());
			}
		}
		
		return vector;
	}
	
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		if(m_currentSentence!=sentence)
		{
			m_currentEntityVector = getEntityVector(sentence);
			m_currentSentence = sentence;
		}
		for(BioNEREntity entity : m_currentEntityVector)
		{
			if(index<=entity.getTokenEndIndex() && index>=entity.getTokenBeginIndex())
			{
				return "1";
			}
		}
		return "0";
	}

}
