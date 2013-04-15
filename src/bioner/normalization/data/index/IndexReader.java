package bioner.normalization.data.index;

import java.util.Vector;

import bioner.normalization.data.BioNERCandidate;

/**
 * Interface for search gene IDs from index.
 * @author Liu Jingchen
 *
 */
public interface IndexReader {
	/**
	 * Search for the gene IDs according to a set of given tokens.
	 * @param tokenVector Vector<String> A vector of tokens as the query.
	 * @return String[] An array of gene IDs.
	 */
	public abstract BioNERCandidate[] searchIDs(Vector<String> tokenVector);
	public abstract BioNERCandidate[] searchIDs(Vector<String> tokenVector, int maxNum);
	public abstract void close();
	public abstract double getIDF(String termStr);
}
