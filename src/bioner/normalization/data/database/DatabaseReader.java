package bioner.normalization.data.database;

import java.util.HashMap;
import bioner.normalization.data.BioNERRecord;

/**
 * Interface for reading data from database.
 * @author Liu Jingchen
 *
 */
public interface DatabaseReader {
	/**
	 * Get gene records from database according to a given ID array.
	 * @param idArray String[] An array of IDs for gene records.
	 * @return An vector of BioNERRecord. 
	 */
	public abstract HashMap<String, BioNERRecord> searchRecords(String[] idArray);
	public abstract void connect();
	public abstract void close();
}
