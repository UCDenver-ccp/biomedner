package bioner.application.api;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.process.BioNERProcess;


public class ProcessDocumentTread implements Runnable {

	private DocumentThreadArranger m_DocArranger = null;
	private BioNERProcess[] processPipeline = null;
	private BioNERDocumentOutput m_output = null;
	public ProcessDocumentTread(DocumentThreadArranger DocArranger, BioNERProcessFactory processFactory,  BioNERDocumentOutput output)
	{
		m_DocArranger = DocArranger;
		m_output = output;
		processPipeline = processFactory.buildProcessPipeline();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			BioNERDocument document = m_DocArranger.getNextDocument();
			if(document==null) return;
			
			int num = m_DocArranger.getNextNum();
			String id = document.getID();
			long beginTime = System.currentTimeMillis();
			System.out.println("Begin to process #"+num+" document "+id);
			for(int i=0; i<processPipeline.length; i++)
			{
				processPipeline[i].Process(document);
			}
			m_output.outputDocument(document);
			System.out.println("Finished processing #"+num+" document "+id);
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			clearDocEntity(document);
			
			System.out.println("time used:"+time+"ms");
		}
	}
	
	private void clearDocEntity(BioNERDocument doc)
	{
		for(BioNERSentence sentence : doc.getAbstractSentences())
		{
			sentence.clearEntities();
		}
		for(BioNERSentence sentence : doc.getFullTextSentences())
		{
			sentence.clearEntities();
		}
	}

}
