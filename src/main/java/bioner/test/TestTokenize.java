package bioner.test;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.tools.dictionary.BioNERTerm;
import bioner.tools.dictner.SentenceNERProcessFactory;
import bioner.tools.dictner.TokenNormalize;
import bioner.tools.dictner.Tokenizer;

public class TestTokenize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tokenizer tokenizer = SentenceNERProcessFactory.createTokenizer();
		TokenNormalize tokenNormal = SentenceNERProcessFactory.createrTokenNormalize();
		BioNERTerm term = new BioNERTerm();
		term.setId("P28324");
		term.setDescribe("SAP-1~ETS domain-containing protein Elk-4~Serum response factor accessory protein 1~");
		
		//BioNERSentence termSentence = new BioNERSentence(term.getDescribe(),0);
		//BioNERToken[] tokens = tokenizer.Tokenize(termSentence);
		//tokenNormal.NormalizeToken(tokens);
		
		
		//String describ = term.getDescribe();
		/*BioNERSentence tempSentence = new BioNERSentence(describ,0);
		BioNERToken tempToken = new BioNERToken(tempSentence,0,tempSentence.getEnd());
		BioNERToken[] tempTokens = new BioNERToken[1];
		tempTokens[0] = tempToken;
		tokenNormal.NormalizeToken(tempTokens);
		describ = tempToken.getNormalText();
		term.setDescribe(describ);
		
		BioNERDictionary m_proteinDict = ProteinDictionaryBuilder.getProteinDictionary();
		BioNERTerm terms[] = m_proteinDict.getTermsByIndex("sap");
		for(BioNERTerm single_term : terms)
		{
			String id =single_term.getId();
			if(id.equals("P28324"))	System.out.println("Get P28324!");
		}*/
	}

}
