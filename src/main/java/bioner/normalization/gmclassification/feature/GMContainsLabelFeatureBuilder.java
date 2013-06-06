package bioner.normalization.gmclassification.feature;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMContainsLabelFeatureBuilder implements
		GMClassificationFeatureBuilder {

	private String m_label;
	public GMContainsLabelFeatureBuilder(String label)
	{
		m_label = label;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		if(entity.containLabel(m_label)) return "1";
		return "0";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMLABEL:"+m_label;
	}

}
