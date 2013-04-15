package bioner.application.api;

import bioner.data.document.BioNERDocument;



public class DocumentThreadArranger {
	private BioNERDocument[] m_docArray;
	private int m_currentIndex;
	
	public DocumentThreadArranger(BioNERDocument[] documents)
	{
		m_docArray = documents;
		m_currentIndex = 0;
	}
	synchronized public BioNERDocument getNextDocument()
	{
		if(m_currentIndex>=m_docArray.length) return null;
		BioNERDocument nextDoc = m_docArray[m_currentIndex];
		m_docArray[m_currentIndex] = null;
		m_currentIndex++;
		return nextDoc;
	}
	synchronized public int getNextNum()
	{
		return m_currentIndex;
	}
}
