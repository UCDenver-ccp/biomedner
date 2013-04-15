package crf.featurebuild;

import bioner.data.document.BioNERDocument;

public class FeatureBuildDocumentThreadArranger {
	private BioNERDocument[] m_docArray;
	
	private int m_currentIndex;
	
	public FeatureBuildDocumentThreadArranger(BioNERDocument[] documents)
	{
		m_docArray = documents;
		
		m_currentIndex = 0;
	}
	public BioNERDocument getDocument(int num)
	{
		if(num>=m_docArray.length) return null;
		BioNERDocument nextDoc = m_docArray[num];
		m_docArray[num] = null;
		return nextDoc;
	}
	
	synchronized public int getNextNum()
	{
		int num = m_currentIndex;
		m_currentIndex++;
		return num;
	}
}
