package bioner.application.webtool;

import java.io.File;

import bioner.normalization.data.database.DatabaseConfig;




public class DocumentThreadArranger {
	private String[] m_pmcidArray;
	private int m_currentIndex;
	
	public DocumentThreadArranger(String dir)
	{
		File dirFile = new File(dir);
		File[] files = dirFile.listFiles();
		m_pmcidArray = new String[files.length];
		for(int i=0; i<m_pmcidArray.length; i++)
		{
			String filename = files[i].getName();
			int pos = filename.indexOf('.');
			String pmcid = filename.substring(0, pos);
			m_pmcidArray[i] = pmcid;
		}
	}
	synchronized public String getNextID()
	{
		if(m_currentIndex>=m_pmcidArray.length) return null;
		String nextID = m_pmcidArray[m_currentIndex];
		m_pmcidArray[m_currentIndex] = null;
		m_currentIndex++;
		return nextID;
	}
	synchronized public int getNextNum()
	{
		return m_currentIndex;
	}
	
	private double averageTime = 0.0;
	private int runDocNum = 0;
	synchronized public void setSpentTime(double time)
	{
		averageTime = (averageTime * runDocNum + time) / (runDocNum + 1);
		runDocNum++;
		double remainTime = (m_pmcidArray.length - m_currentIndex + 1) * averageTime / (double)DatabaseConfig.THREAD_NUM;
		int remainS = ((int)remainTime/1000) % 60;
		int timeTmp = (int)remainTime / 60000;
		int remainM = timeTmp % 60;
		timeTmp = timeTmp / 60;
		int remainH = timeTmp;
		System.err.println("Remain time: "+remainH+"h "+remainM+"m "+remainS+"s average time: "+averageTime+"ms");
	}
}
