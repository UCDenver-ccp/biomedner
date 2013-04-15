package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import crf.featurebuild.TokenFeatureBuilder;

public class OrthographicFeatureBuilder implements TokenFeatureBuilder {

	private String m_exp = null;
	public OrthographicFeatureBuilder(String exp)
	{
		m_exp = exp;
	}
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		String tokenText = tokens[index].getText();
		if(tokenText.matches(m_exp)) return "1";
		return "0";
	}

}
