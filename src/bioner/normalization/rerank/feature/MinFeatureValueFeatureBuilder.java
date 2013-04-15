package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class MinFeatureValueFeatureBuilder implements RerankFeatureBuilder {
	private int m_index;
	public MinFeatureValueFeatureBuilder(int index)
	{
		m_index=index;
	}
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> entityVector = map.get(candidate.getRecord().getID());
		double min = Double.MAX_VALUE;
		for(BioNEREntity entity : entityVector)
		{
			BioNERCandidate[] candidates = entity.getCandidates();
			for(int i=0; i<candidates.length; i++)
			{
				if(candidates[i].getRecord().getID().equals(candidate.getRecord().getID()))
				{
					double value = candidates[i].getFeatures()[m_index];
					if(value<min) min=value;
				}
			}
		}
		return Double.toString(min);
	}

}
