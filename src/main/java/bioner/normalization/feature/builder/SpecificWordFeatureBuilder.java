package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.tools.dictionary.WordJudge;
/**
 * For each candidate, get the words of its synonyms, which is not contained by other candidates with same species.
 * Count how many such word appear in the sentence and use the number as feature.
 * @author Liu Jingchen
 *
 */
public class SpecificWordFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	public static BioNEREntity m_currentEntity = null;
	public static HashMap<String, Vector<String>> m_currentMap = null;
	private static int m_rank =5;
	
	
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNEREntity entity = candidate.getEntity();
		if(m_currentEntity != entity)
		{
			m_currentEntity = entity;
			m_currentMap = getSpecificWordMap(m_currentEntity);
		}
		String sentenceText = candidate.getEntity().get_Sentence().getSentenceText();
		sentenceText = sentenceText.toLowerCase();
		Vector<String> specificWordVector = m_currentMap.get(candidate.getRecord().getID());
		int num=0;
		for(String word : specificWordVector)
		{
			Pattern pattern = Pattern.compile("\\b"+word+"\\b");
			Matcher matcher = pattern.matcher(sentenceText);
			if(matcher.find()) num++;
		}
		return Integer.toString(num);
	}
	
	public static HashMap<String, Vector<String>> getSpecificWordMap(BioNEREntity entity)
	{
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		HashMap<String, Vector<String>> allWordMap = new HashMap<String, Vector<String>>();
		
		//Get all words from synonyms for each candidate
		for(BioNERCandidate candidate : entity.getCandidates())
		{
			String id = candidate.getRecord().getID();
			Vector<String> allWordVector = allWordMap.get(id);
			if(allWordVector==null)
			{
				allWordVector = new Vector<String>();
				allWordMap.put(id, allWordVector);
			}
			for(String synonym : candidate.getRecord().getSynonyms())
			{
				String[] words = synonym.split("\\W+");
				for(String word : words)
				{
					//Drop the words having upper case or digit
					if(!word.matches(".*[A-Z].*|.*\\d.*") && !allWordVector.contains(word) && WordJudge.isWordIndex(word))
					{
						allWordVector.add(word);
					}
				}
			}
		}
		
		//For each candidate, get the words which is not contained by other candidates
		for(BioNERCandidate candidate : entity.getCandidates())
		{
			String id = candidate.getRecord().getID();
			Vector<String> allWordVector = allWordMap.get(id);
			Vector<String> specificWordVector = map.get(id);
			if(specificWordVector==null)
			{
				specificWordVector = new Vector<String>();
				map.put(id, specificWordVector);
			}
			specificWordVector.addAll(allWordVector);
			for(int i=0; i<entity.getCandidates().length && i<m_rank; i++)
			{
				BioNERCandidate otherCandidate = entity.getCandidates()[i];
				if(candidate == otherCandidate) continue;
				
				
				Vector<String> otherAllWordVector = allWordMap.get(otherCandidate.getRecord().getID());
				specificWordVector.removeAll(otherAllWordVector);
			}
		}
		
		return map;
	}

}
