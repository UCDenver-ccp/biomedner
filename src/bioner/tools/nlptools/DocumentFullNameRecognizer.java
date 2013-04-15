package bioner.tools.nlptools;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.BC2GNDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.normalization.feature.builder.AbbreviationFinder;
/**
 * For one document, get all possible short name--full name pairs.
 * @author Liu Jingchen
 *
 */
public class DocumentFullNameRecognizer {
	
	private static BioNERDocument m_currentDocument = null;
	private static HashMap<String, String> m_currentMap = null;
	
	/**
	 * 
	 * @param document the document to find full names from.
	 * @return a hash map. The key is the short name, the value is its full name.
	 */
	public static HashMap<String, String> getFullNameMap(BioNERDocument document)
	{
		if(m_currentDocument==document) return m_currentMap;
		m_currentDocument=document;
		HashMap<String, String> map = new HashMap<String, String>();
		
		Vector<String> abbVector = new Vector<String>();
		
		//Match the words having more than one upper case, or in bracket.
		Pattern pattern = Pattern.compile("\\b[\\w\\-]*[A-Z]+[\\w\\-]*\\b|\\([^\\(^\\)]+\\)");
		BioNERSentence[] allSentences = document.getAbstractSentences();
		for(int i=0; i<allSentences.length; i++)
		{
			BioNERSentence sentence = allSentences[i];
			if(sentence==document.getTitle()) continue;
			if(sentence.getParagraph()==null) continue;
			String text = sentence.getSentenceText();
			Matcher matcher = pattern.matcher(text);
			while(matcher.find())
			{
				String abbStr = text.substring(matcher.start(), matcher.end());
				abbStr = abbStr.replaceAll("\\(|\\)", "").trim();
				if(!abbVector.contains(abbStr))
				{//Only check for full names when the short name first appears.
					abbVector.add(abbStr);
					String longName = null;
					int pos = matcher.end();
					if(pos<text.length()-3)
					{
						String behindText = text.substring(pos+1, pos+3);
						if(behindText.contains("("))
						{//There is a ( within the 3 chars following the gene mention
							//Find the positions of ( and )
							int pos_begin = text.indexOf('(', pos+1);
							int pos_end = text.indexOf(')', pos+1);
							if(pos_begin>0 && pos_end>0 && pos_end>pos_begin)
							{//if the positions are correct
								String textInBracket = text.substring(pos_begin, pos_end).trim();
								longName = AbbreviationFinder.findBestLongForm(abbStr, textInBracket);
								if(longName!=null && isCorrectFullName(abbStr, longName))
								{
									map.put(abbStr, longName);
									map.put(longName, abbStr);
								}//if(longName!=null && isCorrectFullName(abbStr, longName))
							}//if(pos_begin>0 && pos_end>0 && pos_end>pos_begin)
						}//if(behindText.contains("("))
					}
					
					
					if(longName==null || !isCorrectFullName(abbStr, longName))
					{
						int formerEndPos = matcher.start();
						int formerBeginPos = getFormTextBeginPosition(sentence, formerEndPos);
						String formerText = text.substring(formerBeginPos, formerEndPos);
						longName = AbbreviationFinder.findBestLongForm(abbStr, formerText);
						if(longName!=null && isCorrectFullName(abbStr, longName))
						{
							map.put(abbStr, longName);
							map.put(longName, abbStr);
						}
					}
					
				}//if(!abbVector.contains(abbStr))
			}//while
		}
		m_currentMap = map;
		return map;
	}
	
	public static int getFormTextBeginPosition(BioNERSentence sentence, int endPos)
	{
		int beginPos=0;
		int endIndex = sentence.getTokenIndex(endPos);
		BioNERToken[] tokens = sentence.getTokens();
		for(int i=endIndex-1; i>=0; i--)
		{
			String tokenStr = tokens[i].getText();
			if(tokenStr.matches("\\,|\\.|\\;|\\:|and|or|of|with|by|to|from|without|about|in|at|for|on|before|after|above|bellow"))
			{
				beginPos = tokens[i].getEnd();
				break;
			}
		}
		return beginPos;
	}
	
	
	public static boolean isCorrectFullName(String shortName, String fullName)
	{
		String[] words = fullName.split("\\W+");
		if(words.length > shortName.length()+3) return false;
		return true;
	}
	public static void main(String[] args)
	{
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder("../../BC2GN/data/testingData");
		BioNERDocument[] documents = docBuilder.buildDocuments();
		for(int i=0; i<documents.length; i++)
		{
			if(!documents[i].getID().equals("11891219")) continue;
			HashMap<String, String> map = getFullNameMap(documents[i]);
			System.out.println("ID:"+documents[i].getID());
			for(String shortName : map.keySet())
			{
				System.out.println(shortName+":"+map.get(shortName));
			}
			System.out.println();
		}
	}
}
