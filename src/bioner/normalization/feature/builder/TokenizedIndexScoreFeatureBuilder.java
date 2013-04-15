package bioner.normalization.feature.builder;

import java.util.HashMap;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class TokenizedIndexScoreFeatureBuilder implements
		NormalizationPairFeatureBuilder {
	
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
		String queryStr = candidate.getEntity().getText();
		BioNERDocument document = candidate.getEntity().getDocument();
		
		
		BioNERCandidate[] candidates = IndexTokenizedSearcher.getCandidates(queryStr, document);
		String idStr = candidate.getRecordID();
		double score = 0.0;
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecordID().equals(idStr))
			{
				score = candidates[i].getScore();
				//score -= 3.0;
				//if(score<0) score=0.0;
				break;
			}
		}
		
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		queryStr = fullNameMap.get(candidate.getEntity().getText());
		if(queryStr!=null)
		{
			candidates = IndexTokenizedSearcher.getCandidates(queryStr, document);
			for(int i=0; i<candidates.length; i++)
			{
				if(candidates[i].getRecordID().equals(idStr))
				{
					if(candidates[i].getScore()>score) score = candidates[i].getScore();
					//score -= 3.0;
					//if(score<0) score=0.0;
					break;
				}
			}
		}
		return score;
	}
	
}
