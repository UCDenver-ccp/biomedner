package bioner.tools.dictner;

import java.util.Vector;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.normalization.GeneMentionTokenizer;
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.SentenceTokenizer;

public class OpenNLPTokenizer implements Tokenizer {

	private SentenceTokenizer m_tokenizer = NLPToolsFactory.getTokenizer();
	private Vector<Character> specailCharVector = new Vector<Character>();
	public OpenNLPTokenizer()
	{
		specailCharVector.add('-');
		specailCharVector.add('_');
		specailCharVector.add('/');
		specailCharVector.add('\\');
		specailCharVector.add('~');
		specailCharVector.add(',');
		specailCharVector.add('(');
		specailCharVector.add(')');
		specailCharVector.add('[');
		specailCharVector.add(']');
		specailCharVector.add('{');
		specailCharVector.add('}');
		specailCharVector.add('.');
		specailCharVector.add(':');
		specailCharVector.add(';');
		specailCharVector.add('+');
		specailCharVector.add('&');
		specailCharVector.add('*');
	}
	
	
	@Override
	public BioNERToken[] Tokenize(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		String sentenceStr = sentence.getSentenceText();
		String[] tokenStrs = m_tokenizer.tokenize(sentenceStr);
		tokenStrs = SpliteSpecialCharactor(tokenStrs);
		Vector<BioNERToken> tokenVector = new Vector<BioNERToken>();
		int currentPos = 0;
		for(int i=0; i<tokenStrs.length; i++)
		{
			int pos = sentenceStr.indexOf(tokenStrs[i], currentPos);
			int end = pos + tokenStrs[i].length()-1;
			BioNERToken token = new BioNERToken(sentence,pos,end);
			tokenVector.add(token);
			
			currentPos = end;
		}
		int size = tokenVector.size();
		BioNERToken[] tokens = new BioNERToken[size];
		for(int i=0;i<size; i++)
		{
			tokens[i] = tokenVector.elementAt(i);
		}
		return tokens;
	}
	
	//The result from OpenNLP is not satisfying for our system. It can't split '-'. So we have to process it here.
	private String[] SpliteSpecialCharactor(String[] oriTokens)
	{
		Vector<String> newTokenVector = new Vector<String>();
		for(int i=0; i<oriTokens.length; i++)
		{
			Vector<String> newTokensOneOriTok = SpliteSpecialCharactorOneToken(oriTokens[i]);
			for(int j=0; j<newTokensOneOriTok.size(); j++)
			{
				String newToken = newTokensOneOriTok.elementAt(j);
				if(newToken.length()>0&&!newToken.equals("~"))
				{
					newTokenVector.add(newToken);
				}
			}
		}
		int size = newTokenVector.size();
		String[] newTokens = new String[size];
		for(int i=0; i<size; i++)
		{
			newTokens[i] = newTokenVector.elementAt(i);
		}
		return newTokens;
	}
	private Vector<String> SpliteSpecialCharactorOneToken(String oriToken)
	{
		Vector<String> newTokensVector = new Vector<String>();
		
		int length = oriToken.length();
		int beginPos = 0;
		for(int i=0; i<length; i++)
		{
			Character currentChar = oriToken.charAt(i);
			if(this.specailCharVector.contains(currentChar))
			{
				String newToken = oriToken.substring(beginPos, i);
				
				for(String tokenParts : SplitePartsOneToken(newToken))
				{
					newTokensVector.add(tokenParts);
				}
				
				newToken = currentChar.toString();
				newTokensVector.add(newToken);
				
				beginPos = i+1;
			}
		}
		if(beginPos<oriToken.length())
		{
			String newToken = oriToken.substring(beginPos);
			for(String tokenParts : SplitePartsOneToken(newToken))
			{
				newTokensVector.add(tokenParts);
			}
		}
		return newTokensVector;
	}
	private Vector<String> SplitePartsOneToken(String oriToken)
	{
		/*Vector<String> newTokenVector = new Vector<String>();
		int begin = 0;
		while(begin>=0)
		{
			int pos = GeneMentionTokenizer.getNextSplitPostion(oriToken, begin);
			if(pos<0) break;
			String tokenStr = oriToken.substring(begin, pos);
			//if(!tokenVector.contains(tokenStr))
				newTokenVector.add(tokenStr.trim());
			begin = pos;
		}
		return newTokenVector;*/
		Vector<String> newTokenVector = new Vector<String>();
		newTokenVector.add(oriToken);
		return newTokenVector;
	}
	
	public static void main(String[] args)
	{
		OpenNLPTokenizer tokenizer = new OpenNLPTokenizer();
		String sentenceStr = "The abcGh01ex yu-78i oi/p 3.14.";
		BioNERSentence sentence = new BioNERSentence(sentenceStr,0);
		BioNERToken[] tokens = tokenizer.Tokenize(sentence);
		for(int i=0; i<tokens.length; i++)
		{
			System.out.print(tokens[i].getText()+" ");
		}
		
	}

}
