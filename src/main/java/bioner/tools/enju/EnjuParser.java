package bioner.tools.enju;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

public class EnjuParser {
	private static HashMap<String, Vector<EnjuRelation>> m_parsedResultMap = EnjuResultFileReader.readEnjuResultFiles();
	
	
	public static Vector<String> getGMAllRelatedWordTexts(BioNERSentence sentence, BioNEREntity entity)
	{
		Vector<EnjuWord> wordVector = getGMAllRelatedWords(sentence, entity);
		Vector<String> textVector = new Vector<String>();
		for(EnjuWord word : wordVector)
		{
			String text = word.getText();
			if(!textVector.equals(text))
				textVector.add(text);
		}
		return textVector;
	}
	public static Vector<EnjuWord> getGMAllRelatedWords(BioNERSentence sentence, BioNEREntity entity)
	{
		Vector<EnjuRelation> relationVector = m_parsedResultMap.get(sentence.getSentenceText());
		int begin = entity.getTokenBeginIndex();
		int end = entity.getTokenEndIndex();
		BioNERToken[] tokens = sentence.getTokens();
		Vector<EnjuWord> wordVector = new Vector<EnjuWord>();
		for(int i=begin; i<=end; i++)
		{
			Vector<EnjuWord> oneWordVector = getRelatedWordsForOneWord(relationVector, tokens[i].getText(), i);
			for(EnjuWord word : oneWordVector)
			{
				if(!wordVector.contains(word))
				{
					wordVector.add(word);
				}
			}
		}
		return wordVector;
	}
	
	private static Vector<EnjuWord> getRelatedWordsForOneWord_Header(Vector<EnjuRelation> relationVector, String wordText, int index)
	{
		Vector<EnjuWord> wordVector = new Vector<EnjuWord>();
		for(EnjuRelation relation : relationVector)
		{
			if(relationContains(relation, wordText, index))
			{
				if(relation.getArgWordNum()==1)
				{//two words relation
					if(!wordVector.contains(relation.getAllArgWords().elementAt(0)))
					{
						wordVector.add(relation.getAllArgWords().elementAt(0));
					}
				}
				else if(relation.getArgWordNum()==2)
				{//two arg words
					String verbText = relation.getCentralWord().getText();
					if(verbText.matches("is|are|was|were"))
					{
						if(!wordVector.contains(relation.getAllArgWords().elementAt(1)))
						{
							wordVector.add(relation.getAllArgWords().elementAt(1));
						}
					}
				}
			}
		}
		
		return wordVector;
	}
	
	
	private static Vector<EnjuWord> getRelatedWordsForOneWord(Vector<EnjuRelation> relationVector, String wordText, int index)
	{
		Vector<EnjuWord> wordVector = new Vector<EnjuWord>();
		for(EnjuRelation relation : relationVector)
		{
			if(relationContains(relation, wordText, index))
			{
				if(!wordVector.contains(relation.getCentralWord()))
				{
					wordVector.add(relation.getCentralWord());
				}
				for(EnjuWord argWord : relation.getAllArgWords())
				{
					if(!wordVector.contains(argWord))
						wordVector.add(argWord);
				}
			}
		}
		
		return wordVector;
	}
	private static boolean relationContains(EnjuRelation relation, String wordText, int index)
	{
		if(relation.getCentralWord().equals(wordText, index)) return true;
		for(EnjuWord argWord : relation.getAllArgWords())
		{
			if(argWord.equals(wordText, index)) return true;
		}
		return false;
	}
	
	public static void main(String[] args)
	{
		String sentenceText = "We show here that RNA helicase A ( RHA ) protein links BRCA1 to the holoenzyme complex .";
		BioNERSentence sentence = new BioNERSentence(sentenceText, 0);
		BioNEREntity entity = new BioNEREntity();
		entity.set_Sentence(sentence);
		entity.setTokenIndex(4, 6);
		Vector<EnjuWord> wordVector = getGMAllRelatedWords(sentence, entity);
		for(EnjuWord word : wordVector)
		{
			System.out.println(word.toString());
		}
	}
}
