package bioner.normalization.gmclassification.feature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERToken;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMWordDistanceFeatureBuilder implements
		GMClassificationFeatureBuilder {

	private Pattern m_pattern;
	private int m_beginBound = Integer.MAX_VALUE;
	private int m_endBound = Integer.MAX_VALUE;
	public GMWordDistanceFeatureBuilder(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	public GMWordDistanceFeatureBuilder(String patternStr, int beginBound, int endBound)
	{
		m_pattern = Pattern.compile(patternStr);
		m_beginBound = beginBound;
		m_endBound = endBound;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
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
		if(minDis==0) return "1.5";
		if(minDis==Integer.MAX_VALUE || minDis>10) return "0";
		return Double.toString(1.0/(double)minDis);
	}
	
	public static int getDistance(BioNEREntity entity, int index)
	{
		if(index<entity.getTokenBeginIndex()) return entity.getTokenBeginIndex()-index;
		else if(index>entity.getTokenEndIndex()) return index-entity.getTokenEndIndex();
		return 0;
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "DIS";
	}

}
