package bioner.process.organismner;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.process.BioNERProcess;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictner.SentenceNER;
import bioner.tools.dictner.SentenceNERProcessFactory;

public class ProcessImpOrgnismNER implements BioNERProcess {

	private BioNERDictionary m_orgnismDict = OrgnismDictionaryBuilder.getDictionary();
	private SentenceNER m_sentencNER = SentenceNERProcessFactory.createSentenceNER(m_orgnismDict);
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		ProcessSentenceArray(document.getAllSentence());
		
	}
	private void ProcessSentenceArray(BioNERSentence[] sentenceArray)
	{
		for(BioNERSentence sentence : sentenceArray)
		{
			BioNERToken[] tokens = sentence.getTokens();
			
			BioNEREntity[] entities = m_sentencNER.SentenceBasedNER(sentence);
			//TimerSet.PrintOut();
			for(BioNEREntity entity : entities)
			{
				sentence.addEntity(entity);
			}
		}
	}

}
