package bioner.application.webtool;

import bioner.application.api.ProcessDocumentTread;
import bioner.normalization.data.database.DatabaseConfig;

public class RunAllJSON {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long beginTime = System.currentTimeMillis();
		
		DatabaseConfig.ReadConfigFile();
		int threadNum = DatabaseConfig.THREAD_NUM;
		DocumentThreadArranger arranger = new DocumentThreadArranger(DatabaseConfig.DATAFILE_DIR);
		Thread[] threads = new Thread[threadNum];
		for(int i=0; i<threadNum; i++)
		{
			Runnable run = new RunJSONThread(arranger, i+1);
			threads[i] = new Thread(run);
			threads[i].start();
		}
		for(int i=0; i<threadNum; i++)
		{
			try {
				
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		long endTime = System.currentTimeMillis();
		long time = endTime - beginTime;
		System.err.println();
		System.err.println("Done! Total time used:"+time+"ms");
	}

}
