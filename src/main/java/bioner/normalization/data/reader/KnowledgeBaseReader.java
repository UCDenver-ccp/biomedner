package bioner.normalization.data.reader;

import java.util.HashMap;

import bioner.normalization.data.BioNERRecord;

/**
 * This is the interface for reading a knowledge base. The records of this knowledge base is the target of normalization.
 * The gene mentions will be mapped to these records.
 * @author Liu Jingchen
 *
 */
public interface KnowledgeBaseReader {
	/**
	 * 
	 * @return A hash map. The key is the IDs of the records, the value is the object of BioNERRecord.
	 */
	public abstract HashMap<String, BioNERRecord> getRecordTable();
}
