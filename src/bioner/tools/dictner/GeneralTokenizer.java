////////////////////////////////////////////////////////
//Usage: This is a implement for Tokenizer. It use the most general method to tokenize a sentence.
//Author: Liu Jingchen
//Date: 2009/12/2
////////////////////////////////////////////////////////
package bioner.tools.dictner;

import java.util.Vector;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

public class GeneralTokenizer implements Tokenizer {

	@Override
	public BioNERToken[] Tokenize(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		String sentenceText = sentence.getSentenceText();
		Vector<BioNERToken> tokenVector = new Vector<BioNERToken>();
		String[] tokenStrs = sentenceText.split("\\s+|\\-+|\\\\+|\\/+|\\,+|\\||\\(|\\)|\\~|\\,|\\:");
		int currentPos = 0;
		for(int i=0; i<tokenStrs.length; i++)
		{
			int pos = sentenceText.indexOf(tokenStrs[i], currentPos);
			int end = pos + tokenStrs[i].length();
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

}
