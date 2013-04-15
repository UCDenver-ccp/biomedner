////////////////////////////////////////////////////////
//Usage: This is a implement for TokenNormalizer. It use the most general method to normalize a token.
//Author: Liu Jingchen
//Date: 2009/12/2
////////////////////////////////////////////////////////
package bioner.tools.dictner;


import bioner.data.document.BioNERToken;
import bioner.tools.strnormal.StringNormalizer;
import bioner.tools.strnormal.StringNormalizerFactory;

public class GeneralTokenNormalize implements TokenNormalize {
	private StringNormalizer m_strNormalizer = StringNormalizerFactory.getStringNormalizer();
	
	@Override
	public void NormalizeToken(BioNERToken[] tokens) {
		// TODO Auto-generated method stub
		
		//Get lower case of original token for normalized token.
		for(BioNERToken token : tokens)
		{
			token.setNormalText(token.getText().toLowerCase());
		}
		replaceByRules(tokens);
	}
	
	private void replaceByRules(BioNERToken[] tokens)
	{
		for(BioNERToken token : tokens)
		{
			String tokStr = token.getNormalText();
			tokStr = m_strNormalizer.normalizeString(tokStr);
			token.setNormalText(tokStr);
		}
	}

}
