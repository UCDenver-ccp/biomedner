package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.normalization.SpeciesParentFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SpeciesNearbyFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private static BioNERDocument m_currentDocument = null;
	private static HashMap<BioNEREntity, Vector<BioNEREntity>> m_currentMap = null;
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNEREntity entity = candidate.getEntity();
		BioNERDocument document = candidate.getEntity().getDocument();
		if(document!=m_currentDocument)
		{
			m_currentDocument = document;
			m_currentMap = getNearestSpecies(m_currentDocument);
		}
		Vector<BioNEREntity> speciesVector = m_currentMap.get(entity);
		for(BioNEREntity speciesEntity : speciesVector)
		{
			
			if(speciesEntity.hasID(candidate.getRecord().getSpeciesID())) return "1";
		}
		
		return "0";
	}
	
	public static HashMap<BioNEREntity, Vector<BioNEREntity>> getNearestSpecies(BioNERDocument document)
	{
		HashMap<BioNEREntity, Vector<BioNEREntity>> map = new HashMap<BioNEREntity, Vector<BioNEREntity>>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
				Vector<BioNEREntity> nearSpeciesVector = new Vector<BioNEREntity>();
				map.put(entity, nearSpeciesVector);
				for(BioNEREntity speciesEntity : speciesVector)
				{
					if(speciesEntity.get_Sentence()==sentence)
					{
						if(speciesEntity.getTokenEndIndex() < entity.getTokenBeginIndex())
						{
							if(Math.abs(speciesEntity.getTokenEndIndex()-entity.getTokenBeginIndex())<=2) nearSpeciesVector.add(speciesEntity);
						}
						else if(speciesEntity.getTokenBeginIndex() > entity.getTokenEndIndex())
						{
							if(Math.abs(speciesEntity.getTokenBeginIndex() - entity.getTokenEndIndex())<=2) nearSpeciesVector.add(speciesEntity);
						}
					}	
				}
				if(!nearSpeciesVector.isEmpty()) 
				{
					continue;
				}
				
				//Find species in the same sentence.
				for(BioNEREntity speciesEntity : speciesVector)
				{
					if(speciesEntity.get_Sentence()==sentence)
					{
						nearSpeciesVector.add(speciesEntity);
					}	
				}
				if(!nearSpeciesVector.isEmpty())
				{
					continue;
				}
				//Find species in the same paragraph.
				BioNERParagraph paragraph = entity.getParagraph();
				if(paragraph==null)
				{
					continue;
				}
				for(BioNEREntity speciesEntity : speciesVector)
				{
					if(speciesEntity.getParagraph()==paragraph)
					{
						nearSpeciesVector.add(speciesEntity);
					}	
				}
				if(!nearSpeciesVector.isEmpty())
				{
					continue;
				}
				
				//Find species in the same section.
				BioNERSection section = entity.getSection();
				if(section==null)
				{
					continue;
				}
				for(BioNEREntity speciesEntity : speciesVector)
				{
					if(speciesEntity.getSection()==section)
					{
						nearSpeciesVector.add(speciesEntity);
					}	
				}
			}
		}
		return map;
	}

}
