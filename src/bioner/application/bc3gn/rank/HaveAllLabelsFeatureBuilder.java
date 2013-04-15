package bioner.application.bc3gn.rank;

import java.util.Vector;

import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class HaveAllLabelsFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private String[] m_labels;
	public HaveAllLabelsFeatureBuilder(String[] labels)
	{
		m_labels = labels;
	}
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNEREntity entity = candidate.getEntity();
		int num=0;
		Vector<String> labelVector = entity.getLabelVector();
		for(String label : m_labels)
		{
			if(labelVector.contains(label)) num++;
		}
		return Integer.toString(num);
	}

}
