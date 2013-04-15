package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import crf.featurebuild.TokenFeatureBuilder;

public class SingleCharFeatureBuilder implements TokenFeatureBuilder {

	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		//Return the special chars such as . - % ^ &....
		String tokenStr = tokens[index].getText();
		if(tokenStr.length()!=1) return "N";
		if(tokenStr.matches("[a-zA-Z0-9]|\\s")) return "N";
		return tokenStr;
	}

}
