//////////////////////////////////////////////////////////////////
//Usage: This is a interface for stemming. The input string should be a single word.
//Author: Liu Jingchen
//Date: 2010/1/23
//////////////////////////////////////////////////////////////////
package bioner.tools.nlptools;

public interface Stemmer {
	public abstract String stem(String word);
}
