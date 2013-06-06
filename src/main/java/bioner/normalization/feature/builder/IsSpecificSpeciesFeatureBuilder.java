package bioner.normalization.feature.builder;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class IsSpecificSpeciesFeatureBuilder implements
		NormalizationPairFeatureBuilder {
	private String m_speciesID = null;
	public IsSpecificSpeciesFeatureBuilder(String speciesID)
	{
		m_speciesID = speciesID;
	}
	
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		if(candidate.getRecord().getSpeciesID().equals(m_speciesID)) return "1";
		return "0";
	}

}
