///////////////////////////////////////////////////////////////////////
//Usage: This class is used to store a entity from a BIO sequence.
//Author: Liu Jingchen
//Date: 2009/12/25
///////////////////////////////////////////////////////////////////////
package crf.eval;

public class EvalEntity {
	private int m_begin = -1;
	private int m_end = -1;
	
	private String m_type = null;
	
	public EvalEntity()
	{
		
	}
	public EvalEntity(int begin, int end, String type)
	{
		m_begin = begin;
		m_end = end;
		m_type = type;
	}
	
	public void setBegin(int m_begin) {
		this.m_begin = m_begin;
	}
	public int getBegin() {
		return m_begin;
	}
	public void setEnd(int m_end) {
		this.m_end = m_end;
	}
	public int getEnd() {
		return m_end;
	}
	public void setType(String m_type) {
		this.m_type = m_type;
	}
	public String getType() {
		return m_type;
	}
	
	public boolean equals(EvalEntity entity)
	{
		if(entity.getBegin() != m_begin) return false;
		if(entity.getEnd() != m_end) return false;
		if(!entity.getType().equals(m_type)) return false;
		return true;
	}
	
}
