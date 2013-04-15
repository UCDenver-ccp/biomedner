package bioner.normalization.gmclassification.feature;

import java.util.HashMap;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.TokenPOSTagger;

public class PosTagStore {
	private static HashMap<BioNERSentence, String[]> map = new HashMap<BioNERSentence, String[]>();
	private static BioNERDocument m_currentDocument = null;
	private static TokenPOSTagger posTagger = NLPToolsFactory.getPOSTagger();
	public static String[] getPosTag(BioNERSentence sentence, BioNERDocument document)
	{
		if(m_currentDocument != document)
		{
			map.clear();
			m_currentDocument = document;
		}
		String[] tags = map.get(sentence);
		if(tags!=null) return tags;
		BioNERToken[] tokenArray = sentence.getTokens();
		String[] tokens = new String[tokenArray.length];
		for(int i=0; i<tokens.length; i++)
		{
			tokens[i] = tokenArray[i].getText();
		}
		tags = posTagger.POSTag(tokens);
		map.put(sentence, tags);
		return tags;
	}
}
