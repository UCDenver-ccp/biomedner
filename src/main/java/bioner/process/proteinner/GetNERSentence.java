package bioner.process.proteinner;

import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.normalization.feature.builder.SpeciesEntityStore;

public class GetNERSentence {
	public static Vector<BioNERSentence> getNERSentence(BioNERDocument document)
	{
		Vector<BioNERSentence> vector = new Vector<BioNERSentence>();
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			if(isUselessSection(sentence)) continue;
			/*if(isAbstract(sentence) || isTitle(sentence) 
					||hasSpeciesInSentence(sentence, speciesVector)
					||hasSpeciesInSection(sentence, speciesVector))*/
			vector.add(sentence);
		}
		return vector;
	}
	
	private static boolean isAbstract(BioNERSentence sentence)
	{
		BioNERDocument document = sentence.getDocument();
		if(document==null) return false;
		if(sentence.getSection()==document.getAbstractSection()) return true;
		return false;
	}
	private static boolean isTitle(BioNERSentence sentence)
	{
		BioNERDocument document = sentence.getDocument();
		if(document==null) return false;
		if(sentence==document.getTitle()) return true;
		return false;
	}
	
	private static boolean isUselessSection(BioNERSentence sentence)
	{
		BioNERSection section = sentence.getSection();
		if(section==null) return false;
		String type = section.getType();
		if(type==null) return false;
		if(type.contains("material") || type.contains("method") || type.contains("supporting") || type.contains("supplementary"))
		return true;
		return false;
	}
	
	private static boolean hasSpeciesInParagraph(BioNERSentence sentence, Vector<BioNEREntity> speciesVector)
	{
		BioNERParagraph paragraph = sentence.getParagraph();
		
		if(paragraph==null) return false;
		for(BioNEREntity speciesEntity : speciesVector)
		{
			BioNERParagraph speciesParagraph = speciesEntity.getParagraph();
			if(speciesParagraph==null) continue;
			if(paragraph==speciesParagraph) return true;
		}
		return false;
	}
	
	private static boolean hasSpeciesInSentence(BioNERSentence sentence, Vector<BioNEREntity> speciesVector)
	{
		for(BioNEREntity speciesEntity : speciesVector)
		{
			BioNERSentence speciesSentence = speciesEntity.get_Sentence();
			if(speciesSentence==null) continue;
			if(sentence==speciesSentence) return true;
		}
		return false;
	}
	private static boolean hasSpeciesInSection(BioNERSentence sentence, Vector<BioNEREntity> speciesVector)
	{
		BioNERSection section = sentence.getSection();
		
		if(section==null) return false;
		for(BioNEREntity speciesEntity : speciesVector)
		{
			BioNERSection speciesSection = speciesEntity.getSection();
			if(speciesSection==null) continue;
			if(section==speciesSection) return true;
		}
		return false;
	}
	
}
