package bioner.application.api;

import bioner.data.document.BioNERDocument;
import bioner.global.GlobalConfig;

public class BioNERApplication {
	private BioNERDocumentBuilder m_documentBuilder = null;
	private BioNERDocumentOutput m_output = null;
	private BioNERProcessFactory m_processFactory = null;
	
	public BioNERApplication(BioNERDocumentBuilder documentBuilder, BioNERDocumentOutput output, BioNERProcessFactory processFactory)
	{
		m_documentBuilder = documentBuilder;
		m_output = output;
		m_processFactory = processFactory;
		GlobalConfig.ReadConfigFile();
	}
	public void run()
	{
		int threadNum = GlobalConfig.THREAD_NUM;
		run(threadNum);
	}
	public void run(int threadNum)
	{
		long beginTime = System.currentTimeMillis();
		BioNERDocument[] documents = m_documentBuilder.buildDocuments();
		Thread[] threads = new Thread[threadNum];
		DocumentThreadArranger threadArranger = new DocumentThreadArranger(documents);
		m_output.init();
		for(int i=0; i<threadNum; i++)
		{
			Runnable run = new ProcessDocumentTread(threadArranger, m_processFactory, m_output);
			threads[i] = new Thread(run);
			
		}
		for(int i=0; i<threadNum; i++)
		{
			try {
				threads[i].start();
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_output.close();
		long endTime = System.currentTimeMillis();
		long time = endTime - beginTime;
		System.out.println();
		System.out.println("Done! Total time used:"+time+"ms");
	}
}
