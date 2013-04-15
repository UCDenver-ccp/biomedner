package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;


/**
 * This class is used to decide the species of gene mentions.
 * @author Liu Jingchen
 *
 */
public class GeneSpeciesLinker {

	/**
	 * 
	 * @param document	: link the gene mentions in this document with its species if possible.
	 * @return A HashMap storing species for each gene mention. The key is the gene mention string, the value is a vector of species IDs.
	 */
	public HashMap<String, Vector<String>> getSpeciesTable(BioNERDocument document)
	{
		HashMap<String, Vector<String>> speciesTable = new HashMap<String, Vector<String>>();
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		
		/*for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				int min = Integer.MAX_VALUE;
				String speciesID = null;
				for(BioNEREntity speciesEnttiy : speciesVector)
				{
					if(speciesEnttiy.get_Sentence() == sentence)
					{//When the species is in the same sentence with the gene mention
						int distance;
						if(entity.getTokenBeginIndex() < speciesEnttiy.getTokenBeginIndex())
						{//If the gene mention is in the front of the species
							//distance = speciesEnttiy.getTokenBeginIndex() - entity.getTokenEndIndex();
							continue;//We only take the species before the gene mention as its speices.
						}
						else
						{
							distance = entity.getTokenBeginIndex() - speciesEnttiy.getTokenEndIndex();
						}
						if(distance < 0) distance = 0;
						
						//Found the nearest species as the gene mention's species.
						if(distance < min)
						{
							min = distance;
							speciesID = speciesEnttiy.getID()[0];
						}
						
					}
				}//for each species entity
				
				if(speciesID!=null)
				{//If found the species for this gene mention
					Vector<String> speciesIDVector = speciesTable.get(entity.getText());
					if(speciesIDVector == null)
					{
						speciesIDVector = new Vector<String>();
						speciesTable.put(entity.getText(), speciesIDVector);
					}
					if(!speciesIDVector.contains(speciesID))
					{
						speciesIDVector.add(speciesID);
					}
				}
				
			}//for each gene mention entity
		}//for each sentence*/
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				linkSpeciesAdjacent(entity, speciesVector, speciesTable);
			}
		}
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				linkSpeciesInSentence(entity, speciesVector, speciesTable);
			}
		}
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				linkSpeciesInParagraph(entity, speciesVector, speciesTable);
			}
		}
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				linkSpeciesInSection(entity, speciesVector, speciesTable);
			}
		}
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				linkSpeciesInArticleTitle(entity, speciesVector, speciesTable);
			}
		}
		
		return speciesTable;
	}
	
	private static void linkSpeciesAdjacent(BioNEREntity entity, Vector<BioNEREntity> speciesVector, HashMap<String,Vector<String>> speciesTable)
	{
		Vector<String> speciesIDVector = speciesTable.get(entity.getText());
		if(speciesIDVector != null) return;
		speciesIDVector = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(speciesEntity.get_Sentence() == entity.get_Sentence() && entity.get_Sentence()!=null)
			{
				if(speciesEntity.getTokenEndIndex() < entity.getTokenBeginIndex())
				{
					if(Math.abs(speciesEntity.getTokenEndIndex()-entity.getTokenBeginIndex())<=2) speciesIDVector.addAll(speciesEntity.getID());;
				}
				else if(speciesEntity.getTokenBeginIndex() > entity.getTokenEndIndex())
				{
					if(Math.abs(speciesEntity.getTokenBeginIndex() - entity.getTokenEndIndex())<=2) speciesIDVector.addAll(speciesEntity.getID());;
				}
				else
				{
					speciesIDVector.addAll(speciesEntity.getID());;
				}
			}
		}
		if(!speciesIDVector.isEmpty()) speciesTable.put(entity.getText(), speciesIDVector);
	}
	
	private static void linkSpeciesInSentence(BioNEREntity entity, Vector<BioNEREntity> speciesVector, HashMap<String,Vector<String>> speciesTable)
	{
		Vector<String> speciesIDVector = speciesTable.get(entity.getText());
		if(speciesIDVector != null) return;
		speciesIDVector = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(speciesEntity.get_Sentence() == entity.get_Sentence() && entity.get_Sentence()!=null)
			{
				speciesIDVector.addAll(speciesEntity.getID());
			}
		}
		if(!speciesIDVector.isEmpty()) speciesTable.put(entity.getText(), speciesIDVector);
	}
	
	private static void linkSpeciesInParagraph(BioNEREntity entity, Vector<BioNEREntity> speciesVector, HashMap<String,Vector<String>> speciesTable)
	{
		Vector<String> speciesIDVector = speciesTable.get(entity.getText());
		if(speciesIDVector != null) return;
		speciesIDVector = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(speciesEntity.getParagraph() == entity.getParagraph() && entity.getParagraph()!=null)
			{
				speciesIDVector.addAll(speciesEntity.getID());
			}
		}
		if(!speciesIDVector.isEmpty()) speciesTable.put(entity.getText(), speciesIDVector);
	}
	private static void linkSpeciesInSection(BioNEREntity entity, Vector<BioNEREntity> speciesVector, HashMap<String,Vector<String>> speciesTable)
	{
		Vector<String> speciesIDVector = speciesTable.get(entity.getText());
		if(speciesIDVector != null) return;
		speciesIDVector = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(speciesEntity.getSection() == entity.getSection() && entity.getSection()!=null)
			{
				speciesIDVector.addAll(speciesEntity.getID());
			}
		}
		if(!speciesIDVector.isEmpty()) speciesTable.put(entity.getText(), speciesIDVector);
	}
	private static void linkSpeciesInArticleTitle(BioNEREntity entity, Vector<BioNEREntity> speciesVector, HashMap<String,Vector<String>> speciesTable)
	{
		Vector<String> speciesIDVector = speciesTable.get(entity.getText());
		if(speciesIDVector != null) return;
		speciesIDVector = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(speciesEntity.get_Sentence() == entity.getDocument().getTitle())
			{
				speciesIDVector.addAll(speciesEntity.getID());
			}
		}
		if(!speciesIDVector.isEmpty()) speciesTable.put(entity.getText(), speciesIDVector);
	}
}
