package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMContainsStringFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private String m_str;
	public GMContainsStringFeatureBuilder(String str)
	{
		m_str = str;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		if(entity.getText().contains(m_str)) return "1";
		return "0";
	}
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMContainsStr_"+m_str;
	}
	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
