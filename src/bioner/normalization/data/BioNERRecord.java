package bioner.normalization.data;

import java.util.HashMap;
import java.util.Vector;

/**
 * This class stores one record in the knowledge base.
 * @author Liu Jingchen
 *
 */
 
public class BioNERRecord {
	
	private String m_id = null;
	private String m_speciesID = null;
	private String m_symbol = null;
	private String[] m_synonyms = null;
	private HashMap<String, String> m_attributes = new HashMap<String, String>();
	
	public void setID(String m_id) {
		this.m_id = m_id;
	}
	public String getID() {
		return m_id;
	}
	public void setSpeciesID(String m_speciesID) {
		this.m_speciesID = m_speciesID;
	}
	public String getSpeciesID() {
		return m_speciesID;
	}
	public void setSymbol(String m_symbol) {
		this.m_symbol = m_symbol;
	}
	public String getSymbol() {
		return m_symbol;
	}
	public void setSynonyms(String[] synonyms) {
		this.m_synonyms = synonyms;
	}
	public void setSynonyms(Vector<String> synonyms) {
		this.m_synonyms = new String[synonyms.size()];
		for(int i=0; i<synonyms.size(); i++)
		{
			this.m_synonyms[i] = synonyms.elementAt(i);
		}
	}
	public String[] getSynonyms() {
		return m_synonyms;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("species:");
		sb.append(m_speciesID);
		sb.append("\tGeneID:");
		sb.append(m_id);
		sb.append("\tsymbol:");
		sb.append(m_symbol);
		sb.append("\tsynonyms:");
		if(m_synonyms!=null)
		{
			for(String synonym : m_synonyms)
			{
				sb.append(synonym);
				sb.append("|");
			}
		}
		for(String attributeName : this.m_attributes.keySet())
		{
			String attributeValue = this.m_attributes.get(attributeName);
			sb.append("\t"+attributeName+":");
			sb.append(attributeValue);
		}
		sb.trimToSize();
		return sb.toString();
	}
	
	public void setAttribute(String name, String value)
	{
		this.m_attributes.put(name, value);
	}
	public String getAttribute(String name)
	{
		return this.m_attributes.get(name);
	}
}
