//////////////////////////////////////////////////////////////
//Usage: An implement for SentenceSpliter with OpenNLP tool.
//Author: Liu Jingchen
//Date: 2009/12/7
//////////////////////////////////////////////////////////////
package bioner.tools.nlptools;

import java.io.IOException;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.TokenizerME;

public class OpenNLPTool implements SentenceSpliter, TokenPOSTagger, SentenceTokenizer{
	private SentenceDetectorME sdetector;
	private POSTaggerME posTagger;
	private TokenizerME tokenizer;
	public OpenNLPTool()
	{
		try {
			this.sdetector = new SentenceDetector("models/sentdetect/EnglishSD.bin.gz");
			Dictionary dict = new Dictionary(false);
			this.posTagger = new PosTagger("models/postag/tag.bin.gz", dict);
			this.tokenizer = new Tokenizer("models/tokenize/EnglishTok.bin.gz");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	synchronized public String[] sentenceSplit(String paragraph) {
		// TODO Auto-generated method stub
		String[] sents = sdetector.sentDetect(paragraph);
		return sents;
	}

	@Override
	synchronized public String[] POSTag(String[] tokens) {
		// TODO Auto-generated method stub
		String[] tags = this.posTagger.tag(tokens);
		return tags;
	}

	@Override
	synchronized public String[] tokenize(String sentence) {
		// TODO Auto-generated method stub
		String[] tokens = this.tokenizer.tokenize(sentence);
		return tokens;
	}
}
