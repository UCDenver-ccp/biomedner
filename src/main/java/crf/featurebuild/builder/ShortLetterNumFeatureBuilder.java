package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import crf.featurebuild.TokenFeatureBuilder;

public class ShortLetterNumFeatureBuilder implements TokenFeatureBuilder {

	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		String tokenStr = tokens[index].getText();
		if(tokenStr.matches("[a-zA-Z]{1,2}[0-9]+"))
		{
			int pos = -1;
			for(int i=0; i<tokenStr.length(); i++)
			{
				char c = tokenStr.charAt(i);
				if(c>='0'&&c<='9')
				{
					pos = i;
					break;
				}
			}
			String letterStr = tokenStr.substring(0,pos);
			return letterStr;
		}
		return "NULL";
	}

}
