package bioner.normalization.feature.builder;

import java.util.HashMap;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.BioNERRecord;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class GeneOutsideBracketFeatureBuilder implements NormalizationPairFeatureBuilder {
	
	private static BioNERDocument m_currentDocument = null;
	private static HashMap<String, String> m_currentMap = null;
	
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERDocument document = candidate.getEntity().getDocument();
		if(m_currentDocument != document)
		{
			m_currentDocument = document;
			m_currentMap = getMap(m_currentDocument);
		}
		String entityText = candidate.getEntity().getText();
		
		
		String contextStr = m_currentMap.get(entityText);
		if(contextStr==null) return "0.0";
		BioNERCandidate[] candidates = IndexTokenizedSearcher.getCandidates(contextStr, document);
		String idStr = candidate.getRecordID();
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecordID().equals(idStr))
			{
				double score = candidates[i].getScore();
				//score -= 3.0;
				//if(score<0) score=0.0;
				return Double.toString(score);
			}
		}
		return "0.0";
		/*if(m_currentStr != contextStr)
		{
			m_currentStr = contextStr;
			Vector<String> tokenVector = GeneMentionTokenizer.getTokens(m_currentStr);
			m_currentCandidates = m_indexReader.searchIDs(tokenVector, 100);
		}
		
		BioNERCandidate[] candidates = m_currentCandidates;
		String idStr = candidate.getRecordID();
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecordID().equals(idStr))
			{
				return Double.toString(candidates[i].getScore());
			}
		}
		return "0.0";*/
		/*BioNERRecord record = candidate.getRecord();
		int min = EditDistance.getEditDistance(contextStr.toLowerCase().trim(), record.getSymbol().toLowerCase().trim());
		
		String[] synonyms = record.getSynonyms();
		if(synonyms != null)
		{
			for(int i=0; i<synonyms.length; i++)
			{
				int dis = EditDistance.getEditDistance(contextStr.toLowerCase().trim(), synonyms[i].toLowerCase().trim());
				if(dis<min)
				{
					min = dis;
				}
			}
		}
		return Integer.toString(min);*/
		/*double score=0.0;
		if(min==0) score=1.0;
		else if(min<=1) score=0.8;
		else if(min<=2) score=0.6;
		else if(min<=3) score=0.5;
		else if(min<=5) score=0.2;
		else if(min<=10) score=0.1;
		else score=0.0;
		return Double.toString(score);*/
		
	}
	
	protected HashMap<String, String> getMap(BioNERDocument document)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String outText = getTextOutsideBracket(sentence,entity);
				if(outText!=null && outText.length()>0)
				{
					map.put(entity.getText(), outText);
					map.put(outText, entity.getText());
				}
			}
		}
		return map;
	}
	
	private String getTextOutsideBracket(BioNERSentence sentence, BioNEREntity entity)
	{
		int indexEnd = entity.getTokenEndIndex();
		BioNERToken[] tokens = sentence.getTokens();
		int i=indexEnd+1;
		boolean isBeforeBracket = false;
		for(; i<=indexEnd+2 && i<tokens.length; i++)
		{
			if(tokens[i].getText().equals("(")) 
			{
				isBeforeBracket = true;
				break;
			}
		}
		if(!isBeforeBracket) return null;
		int begin = i+1;
		
		if(begin>=tokens.length) return null;
		
		int end = tokens.length-1;
		i++;
		for(;i<tokens.length; i++)
		{
			if(tokens[i].getText().equals(")"))
			{
				end = i-1;
				break;
			}
		}
		if(i<tokens.length) end = i;
		else end = tokens.length-1;
		/*String formerStr = sentence.getSentenceText().substring(tokens[begin].getBegin(), tokens[end].getEnd());
		String longForm = AbbreviationFinder.findBestLongForm(entity.getText(), formerStr);
		if(longForm!=null && longForm.contains(entity.getText())) return null;
		return longForm;*/
		String resultStr = sentence.getSentenceText().substring(tokens[begin].getBegin(), tokens[end].getEnd());
		if(resultStr.length()<50) return resultStr;
		return null;
	}
}
