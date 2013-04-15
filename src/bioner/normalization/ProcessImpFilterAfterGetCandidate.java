package bioner.normalization;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.GMCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.process.BioNERProcess;

public class ProcessImpFilterAfterGetCandidate implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> countMap = getGMCountMap(document);
		//Vector<BioNEREntity> speciesEntityVector = SpeciesEntityStore.getSpeciesEntities(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			filterByScore(sentence, countMap);
		}
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				//filterGeneIDbyExplicitSpecies(entity, speciesEntityVector);
				removeNotMatchedCandidates(entity);
			}
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entityArray)
			{
				if(entity.getCandidates().length>0) sentence.addEntity(entity);
			}
		}
	}
	
	private void removeNotMatchedCandidates(BioNEREntity entity)
	{
		BioNERCandidate[] candidates = entity.getCandidates();
		Vector<BioNERCandidate> candidateVector = new Vector<BioNERCandidate>();
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(entity.getText());
		String gmText = entity.getText();
		
		/*for(int i=0; i<candidates.length; i++)
		{
			String symbol = candidates[i].getRecord().getSymbol();
			if(gmText.equals(symbol)) candidateVector.add(candidates[i]);
		}*/
		/*if(candidateVector.isEmpty())
		{
			for(int i=0; i<candidates.length; i++)
			{
				for(String synonym : candidates[i].getRecord().getSynonyms())
				{
					if(gmText.equals(synonym)) 
						candidateVector.add(candidates[i]);
				}
				
			}
		}*/
		String[] parts = gmText.split("\\W+");
		int tokenNum = parts.length;
		
		if(candidateVector.isEmpty())
		{
			for(int i=0; i<candidates.length; i++)
			{
				double maxR =0.0;
				double maxP =0.0;
				for(String synonym : candidates[i].getRecord().getSynonyms())
				{
					Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
					double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
					
					double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
					
					if(!gmText.matches("[a-z\\W]+") && !synonym.matches("[a-z\\W]+"))
					{
						if(gmText.toLowerCase().replaceAll("\\W+", "").equals(synonym.toLowerCase().replaceAll("\\W+", "")))
						{
							valueP = 1.0;
							valueR = 1.0;
						}
					}
					if(valueP>maxP) maxP = valueP;
					if(valueR>maxR) maxR = valueR;
					if(tokenNum<3)
					{
						if(valueP>0.49 && valueR>0.49)
						{
							candidateVector.add(candidates[i]);
							break;
						}
					}
					else
					{
						if(valueP>0.49 && valueR>0.49)
						{
							candidateVector.add(candidates[i]);
							break;
						}
					}
				}
				//if(maxP>0.60 && maxR>0.9) 
				//	candidateVector.add(candidates[i]);
			}
		}
		BioNERCandidate[] newCandidates = new BioNERCandidate[candidateVector.size()];
		for(int i=0; i<newCandidates.length; i++)
		{
			newCandidates[i] = candidateVector.elementAt(i);
		}
		entity.setCandidates(newCandidates);
	}
	
	
	private void filterByScore(BioNERSentence sentence, HashMap<String, Integer> countMap)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		
		for(BioNEREntity entity : entityArray)
		{
			String text = entity.getText();
			Integer count = countMap.get(text);
			BioNERCandidate[] candidates = entity.getCandidates();
			if(candidates==null || candidates.length==0 ||count==null) continue;
			double score = candidates[0].getScore();
			if(count>=2)
			{
				//if(score>3.0) sentence.addEntity(entity);
				sentence.addEntity(entity);
			}
			else if(count>=3)//3<=count<=9
			{
				if(score>4.0) sentence.addEntity(entity);
			}
			else if(count>=2)//count==2
			{
				if(score>5.0) sentence.addEntity(entity);
			}
			else //count==1
			{
				if(score>2.0) sentence.addEntity(entity);
			}
		}
	}
	private HashMap<String, Integer> getGMCountMap(BioNERDocument document)
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String text = entity.getText();
				Integer num = map.get(text);
				if(num==null) num=0;
				num++;
				map.put(text, num);
			}
		}
		return map;
	}
	
	private void filterGeneIDbyExplicitSpecies(BioNEREntity entity, Vector<BioNEREntity> speciesEntityVector)
	{
		Vector<String> speciesIDVector = getExplicitSpeciesID(entity, speciesEntityVector);
		if(speciesIDVector.isEmpty()) return;
		BioNERCandidate[] candidates = entity.getCandidates();
		int size=0;
		for(int i=0; i<candidates.length; i++)
		{
			if(speciesIDVector.contains(candidates[i].getRecord().getSpeciesID())) size++;
		}
		BioNERCandidate[] correctCandidates = new BioNERCandidate[size];
		int index=0;
		for(int i=0; i<candidates.length; i++)
		{
			if(speciesIDVector.contains(candidates[i].getRecord().getSpeciesID()))
			{
				correctCandidates[index] = candidates[i];
				index++;
			}
		}
		entity.setCandidates(correctCandidates);
	}
	
	private Vector<String> getExplicitSpeciesID(BioNEREntity entity, Vector<BioNEREntity> speciesEntityVector)
	{
		Vector<String> speciesIDVector = new Vector<String>();
		String gmText = entity.getText();
		String speciesInGM = getSpeciesStrInGM(gmText);
		String speciesIDInGM = getSpeciesIDInGM(speciesInGM);
		if(!speciesIDInGM.equals(""))
		{
			speciesIDVector.add(speciesIDInGM);
			return speciesIDVector;
		}
		BioNERSentence gmSentence = entity.get_Sentence();
		for(BioNEREntity speciesEntity : speciesEntityVector)
		{
			if(speciesEntity.get_Sentence()==gmSentence)
			{
				int speciesEntityEnd = speciesEntity.getTokenEndIndex();
				if(speciesEntityEnd>=entity.getTokenBeginIndex()-1 && speciesEntityEnd<=entity.getTokenEndIndex())
				{
					for(String speciesID : speciesEntity.getID())
					{
						speciesIDVector.add(speciesID);
					}
					return speciesIDVector;
				}
			}
		}
		
		return speciesIDVector;
	}
	private String getSpeciesStrInGM(String geneStr)
	{
		for(int i=0; i<geneStr.length(); i++)
		{
			char c = geneStr.charAt(i);
			if(Character.isUpperCase(c))
			{
				return geneStr.substring(0, i);
			}
		}
		return null;
	}
	private String getSpeciesIDInGM(String speciesStr)
	{
		if(speciesStr==null) return "";
		if(speciesStr.equals("h")) return "9606";
		if(speciesStr.equals("r")) return "10116";
		if(speciesStr.equals("m")) return "10090";
		if(speciesStr.equals("d")) return "7227";
		if(speciesStr.equals("me")) return "7227";
		if(speciesStr.equals("mel")) return "7227";
		if(speciesStr.equals("y")) return "4932";
		
		return "";
	}
}
