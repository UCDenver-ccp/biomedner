package bioner.normalization.feature.builder;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class AttributeInSentenceFeatureBuilder implements  
							NormalizationPairFeatureBuilder {

	private String m_attributeName;
	public AttributeInSentenceFeatureBuilder(String attributeName)
	{
		m_attributeName = attributeName;
	}
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String attributeValue = candidate.getRecord().getAttribute(m_attributeName);
		if(attributeValue==null) return "0";
		String patternStr = getPatternStr(attributeValue);
		if(patternStr==null) return "0";
		
		String sentenceText = candidate.getEntity().get_Sentence().getSentenceText().toLowerCase();
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(sentenceText);
		if(matcher.find()) return "1";
		
		Vector<String> tokenVector = GeneMentionTokenizer.getTokens(attributeValue);
		for(String token : tokenVector)
		{
			pattern = Pattern.compile("\\b"+token+"\\b");
			matcher = pattern.matcher(sentenceText);
			if(matcher.find()) return "0.5";
		}
		
		return "0";
	}
	protected String getPatternStr(String valueStr)
	{
		Vector<String> tokenVector = GeneMentionTokenizer.getTokens(valueStr);
		if(tokenVector.isEmpty()) return null;
		StringBuffer sb = new StringBuffer("\\b");
		for(int i=0; i<tokenVector.size(); i++)
		{
			if(i>0) sb.append("[\\W\\_]*");
			sb.append(tokenVector.elementAt(i));
		}
		sb.append("\\b");
		return sb.toString();
	}
}
