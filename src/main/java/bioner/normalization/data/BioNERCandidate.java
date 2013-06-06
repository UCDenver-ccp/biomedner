package bioner.normalization.data;

import bioner.data.document.BioNEREntity;

/**
 * This class is a link between BioNEREntity and BioNERRecord. It store one BioNERRecord as a gene id candidate for the BioNEREntity.
 * It also stores some information such as scores for this candidate. 
 * @author Liu Jingchen
 *
 */
public class BioNERCandidate {
	private BioNERRecord m_record = null;
	private double m_score = 0.0;
	private String m_recordID = null;
	private BioNEREntity m_entity = null;
	private double[] m_features = null;
	public void setRecord(BioNERRecord record) {
		this.m_record = record;
	}

	public BioNERRecord getRecord() {
		return m_record;
	}

	public void setScore(double score) {
		this.m_score = score;
	}

	public double getScore() {
		return m_score;
	}

	public void setRecordID(String m_recordID) {
		this.m_recordID = m_recordID;
	}

	public String getRecordID() {
		return m_recordID;
	}
	public String toString()
	{
		return m_recordID+" "+m_score+"|"+m_record.toString();
	}

	public void setEntity(BioNEREntity entity) {
		this.m_entity = entity;
	}

	public BioNEREntity getEntity() {
		return m_entity;
	}
	public BioNERCandidate clone()
	{
		BioNERCandidate cloneCandidate = new BioNERCandidate();
		cloneCandidate.setEntity(m_entity);
		cloneCandidate.setRecord(m_record);
		cloneCandidate.setRecordID(m_recordID);
		cloneCandidate.setScore(m_score);
		cloneCandidate.setFeatures(m_features.clone());
		return cloneCandidate;
	}

	public void setFeatures(double[] m_features) {
		this.m_features = m_features;
	}

	public double[] getFeatures() {
		return m_features;
	}
	
}
