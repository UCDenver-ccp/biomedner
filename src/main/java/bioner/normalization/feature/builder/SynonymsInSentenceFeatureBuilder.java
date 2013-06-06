package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.IndexReader;
import bioner.normalization.data.index.IndexReaderFactory;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
/**
 * Check how many synonyms of the candidate appear in the context.
 * @author Liu Jingchen
 *
 */
public class SynonymsInSentenceFeatureBuilder implements
		NormalizationPairFeatureBuilder {
	private IndexReader indexReader = IndexReaderFactory.createGeneIndexReader();
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		
		HashMap<BioNERCandidate, Double> map = getValueMap(candidate);
		double value = map.get(candidate);
		return Double.toString(value);
	}
	
	private static BioNEREntity m_currentEntity = null;
	private static HashMap<BioNERCandidate, Double> m_currentMap = null;
	private HashMap<BioNERCandidate, Double> getValueMap(BioNERCandidate candidate)
	{
		BioNEREntity entity = candidate.getEntity();
		if(m_currentEntity==entity) return m_currentMap;
		m_currentEntity = entity;
		m_currentMap = new HashMap<BioNERCandidate, Double>();
		double max = 0.0;
		for(BioNERCandidate gmCandidate : entity.getCandidates())
		{
			double value = getValue(gmCandidate);
			m_currentMap.put(gmCandidate, value);
			if(Math.abs(value)>max) max = Math.abs(value);
		}
		for(BioNERCandidate gmCandidate : entity.getCandidates())
		{
			double value = m_currentMap.get(gmCandidate);
			value = value / max;
			if(Double.isNaN(value) || Double.isInfinite(value)) value = 0.0;
			m_currentMap.put(gmCandidate, value);
		}
		
		return m_currentMap;
	}
	
	
	private double getValue(BioNERCandidate candidate)
	{
		String valueStr = maxFind(candidate);
		double value = Double.parseDouble(valueStr);
		return value;
	}
	
	private String exactFind(BioNERCandidate candidate)
	{
		String sentenceText = candidate.getEntity().get_Sentence().getSentenceText();
		Vector<String> tokenSentenceVector = GeneMentionTokenizer.getTokens(sentenceText);
		double score = 0.0;
		for(String synonymStr : candidate.getRecord().getSynonyms())
		{
			Vector<String> tokenSynonymVector = GeneMentionTokenizer.getTokens(synonymStr);
			
			if(!tokenSynonymVector.isEmpty())
			{
				//Count how many words of the synonym appear in the context.
				int num=0;
				for(String tokenStr : tokenSynonymVector)
				{
					if(tokenSentenceVector.contains(tokenStr))
					{
						num++;
					}
				}
				//Add the appear rate to the score.
				if(num==tokenSynonymVector.size()) return "1";
			}
		}
		
		return "0";
	}
	private String fuzzyFind(BioNERCandidate candidate)
	{
		String sentenceText = candidate.getEntity().get_Sentence().getSentenceText();
		Vector<String> tokenSentenceVector = GeneMentionTokenizer.getTokens(sentenceText);
		double score = 0.0;
		for(String synonymStr : candidate.getRecord().getSynonyms())
		{
			Vector<String> tokenSynonymVector = GeneMentionTokenizer.getTokens(synonymStr);
			
			if(!tokenSynonymVector.isEmpty())
			{
				//Count how many words of the synonym appear in the context.
				int num=0;
				for(String tokenStr : tokenSynonymVector)
				{
					if(tokenSentenceVector.contains(tokenStr))
					{
						num++;
					}
				}
				//Add the appear rate to the score.
				score += (double)num / (double)tokenSynonymVector.size();
			}
		}
		
		return Double.toString(score);
	}
	private String maxFind(BioNERCandidate candidate)
	{
		Vector<String> tokenDocumentVector = getDocumentTokenVector(candidate.getEntity().getDocument());
		double maxScore = 0.0;
		String geneMentionStr = candidate.getEntity().getText();
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(geneMentionStr);
		for(String synonymStr : candidate.getRecord().getSynonyms())
		{
			Vector<String> tokenSynonymVector = GeneMentionTokenizer.getTokens(synonymStr);
			
			boolean isGM = true;
			for(String tokenStr : tokenSynonymVector)
			{
				if(!gmTokenVector.contains(tokenStr))
				{
					isGM = false;
					break;
				}
			}
			if(isGM) continue;
			
			Vector<String> allTokenVector = tokenSynonymVector;
			tokenSynonymVector = new Vector<String>();
			for(String tokenStr : allTokenVector)
			{
				if(tokenStr.matches(".*[A-Z].*|.*\\d.*")) continue;
				//if(!WordJudge.isWordIndex(tokenStr)) continue;
				
				if(tokenStr.matches("(domain|region|family|ligand|sequence|homolog|superfamily|polymeric|cell|acid|antibody|antibodies|proteins|complex|gene|antigen|subfamily|group|reagent)+('?s)?$"))
					continue;
				tokenSynonymVector.add(tokenStr);
			}
			
			
			if(!tokenSynonymVector.isEmpty())
			{
				//Count how many words of the synonym appear in the context.
				double score = 0.0;
				double normal = 0.0;
				for(String tokenStr : tokenSynonymVector)
				{
					if(gmTokenVector.contains(tokenStr)) continue;
					double idf =  indexReader.getIDF(tokenStr);
					normal += idf;
					if(tokenDocumentVector.contains(tokenStr))
					{
						score += idf;
					}
				}
				//score = score / normal;
				if(!Double.isNaN(score) && !Double.isInfinite(score) && score > maxScore) maxScore = score;
				
			}
		}
		
		return Double.toString(maxScore);
	}
	
	private static BioNERDocument m_currentDocument = null;
	private static Vector<String> m_currentVector = null;
	public static Vector<String> getDocumentTokenVector(BioNERDocument document)
	{
		if(m_currentDocument==document) return m_currentVector;
		m_currentDocument = document;
		Vector<String> vector = new Vector<String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			String sentenceText = sentence.getSentenceText();
			Vector<String> tokenSentenceVector = GeneMentionTokenizer.getTokens(sentenceText);
			for(String tokenStr : tokenSentenceVector)
			{
				if(!vector.contains(tokenStr)) vector.add(tokenStr);
			}
		}
		m_currentVector = vector;
		return vector;
	}

}
