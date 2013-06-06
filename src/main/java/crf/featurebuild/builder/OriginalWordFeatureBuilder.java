package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import crf.featurebuild.TokenFeatureBuilder;

public class OriginalWordFeatureBuilder implements TokenFeatureBuilder {

	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		return sentence.getTokens()[index].getText();
	}

}
