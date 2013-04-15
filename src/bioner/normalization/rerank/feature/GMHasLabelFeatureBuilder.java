package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class GMHasLabelFeatureBuilder implements RerankFeatureBuilder {
	private String m_label;
	public GMHasLabelFeatureBuilder(String label)
	{
		m_label=label;
	}
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> entityVector = map.get(candidate.getRecord().getID());
		for(BioNEREntity entity : entityVector)
		{
			if(entity.containLabel(m_label)) return "1";
		}
		return "0";
	}

}
