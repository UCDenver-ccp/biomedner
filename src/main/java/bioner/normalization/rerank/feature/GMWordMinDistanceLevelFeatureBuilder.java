package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERToken;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class GMWordMinDistanceLevelFeatureBuilder implements
		RerankFeatureBuilder {
	private Pattern m_pattern;
	private int m_beginBound = Integer.MAX_VALUE;
	private int m_endBound = Integer.MAX_VALUE;
	public GMWordMinDistanceLevelFeatureBuilder(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	public GMWordMinDistanceLevelFeatureBuilder(String patternStr, int beginBound, int endBound)
	{
		m_pattern = Pattern.compile(patternStr);
		m_beginBound = beginBound;
		m_endBound = endBound;
	}
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		int minDis = Integer.MAX_VALUE;
		Vector<BioNEREntity> gmVector = map.get(candidate.getRecord().getID());
		for(BioNEREntity gmEntity : gmVector)
		{
			int dis = getDistance(gmEntity);
			if(minDis>dis) minDis = dis;
		}
		if(minDis==0) return "0";
		if(minDis>=1 && minDis<=2) return "1";
		if(minDis>=2 && minDis<=5) return "2";
		return "3";
	}

	private int getDistance(BioNEREntity entity)
	{
		BioNERToken[] tokens = entity.get_Sentence().getTokens();
		int minDis = Integer.MAX_VALUE;
		int minBegin = entity.getTokenBeginIndex() - m_beginBound;
		int maxEnd = entity.getTokenEndIndex() + m_endBound;
		for(int i=entity.getTokenBeginIndex(); i<=entity.getTokenEndIndex(); i++)
		{
			String tokenText = tokens[i].getText();
			Matcher matcher = m_pattern.matcher(tokenText);
			if(matcher.matches())
			{
				int dis = getDistance(entity, i);
				if(dis<minDis) minDis = dis;
				break;
			}
		}
		if(minDis>0)
		{
			for(int i=entity.getTokenBeginIndex()-1; i>=0 && i>=minBegin; i--)
			{
				String tokenText = tokens[i].getText();
				Matcher matcher = m_pattern.matcher(tokenText);
				if(matcher.matches())
				{
					int dis = getDistance(entity, i);
					if(dis<minDis) minDis = dis;
					break;
				}
			}
		}
		if(minDis>0)
		{
			for(int i=entity.getTokenEndIndex()+1; i<tokens.length && i<=maxEnd; i++)
			{
				String tokenText = tokens[i].getText();
				Matcher matcher = m_pattern.matcher(tokenText);
				if(matcher.matches())
				{
					int dis = getDistance(entity, i);
					if(dis<minDis) minDis = dis;
					break;
				}
			}
		}
		return minDis;
	}
	public static int getDistance(BioNEREntity entity, int index)
	{
		if(index<entity.getTokenBeginIndex()) return entity.getTokenBeginIndex()-index;
		else if(index>entity.getTokenEndIndex()) return index-entity.getTokenEndIndex();
		return 0;
	}
}
