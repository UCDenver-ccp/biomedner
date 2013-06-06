package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.TokenPOSTagger;
import crf.featurebuild.TokenFeatureBuilder;

public class POSFeatureBuilder implements TokenFeatureBuilder {

	private TokenPOSTagger posTagger = NLPToolsFactory.getPOSTagger();
	
	//Here, we store the current token array and pos tag result. So if the input token array is the same as the last time one, we don't need to pos tag it again.
	private BioNERToken[] currentTokenArray = null;	//This is for checking if the input is a new one.
	private String[] currentPOSTagArray = null;
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		//If a new sentence is sent here, we need to pos tag it.
		if(currentTokenArray!=tokens)
		{
			currentTokenArray = tokens;
			String[] tokenStrArray = new String[tokens.length];
			for(int i=0; i<tokens.length; i++)
			{
				tokenStrArray[i] = tokens[i].getText();
			}
			currentPOSTagArray = posTagger.POSTag(tokenStrArray);
		}
		
		
		return currentPOSTagArray[index];
	}

}
