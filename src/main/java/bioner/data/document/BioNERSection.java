package bioner.data.document;

import java.util.Vector;

/**
 * This class store a section of the full text article. It contains its sub sections, and a set of paragraph.
 * @author Liu Jingchen
 *
 */
public class BioNERSection {
	private BioNERSentence m_titleSentence = null;
	private Vector<BioNERSection> m_subSectionVector = new Vector<BioNERSection>();
	private Vector<BioNERParagraph> m_paragraphVector = new Vector<BioNERParagraph>();
	private BioNERDocument m_document = null;
	private BioNERSection m_parentSection = null;
	private String m_type = null;
	private int currentDocLength=0;

	public BioNERSection() {}
	public BioNERSection(int currentDocLength) {
		this.currentDocLength = currentDocLength;
	}

	public void setTitleSentence(BioNERSentence titleSentence) {
		this.m_titleSentence = titleSentence;
		this.m_titleSentence.setSection(this);
	}
	public BioNERSentence getTitleSentence() {
		return m_titleSentence;
	}
	public void setSubSection(Vector<BioNERSection> subSectionVector) {
		this.m_subSectionVector = subSectionVector;
	}
	public Vector<BioNERSection> getSubSection() {
		return m_subSectionVector;
	}
	public void addSubSection(BioNERSection subSection)
	{
		subSection.setDocument(this.m_document);
		m_subSectionVector.add(subSection);
	}
	public void setParagraph(Vector<BioNERParagraph> paragraphVector) {
		this.m_paragraphVector = paragraphVector;
	}
	public void addParagraph(BioNERParagraph paragraph)
	{
		m_paragraphVector.add(paragraph);
	}
	public Vector<BioNERParagraph> getParagraph() {
		return m_paragraphVector;
	}
	
	/**
	 * Get all sentences in this section. All the sentences in the paragraph and the sub sections will be returned.
	 * @return A vector of BioNERSentence.
	 */
	public Vector<BioNERSentence> getAllSentence()
	{
		Vector<BioNERSentence> sentenceVector = new Vector<BioNERSentence>();
		//Get title sentence
		if(m_titleSentence != null)	sentenceVector.add(m_titleSentence);
		
		//Get sentences from paragraphs of this section.
		for(int i=0; i<m_paragraphVector.size(); i++)
		{
			Vector<BioNERSentence> sentenceParaVector = m_paragraphVector.elementAt(i).getSentence();
			for(int j=0; j<sentenceParaVector.size(); j++)
			{
				sentenceVector.add(sentenceParaVector.elementAt(j));
			}
		}
		//Get sentences of all sub sections.
		for(int i=0; i<m_subSectionVector.size(); i++)
		{
			Vector<BioNERSentence> sentenceParaVector = m_subSectionVector.elementAt(i).getAllSentence();
			for(int j=0; j<sentenceParaVector.size(); j++)
			{
				sentenceVector.add(sentenceParaVector.elementAt(j));
			}
		}
		return sentenceVector;
	}
	public void setDocument(BioNERDocument document) {
		this.m_document = document;
	}
	public BioNERDocument getDocument() {
		return m_document;
	}
	
	public void linkComponent()
	{
		for(BioNERParagraph paragraph : this.m_paragraphVector)
		{
			paragraph.setSection(this);
			paragraph.linkComponent();
		}
		for(BioNERSection subSection : this.m_subSectionVector)
		{
			subSection.setDocument(m_document);
			subSection.setType(m_type);
			subSection.linkComponent();
			subSection.setParentSection(this);
		}
	}
	public void setType(String type) {
		this.m_type = type;
	}
	public String getType() {
		return m_type;
	}
	public void setParentSection(BioNERSection m_parentSection) {
		this.m_parentSection = m_parentSection;
	}
	public BioNERSection getParentSection() {
		return m_parentSection;
	}

	public int getLength() {
		int length=0;
		for (BioNERSentence bns : getAllSentence()) {
			length += bns.getSentenceText().length();	
		}
		return length;
	}	
}
