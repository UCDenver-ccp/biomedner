//////////////////////////////////////////////////////
//Usage: A factory class for nlp tools interfaces. To return a single object as possible.
//Author: Liu Jingchen
//Date:2009/12/7
//////////////////////////////////////////////////////
package bioner.tools.nlptools;

public class NLPToolsFactory {
	private static OpenNLPTool openNLPTool = new OpenNLPTool();
	private static StandFordNLPTool standfordNLPTool = new StandFordNLPTool();
	public static SentenceSpliter getSentenceSpliter()
	{
		return standfordNLPTool;
	}
	public static TokenPOSTagger getPOSTagger()
	{
		return standfordNLPTool;
	}
	public static SentenceTokenizer getTokenizer()
	{
		return openNLPTool;
	}
}
