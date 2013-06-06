package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.IndexConfig;
import bioner.normalization.data.index.LuceneSpeciesIndexBuilder;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class CommonSpeciesFeatureBuilder implements
		NormalizationPairFeatureBuilder {
	private static Vector<String> m_commonSpeciesVector = LuceneSpeciesIndexBuilder.readCommonSpeciesList(IndexConfig.COMMON_SPEICIES_FILENAME); 
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String speciesID = candidate.getRecord().getSpeciesID();
		if(m_commonSpeciesVector.contains(speciesID)) return "1";
		return "0";
	}

}
