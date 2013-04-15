package bioner.normalization.rerank;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.SpeciesParentFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.SpeciesEntityStore;

public class FilterBySpecies {
	public static void filter(Vector<BioNERCandidate> candidateVector, BioNERDocument document, HashMap<String, Vector<BioNEREntity>> geneIDMap)
	{
		Vector<BioNEREntity> speciesVector= SpeciesEntityStore.getSpeciesEntities(document);
		Vector<BioNERCandidate> orignalVector = (Vector<BioNERCandidate>)candidateVector.clone();
		candidateVector.clear();
		for(BioNERCandidate candidate : orignalVector)
		{
			if(ifHasIDGM(geneIDMap.get(candidate.getRecordID())))
			{
				candidateVector.add(candidate);
				continue;
			}
			String speciesID = candidate.getRecord().getSpeciesID();
			for(BioNEREntity speciesEntity : speciesVector)
			{
				if(speciesEntity.hasID(speciesID)
						|| SpeciesParentFinder.hasParent(speciesID, speciesEntity.getID()))
				{
					candidateVector.add(candidate);
					break;
				}
			}
				
		}
	}
	private static boolean ifHasIDGM(Vector<BioNEREntity> gmVector)
	{
		if(gmVector==null) return false;
		for(BioNEREntity entity : gmVector)
		{
			if(entity.containLabel(GlobalConfig.ENTITY_LABEL_IDGM)) return true;
		}
		return false;
	}
}
