package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.tools.nlptools.IteratedLovinsStemmer;
import crf.featurebuild.TokenFeatureBuilder;

public class StemFeatureBuilder implements TokenFeatureBuilder {

	private IteratedLovinsStemmer stemmer = new IteratedLovinsStemmer();
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		String tokenStr = tokens[index].getText().toLowerCase();
		String stemTokenStr = stemmer.stem(tokenStr);
		return stemTokenStr;
	}

}
