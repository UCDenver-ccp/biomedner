package bioner.data.document;

import java.util.Vector;

import bioner.normalization.data.BioNERCandidate;
/**
 * 
 * This class is used to store the NER result for one entity. Including the entity's text, begin/end index, length, etc.
 * @author Liu Jingchen
 * 
 */
public class BioNEREntity {
	//The position information:
	private int m_Begin=-1;
	private int m_End=-1;
	private int m_Size=-1;
	private String m_text = null;
	private BioNERSentence m_Sentence = null;//Store the sentence this entity come from.
	
	private String m_Type=null;
	
	private Vector<String> m_idVector = new Vector<String>();
	private int m_tokenBeginIndex = -1;
	private int m_tokenEndIndex = -1;
	private BioNERCandidate[] m_candidates = null;
	private double score = 0.0;
	//Getters.
	public int get_Begin() {
		return m_Begin;
	}
	public int get_End() {
		return m_End;
	}
	public int get_Size() {
		return m_Size;
	}
	public BioNERSentence get_Sentence() {
		return m_Sentence;
	}
	public String get_Type() {
		return m_Type;
	}
	
	//Setters.
	public void set_position(int begin, int end)
	{
		if(end<begin) 
		{
			Exception e = new Exception();
			e.printStackTrace();
			return;
		}
		this.m_Begin = begin;
		this.m_End = end;
		this.m_Size = end - begin;
		this.m_tokenBeginIndex = m_Sentence.getTokenIndex(m_Begin);
		this.m_tokenEndIndex = m_Sentence.getTokenIndex(m_End);
		this.setText();
	}
	public void set_Sentence(BioNERSentence sentence)
	{
		this.m_Sentence = sentence;
	}
	public void set_Type(String type)
	{
		this.m_Type = type;
	}
	
	private void setText()
	{
		if(this.m_Begin<0 || this.m_End<0 || this.m_Sentence==null)
		{
			Exception e = new Exception();
			e.printStackTrace();
			return;
		}
		String sentenceText = this.m_Sentence.getSentenceText();
		this.m_text = sentenceText.substring(m_Begin, m_End+1);
	}
	
	public String toString()
	{		
		String str = "Entity:"+this.m_text
					+ " begin:"+this.m_Begin
					+ " end:"+this.m_End
					+ " type:"+this.m_Type;
		str += " id:";
		for(String id : m_idVector)
		{
			str += id+"~";
		}
		return str;
	}
	public Vector<String> getID() {
		/*if(this.m_idVector.isEmpty())
		{
			String[] ids = new String[1];
			ids[0] = "null";
			return ids;
		}
		int size = this.m_idVector.size();
		String[] ids = new String[size];
		for(int i=0; i<size; i++)
		{
			ids[i] = this.m_idVector.elementAt(i);
		}
		return ids;*/
		return m_idVector;
	}
	public void addID(String id)
	{
		this.m_idVector.add(id);
	}
	public void setID(Vector<String> ids)
	{
		int size = ids.size();
		this.m_idVector.setSize(size);
		for(int i=0; i<size; i++)
		{
			this.m_idVector.set(i, ids.elementAt(i));
		}
	}
	public int getIDNum()
	{
		return this.m_idVector.size();
	}
	public String getText()
	{
		return this.m_text;
	}
	public void clearID()
	{
		this.m_idVector.clear();
	}
	public boolean hasID(String id)
	{
		return this.m_idVector.contains(id);
	}
	
	public void setTokenIndex(int begin, int end)
	{
		this.m_tokenBeginIndex = begin;
		this.m_tokenEndIndex = end;
		BioNERToken[] tokens = this.m_Sentence.getTokens();
		this.m_Begin = tokens[this.m_tokenBeginIndex].getBegin();
		this.m_End = tokens[this.m_tokenEndIndex].getEnd();
		this.setText();
	}
	
	public void setTokenBeginIndex(int tokenBeginIndex) {
		this.m_tokenBeginIndex = tokenBeginIndex;
		BioNERToken[] tokens = this.m_Sentence.getTokens();
		this.m_Begin = tokens[this.m_tokenBeginIndex].getBegin();
		this.setText();
	}
	public int getTokenBeginIndex() {
		return m_tokenBeginIndex;
	}
	public void setTokenEndIndex(int tokenEndIndex) {
		this.m_tokenEndIndex = tokenEndIndex;
		BioNERToken[] tokens = this.m_Sentence.getTokens();
		this.m_End = tokens[this.m_tokenEndIndex].getEnd();
		this.setText();
	}
	public int getTokenEndIndex() {
		return m_tokenEndIndex;
	}
	public void setCandidates(BioNERCandidate[] candidates) {
		this.m_candidates = candidates;
		for(BioNERCandidate candidate : m_candidates)
		{
			candidate.setEntity(this);
		}
	}
	public BioNERCandidate[] getCandidates() {
		return m_candidates;
	}
	
	public BioNERParagraph getParagraph()
	{
		return this.m_Sentence.getParagraph();
	}
	public BioNERSection getSection()
	{
		return this.m_Sentence.getSection();
	}
	public BioNERDocument getDocument()
	{
		return this.m_Sentence.getDocument();
	}
	
	Vector<String> m_labelVector = new Vector<String>();
	public void addLabel(String label)
	{
		if(!m_labelVector.contains(label))
			m_labelVector.add(label);
	}
	public boolean containLabel(String label)
	{
		return m_labelVector.contains(label);
	}
	public Vector<String> getLabelVector()
	{
		return m_labelVector;
	}
	public void setLabelVector(Vector<String> labelVector)
	{
		m_labelVector = new Vector<String>();
		for(String label : labelVector)
		{
			m_labelVector.add(label);
		}
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getScore() {
		return score;
	}
}
