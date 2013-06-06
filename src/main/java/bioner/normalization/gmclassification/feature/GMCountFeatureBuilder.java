package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMCountFeatureBuilder implements GMClassificationFeatureBuilder {

	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERDocument document = entity.getDocument();
		String entityStr = entity.getText();
		int count=0;
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity other_entity : sentence.getAllEntities())
			{
				if(entity == other_entity) continue;
				if(other_entity.getText().equals(entityStr)) count++;
			}
		}
		return Integer.toString(count);
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMCount";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
