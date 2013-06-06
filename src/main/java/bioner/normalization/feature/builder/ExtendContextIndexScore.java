package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERToken;
import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.IndexReader;
import bioner.normalization.data.index.IndexReaderFactory;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;
/**
 * Extend the words before and after the gene mention, search it in the index. Use the returned score as the feature.
 * @author Liu Jingchen
 *
 */
public class ExtendContextIndexScore implements NormalizationPairFeatureBuilder {

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
		StringBuffer sb = new StringBuffer();
		BioNEREntity entity = candidate.getEntity();
		BioNERToken[] tokens = candidate.getEntity().get_Sentence().getTokens();
		//Decide the boundary of the extension. Store the begin and end index. 
		int extendedNum = 0;
		int beginIndex = entity.getTokenBeginIndex();
		for(int i=entity.getTokenBeginIndex()-1; i>=0; i--)
		{
			if(!tokens[i].getText().matches("\\W+|[\\d\\.]+"))
			{
				beginIndex = i;
				extendedNum++;
			}
			if(extendedNum>=3) break;
		}
		extendedNum = 0;
		int endIndex = entity.getTokenEndIndex();
		for(int i=entity.getTokenEndIndex()+1; i<tokens.length; i++)
		{
			if(!tokens[i].getText().matches("\\W+"))
			{
				endIndex = i;
				extendedNum++;
			}
			if(extendedNum>=3) break;
		}
		
		//Get the extended gene mention.
		for(int i=beginIndex; i<=endIndex; i++)
		{
			sb.append(tokens[i].getText());
			sb.append(" ");
		}
		String queryStr = sb.toString();
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
		return score;
	}

}
