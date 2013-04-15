/////////////////////////////////////////////////////////
//Usage: This class presents a token in one sentence. It stores the position info for this token.
//Author:Liu Jingchen
//Date:2009/12/2
/////////////////////////////////////////////////////////
package bioner.data.document;

import java.util.Vector;

import bioner.data.document.BioNERSentence;
import bioner.tools.dictionary.BioNERTerm;

public class BioNERToken {
	private String m_text;			//Original text
	private String m_normalText;	//Normalized text
	
	private BioNERSentence m_sentence;
	
	private int m_begin;
	private int m_end;
	private int m_size;
	
	private String m_lable = "O";
	
	//Store the terms found in the dictionary. These information might be used in the following CRF process as its features.
	private Vector<BioNERTerm> m_termVector = new Vector<BioNERTerm>();
	
	public BioNERToken(BioNERSentence sentence, int begin, int end)
	{
		this.m_begin = begin;
		this.m_end = end;
		this.m_size = end-begin+1;
		this.m_sentence = null;
		this.m_text = sentence.getSentenceText().substring(begin, end+1);
	}
	
	public String getText() {
		return m_text;
	}
	/*public BioNERSentence getSentence() {
		return m_sentence;
	}*/
	public int getBegin() {
		return m_begin;
	}
	public int getEnd() {
		return m_end;
	}
	public int getSize() {
		return m_size;
	}

	public void setNormalText(String m_normalText) {
		this.m_normalText = m_normalText;
	}

	public String getNormalText() {
		return m_normalText;
	}
	
	public void addTerm(BioNERTerm term)
	{
		m_termVector.add(term);
	}
	public Vector<BioNERTerm> getTermVector()
	{
		return m_termVector;
	}
	public BioNERTerm[] getTermArray()
	{
		int size = m_termVector.size();
		BioNERTerm[] terms = new BioNERTerm[size];
		for(int i=0; i<size; i++)
		{
			terms[i] = m_termVector.elementAt(i);
		}
		return terms;
	}

	public void setLable(String m_lable) {
		this.m_lable = m_lable;
	}

	public String getLable() {
		return m_lable;
	}
	
}
