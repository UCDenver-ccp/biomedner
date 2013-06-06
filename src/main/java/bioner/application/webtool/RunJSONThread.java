package bioner.application.webtool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunJSONThread implements Runnable {

	private DocumentThreadArranger m_arranger;
	private int m_num;
	public RunJSONThread(DocumentThreadArranger arranger, int num)
	{
		m_arranger = arranger;
		m_num = num;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		String pmcid = m_arranger.getNextID();
		int num = m_arranger.getNextNum();
		int restartTime = 0;
		while(pmcid!=null)
		{
			long beginTime = System.currentTimeMillis();
			System.err.println("Thread "+m_num+" Begin to process #"+num+" "+pmcid);
			Runtime run = Runtime.getRuntime();
			String cmd = "java -Djava.library.path=. -jar webtool.jar "+pmcid;
			try {
				Process p = run.exec(cmd);
				BufferedInputStream in = new BufferedInputStream(p.getInputStream());  
				BufferedReader inReader = new BufferedReader(new InputStreamReader(in));  
				String line;
				line = inReader.readLine();
				long endTime = System.currentTimeMillis();
				long time = endTime - beginTime;
				if(line!=null)
				{
					if(line.length()>2)
						System.err.println("Thread "+m_num+" End processing #"+num+" "+pmcid+" time: "+time+"ms");
					else
						System.err.println("Thread "+m_num+" End processing #"+num+" "+pmcid+" time: "+time+"ms"+" empty reuslt:"+line);
				}
				else
				{
					System.err.println("Thread "+m_num+" Error in processing #"+num+" "+pmcid+" time: "+time+"ms");
				}
				if(time>10000)
				{
					m_arranger.setSpentTime(time);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(restartTime>60) 
					e.printStackTrace();
				else
				{
					System.err.println("Thread "+m_num+" Error in processing #"+num+" "+pmcid);
					System.err.println("Restart in 10 seconds.");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					restartTime++;
					System.err.println("Thread "+m_num+" Restart #"+num+" "+pmcid+" for the "+restartTime+" Time");
					continue;
				}
			}
			pmcid = m_arranger.getNextID();
			num = m_arranger.getNextNum();
			restartTime = 0;
		}
	}

}
