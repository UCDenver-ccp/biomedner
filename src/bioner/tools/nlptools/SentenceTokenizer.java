////////////////////////////////////////////////////////
//Usage: This is a interface for tokenizer.
//Author: Liu Jingchen
//Date: 2009/12/24
////////////////////////////////////////////////////////
package bioner.tools.nlptools;

public interface SentenceTokenizer {
	public abstract String[] tokenize(String sentence);
}
