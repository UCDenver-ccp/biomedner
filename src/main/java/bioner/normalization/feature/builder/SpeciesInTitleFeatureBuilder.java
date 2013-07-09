package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.SpeciesParentFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SpeciesInTitleFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERDocument document = candidate.getEntity().getDocument();
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		
		BioNERSentence sentence = document.getTitle();
		if(sentence==null) return "0";
		String recordSpeciesID = candidate.getRecord().getSpeciesID();
		int num=0;
		for(BioNEREntity entity : speciesVector)
		{
			if(entity.get_Sentence()==sentence)
			{
				if(entity.hasID(recordSpeciesID) || SpeciesParentFinder.hasParent(recordSpeciesID, entity.getID()))
				{
					num++;
				}
			}
					
		}
		if(num>0) return "1";
		else return "0";
		//return Integer.toString(num);
	}

}