package bioner.data.document;


import java.util.Vector;
/**
 * This class is used to store a document, including its abstract and every sections in full text. 
 * @author Liu Jingchen
 * 
 
 */
public class BioNERDocument {
	
	private BioNERSection m_abstractSection = null;
	private Vector<BioNERSection> m_sectionVector = new Vector<BioNERSection>();
	private String m_id = null;
	private BioNERSentence m_titleSentence = null;
	public BioNERDocument()
	{
		
	}
	
	public void setAbstractSection(BioNERSection abstractSection)
	{
		m_abstractSection = abstractSection;
		m_abstractSection.setDocument(this);
	}
	public BioNERSection getAbstractSection()
	{
		return m_abstractSection;
	}
	
	public void addSection(BioNERSection section)
	{
		section.setDocument(this);
		m_sectionVector.add(section);
	}
	
	/**
	 * Get all sentences in one document. Including all sentences in every section.
	 * @return An array of BioNERSentence
	 */
	public BioNERSentence[] getAllSentence()
	{
		Vector<BioNERSentence> sentenceVector = new Vector<BioNERSentence>();
		
		
		if(m_titleSentence != null)
		{
			sentenceVector.add(m_titleSentence);
		}
		//Get sentences from abstract
		if(m_abstractSection != null)
		{
			Vector<BioNERSentence> sentenceParaVector = m_abstractSection.getAllSentence();
			for(int i=0; i<sentenceParaVector.size(); i++)
			{
				sentenceVector.add(sentenceParaVector.elementAt(i));
			}
		}
		
		//Get all sentences from all sections.
		// BUG? does it miss the title sentence?
		for(int i=0; i<m_sectionVector.size(); i++)
		{
			Vector<BioNERSentence> sentenceParaVector = m_sectionVector.elementAt(i).getAllSentence();
			for(int j=0; j<sentenceParaVector.size(); j++)
			{
				sentenceVector.add(sentenceParaVector.elementAt(j));
			}
		}
	
		return sentenceVector.toArray(new BioNERSentence[0]);	
	}
	
	/**
	 * Get all sentences from abstract.
	 * @return An array of BioNERSentence
	 */
	public BioNERSentence[] getAbstractSentences()
	{
		if(m_abstractSection==null) return new BioNERSentence[]{};
		Vector<BioNERSentence> sentenceVector = new Vector<BioNERSentence>();
		Vector<BioNERSentence> sentenceParaVector = m_abstractSection.getAllSentence();
		
		//Get sentences from abstract
		for(int i=0; i<sentenceParaVector.size(); i++)
		{
			sentenceVector.add(sentenceParaVector.elementAt(i));
		}
	
		return sentenceVector.toArray(new BioNERSentence[0]);	
	}
	
	/**
	 * Get all sentences from sections in full text, apart from abstract.
	 * @return An array of BioNERSentence
	 */
	public BioNERSentence[] getFullTextSentences()
	{
		Vector<BioNERSentence> sentenceVector = new Vector<BioNERSentence>();
		Vector<BioNERSentence> sentenceParaVector = m_abstractSection.getAllSentence();
		
		//Get all sentences from all sections.
		for(int i=0; i<m_sectionVector.size(); i++)
		{
			sentenceParaVector = m_sectionVector.elementAt(i).getAllSentence();
			for(int j=0; j<sentenceParaVector.size(); j++)
			{
				sentenceVector.add(sentenceParaVector.elementAt(j));
			}
		}
		
		//Copy to Array.
		int size = sentenceVector.size();
		BioNERSentence[] sentences = new BioNERSentence[size];
		for(int i=0; i<size; i++)
		{
			sentences[i] = sentenceVector.elementAt(i);
		}
		return sentences;
	}

	public void setID(String id) {
		this.m_id = id;
	}

	public String getID() {
		return m_id;
	}

	public void setTitle(BioNERSentence title) {
		this.m_titleSentence = title;
		this.m_titleSentence.setDocument(this);
	}
	
	public void setTitle(String title) {
		this.m_titleSentence = new BioNERSentence(title,0);
		this.m_titleSentence.setDocument(this);
	}

	public BioNERSentence getTitle() {
		return m_titleSentence;
	}
	public void linkComponent()
	{
		if(m_abstractSection != null)
		{
			m_abstractSection.setDocument(this);
			m_abstractSection.linkComponent();
		}
		for(BioNERSection section : this.m_sectionVector)
		{
			section.setDocument(this);
			section.linkComponent();
		}
	}
	
	public Vector<BioNEREntity> getAllEntity()
	{
		Vector<BioNEREntity> entityVector = new Vector<BioNEREntity>();
		for(BioNERSentence sentence : this.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				entityVector.add(entity);
			}
		}
		
		return entityVector;
	}
	public Vector<BioNERSection> getSections()
	{
		return m_sectionVector;
	}
}
