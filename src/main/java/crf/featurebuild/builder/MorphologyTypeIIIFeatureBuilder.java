package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import crf.featurebuild.TokenFeatureBuilder;

public class MorphologyTypeIIIFeatureBuilder implements TokenFeatureBuilder {

	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		String tokenStr = tokens[index].getText();
		String featureStr = "";
		for(int i=0; i<tokenStr.length(); i++)
		{
			char c = tokenStr.charAt(i);
			if(c>='a' && c<='z')
			{
				featureStr += "a";
			}
			else if(c>='A' && c<='Z')
			{
				featureStr += "A";
			}
			else if(c>='0' && c<='9')
			{
				featureStr += "1";
			}
			else
			{
				featureStr += "c";
			}
		}
		return featureStr;
	}

}
