package bioner.data.document;

import java.util.Vector;

import bioner.tools.dictner.SentenceNERProcessFactory;
import bioner.tools.dictner.TokenNormalize;
import bioner.tools.dictner.Tokenizer;
/**
 *  This class store one sentence from one document. It also stores the entities found from it.
 * @author Liu Jingchen
 *
 */
public class BioNERSentence {
	private String m_text=null;//The text of this sentence.
	private Vector<BioNEREntity> m_entityVector = new Vector<BioNEREntity>();//The set of entities found from this sentence.
	
	private BioNERDocument m_document = null;
	private BioNERSection m_section = null;
	private int m_Begin; 
	private int m_End;
	private int m_DocBegin;
	private int m_DocEnd;
	private int m_Size;
	private BioNERToken[] m_tokens=null;
	
	private static Tokenizer m_tokenizer = SentenceNERProcessFactory.createTokenizer();
	private static TokenNormalize m_tokenNormalizer = SentenceNERProcessFactory.createrTokenNormalize();
	
	private BioNERParagraph m_paragraph = null;

	/**
	 * @deprecated use the constructor that includes the beginning relative to the document
     */
	@Deprecated	
	public BioNERSentence(String sentence, int begin)
	{
		setSentenceText(sentence);
		this.m_Begin = begin;
		this.m_Size = this.m_text.length();
		this.m_End = this.m_Begin + this.m_Size;
	}
	
	public BioNERSentence(String sentence, int begin, int docBegin) {
		setSentenceText(sentence);

		this.m_Size = this.m_text.length();

		this.m_Begin = begin;
		this.m_End = this.m_Begin + this.m_Size;

		this.m_DocBegin = docBegin;
		this.m_DocEnd = this.m_DocBegin + this.m_Size;
	}
	
	public void setSentenceText(String text)
	{
		this.m_text = text;
		m_tokens = m_tokenizer.Tokenize(this);
		if(m_tokens != null)
		{
			m_tokenNormalizer.NormalizeToken(m_tokens);
		}
	}
	public String getSentenceText()
	{
		return this.m_text;
	}
	//Add and get entities.
	public void addEntity(BioNEREntity entity)
	{
		if (!this.m_entityVector.contains(entity))
		{
			this.m_entityVector.add(entity);
		}
	}
	public BioNEREntity[] getAllEntities()
	{
		int size = this.m_entityVector.size();
		BioNEREntity[] entities = new BioNEREntity[size];
		for(int i=0; i<size; i++)
		{
			entities[i] = this.m_entityVector.elementAt(i);
		}
		return entities;
	}
	
	//Getters for position info.
	/**
	 * This is the beginning of the sentence relative to the paragraph.
     */
	public int getBegin() { return this.m_Begin; }

	/**
	 * This is the end of the sentence relative to the paragraph.
     */
	public int getEnd() { return this.m_End; }

	public int getLength() { return this.m_Size; }

	/**
	 * This is the beginning of the sentence relative to the document.
     */
	public int getDocBegin() { return this.m_DocBegin; }

	/**
	 * This is the end of the sentence relative to the document.
     */
	public int getDocEnd() { return this.m_DocEnd; }
	
	
	
	public String toString()
	{
		String str = this.m_text+"|"+this.m_Begin+"|"+this.m_End;
		if(this.m_entityVector.size()>0) 
		{
			str += "\nEntities:";
			for(BioNEREntity entity : this.m_entityVector)
			{
				str += "\n"+entity.toString();
			}
		}
		return str;
	}

	public void setTokens(BioNERToken[] m_tokens) {
		this.m_tokens = m_tokens;
	}

	public BioNERToken[] getTokens() {
		return m_tokens;
	}
	
	public void clearEntities()
	{
		this.m_entityVector.clear();
	}
	public void clear()
	{
		m_tokens = null;
		this.m_entityVector.clear();
	}
	
	/**
	 * Get the index of token in this sentence, according to a position in the sentence.
	 * @param pos the position in the sentence
	 * @return the token index which the position belongs to
	 */
	public int getTokenIndex(int pos)
	{
		for(int i=0; i<m_tokens.length-1; i++)
		{
			if(m_tokens[i].getBegin()<=pos && m_tokens[i+1].getBegin()>pos)
			{
				return i;
			}
		}
		return m_tokens.length-1;
	}

	public void setParagraph(BioNERParagraph paragraph) {
		this.m_paragraph = paragraph;
	}

	public BioNERParagraph getParagraph() {
		return m_paragraph;
	}
	
	public void linkComponent()
	{
		for(BioNEREntity entity : this.m_entityVector)
		{
			entity.set_Sentence(this);
		}
	}

	public void setDocument(BioNERDocument document) {
		this.m_document = document;
	}


	public void setSection(BioNERSection section) {
		this.m_section = section;
	}

	
	
	public BioNERSection getSection()
	{
		if(this.m_section  != null) return this.m_section;
		BioNERParagraph paragraph = getParagraph();
		if(paragraph != null) return paragraph.getSection();
		return null;
	}
	public BioNERDocument getDocument()
	{
		if(this.m_document != null) return this.m_document;
		BioNERSection section = getSection();
		if(section!=null) return section.getDocument();
		return null;
	}
}
