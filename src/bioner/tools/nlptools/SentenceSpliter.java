////////////////////////////////////////////////////////////
//Usage: A interface for sentence spliting.
//Author: Liu Jingchen
//Date: 2009/12/7
////////////////////////////////////////////////////////////
package bioner.tools.nlptools;

public interface SentenceSpliter {
	public abstract String[] sentenceSplit(String paragraph);
}
