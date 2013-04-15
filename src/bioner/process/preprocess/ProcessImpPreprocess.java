package bioner.process.preprocess;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.process.BioNERProcess;
import bioner.tools.dictner.SentenceNERProcessFactory;
import bioner.tools.dictner.TokenNormalize;
import bioner.tools.dictner.Tokenizer;


public class ProcessImpPreprocess implements BioNERProcess {
	private Tokenizer m_tokenizer = SentenceNERProcessFactory.createTokenizer();
	private TokenNormalize m_tokenNormalizer = SentenceNERProcessFactory.createrTokenNormalize();
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		ProcessSentenceArray(document.getAllSentence());
	}
	private void ProcessSentenceArray(BioNERSentence[] sentenceArray)
	{
		for(BioNERSentence sentence : sentenceArray)
		{
			BioNERToken[] tokens = m_tokenizer.Tokenize(sentence);
			if(tokens==null) continue;
			
			m_tokenNormalizer.NormalizeToken(tokens);

			sentence.setTokens(tokens);
		}
	}

}
