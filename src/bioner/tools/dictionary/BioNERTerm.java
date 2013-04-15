/////////////////////////////////////////////////////////////
//Usage: This class represent for one 'term' in a dictionary. It can be a concept, such as a gene/protein or a organism.
//		It contains two parts: id and description. The description is the text for the detail of this term. This text will be used in the matching process.
//Author: Liu Jingchen
//Date: 2009/12/7
/////////////////////////////////////////////////////////////
package bioner.tools.dictionary;

import java.util.Vector;

public class BioNERTerm {
	private String m_id;
	private Vector<String> m_describe = new Vector<String>();
	private String m_type;
	public BioNERTerm()
	{
		m_id = "";
		m_type = "";
	}
	public void setId(String m_id) {
		this.m_id = m_id;
	}
	public String getId() {
		return m_id;
	}
	public void setDescribe(String describe) {
		String[] parts = describe.split("\\~");
		m_describe.setSize(parts.length);
		for(int i=0; i<parts.length; i++)
		{
			m_describe.set(i, parts[i]);
		}
	}
	public Vector<String> getDescribe() {
		return m_describe;
	}
	public void setType(String m_type) {
		this.m_type = m_type;
	}
	public String getType() {
		return m_type;
	}
	public void addDescribe(String describe)
	{
		if(!this.m_describe.contains(describe))
		{
			this.m_describe.add(describe);
		}
	}
	
}
