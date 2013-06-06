package bioner.normalization;

public class DecaItem {
	private String m_docID;
	private String m_entityStr;
	private String m_typeStr;
	private String m_speciesID;
	public DecaItem(String docID, String entityStr, String typeStr, String speicesID)
	{
		m_docID = docID;
		m_entityStr = entityStr;
		m_typeStr = typeStr;
		m_speciesID = speicesID;
	}
	
	public void setDocID(String docID) {
		this.m_docID = docID;
	}
	public String getDocID() {
		return m_docID;
	}
	public void setEntityStr(String entityStr) {
		this.m_entityStr = entityStr;
	}
	public String getEntityStr() {
		return m_entityStr;
	}
	public void setTypeStr(String typeStr) {
		this.m_typeStr = typeStr;
	}
	public String getTypeStr() {
		return m_typeStr;
	}
	public void setSpeciesID(String speciesID) {
		this.m_speciesID = speciesID;
	}
	public String getSpeciesID() {
		return m_speciesID;
	}
}
