package bioner.tools.nlptools;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.BC2GNDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;

public class CombinedNameRecognizer {
	private static BioNERDocument m_currentDocument = null;
	private static HashMap<String, String> m_currentMap = null;
	
	/**
	 * 
	 * @param document the document to find full names from.
	 * @return a hash map. The key is the short name, the value is its full name.
	 */
	public static HashMap<String, String> getCombinedNameMap(BioNERDocument document)
	{
		if(m_currentDocument==document) return m_currentMap;
		m_currentDocument=document;
		HashMap<String, String> map = new HashMap<String, String>();
		Pattern pattern = Pattern.compile("\\b[\\w\\-]+\\s?\\\\?\\/\\s?[\\w\\-]+\\b");
		BioNERSentence[] allSentences = document.getAbstractSentences();
		for(int i=0; i<allSentences.length; i++)
		{
			BioNERSentence sentence = allSentences[i];
			String text = sentence.getSentenceText();
			Matcher matcher = pattern.matcher(text);
			while(matcher.find())
			{
				String str = text.substring(matcher.start(), matcher.end());
				String[] parts = str.split("[\\\\\\/\\s]+");
				if(parts.length!=2) continue;
				map.put(parts[0], parts[1]);
				map.put(parts[1], parts[0]);
			}
		}
		
		
		m_currentMap = map;
		return map;
	}
	
	public static void main(String[] args)
	{
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder("../../BC2GN/data/testingData");
		BioNERDocument[] documents = docBuilder.buildDocuments();
		for(int i=0; i<documents.length; i++)
		{
			if(!documents[i].getID().equals("12878160")) continue;
			HashMap<String, String> map = getCombinedNameMap(documents[i]);
			System.out.println("ID:"+documents[i].getID());
			for(String shortName : map.keySet())
			{
				System.out.println(shortName+":"+map.get(shortName));
			}
			System.out.println();
		}
	}
}
