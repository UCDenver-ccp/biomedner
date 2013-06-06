////////////////////////////////////////////////////////////////
//Usage: This is a interface for POS tagging.
//Author: Liu Jingchen
//Date: 2009/12/24
////////////////////////////////////////////////////////////////
package bioner.tools.nlptools;

public interface TokenPOSTagger {
	public abstract String[] POSTag(String[] tokens);
}
