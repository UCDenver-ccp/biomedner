package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMContainsKeywordFeatureBuilder implements
		GMClassificationFeatureBuilder {

	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		String text = entity.getText();
		if(text.matches("[a-z\\W]+")) return "0";
		return "1";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "ContainKeyWord";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
