package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMWordNumFeatureBuilder implements GMClassificationFeatureBuilder {

	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		String text = entity.getText();
		String[] words = text.split("\\W+");
		
		return Integer.toString(words.length);
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMWordNum";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
