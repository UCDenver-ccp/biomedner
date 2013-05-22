package bioner.process.proteinner;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;
import bioner.tools.NERFactory;
import bioner.tools.NERProcessor;

import bioner.normalization.data.index.LuceneIndexNER;
import bioner.normalization.data.index.IndexConfig;

public class ProcessImpProteinIndexNER implements BioNERProcess {
	private NERProcessor m_ner = NERFactory.getGeneIndexNER();
 

	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : GetNERSentence.getNERSentence(document))
		{
			BioNEREntity[] entities = m_ner.recognizeSentence(sentence);
			for(BioNEREntity entity : entities)
			{
				///////if(entity.getText().matches(".{1,2}|[\\d\\.]+")) continue;
				entity.addLabel(GlobalConfig.ENTITY_LABEL_INDEX);
				sentence.addEntity(entity);
			}
		}
	}

}
