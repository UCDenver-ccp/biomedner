package bioner.tools.nlptools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class StandFordNLPTool implements TokenPOSTagger, SentenceSpliter {

	private static MaxentTagger tagger = null;
	
	public StandFordNLPTool()
	{
		try {
			if(tagger==null)
			{
				tagger = new MaxentTagger("./models/left3words-wsj-0-18.tagger");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	synchronized public String[] POSTag(String[] tokens) {
		// TODO Auto-generated method stub
		int size = tokens.length;
		Sentence<Word> sentence = new Sentence<Word>();
		for(int i=0; i<size; i++)
		{
			Word word = new Word(tokens[i]);
			sentence.add(word);
		}
		Sentence<TaggedWord> tSentence = MaxentTagger.tagSentence(sentence);
		String[] tags = new String[size];
		for(int i=0; i<tSentence.size(); i++)
		{
			TaggedWord tWord = tSentence.get(i);
			tags[i] = tWord.tag();
		}
		return tags;
	}

	@Override
	public String[] sentenceSplit(String paragraph) {
		// TODO Auto-generated method stub
		List<Sentence<? extends HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new StringReader(paragraph)));
		String[] sentenceArray = new String[sentences.size()];
		for(int i=0; i<sentences.size(); i++)
		{
			sentenceArray[i] = sentences.get(i).toString();
			sentenceArray[i] = sentenceArray[i].replaceAll("\\-LRB\\-", "(");
			sentenceArray[i] = sentenceArray[i].replaceAll("\\-RRB\\-", ")");
		}
		return sentenceArray;
	}

}
