//////////////////////////////////////////////////////
//Usage: This is a factory class for SentenceNER, Tokenizer and TokenNormalize.
//Author: Liu Jingchen
//Date: 2009/12/2
//////////////////////////////////////////////////////
package bioner.tools.dictner;

import bioner.tools.dictionary.BioNERDictionary;

public class SentenceNERProcessFactory {
	
	static public SentenceNER createSentenceNER(BioNERDictionary dict)
	{
		return new DictionaryBasedNER(dict);
		
	}
	static public Tokenizer createTokenizer()
	{
		//return new GeneralTokenizer();
		return new OpenNLPTokenizer();
	}
	static public TokenNormalize createrTokenNormalize()
	{
		return new GeneralTokenNormalize();
	}

}
