package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMLastWordStrFeatureBuilder implements
		GMClassificationFeatureBuilder {

	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERSentence sentence = entity.get_Sentence();
		
		String headerWord = sentence.getTokens()[entity.getTokenEndIndex()].getText();
		if(headerWord.matches("\\W+")) return "null";
		return "\""+headerWord+"\"";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "string";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMLastWordStr";
	}

}
