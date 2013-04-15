package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import crf.featurebuild.TokenFeatureBuilder;

public class AllUpcaseFeatureBuilder implements TokenFeatureBuilder {

	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		String tokenStr = tokens[index].getText();
		int length = tokenStr.length();
		for(int i=0; i<length; i++)
		{
			char c = tokenStr.charAt(i);
			if(c>='a' && c<='z') return "0";
		}
		return "1";
	}

}
