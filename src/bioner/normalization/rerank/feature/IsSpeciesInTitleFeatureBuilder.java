package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class IsSpeciesInTitleFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String speciesID = candidate.getRecord().getSpeciesID();
		Vector<BioNEREntity> speciesVector= SpeciesEntityStore.getSpeciesEntities(document);
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(speciesEntity.hasID(speciesID))
			{
				if(speciesEntity.get_Sentence()==document.getTitle()) return "1";
			}
		}
		return "0";
	}

}
