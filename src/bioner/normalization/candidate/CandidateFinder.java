package bioner.normalization.candidate;

import java.util.HashMap;
import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.BioNERRecord;
import bioner.normalization.data.database.DatabaseReader;
import bioner.normalization.data.database.DatabaseReaderFactory;
import bioner.normalization.data.index.IndexReader;
import bioner.normalization.data.index.IndexReaderFactory;
import bioner.normalization.feature.builder.NCBIRankFinder;

public class CandidateFinder {

    // genes	
	private DatabaseReader m_databaseReader = DatabaseReaderFactory.createDatabaseReader();

    // index from synonyms to ids
	private IndexReader m_indexReader = IndexReaderFactory.createGeneIndexReader();
	private NCBIRankFinder m_ncbiFinder = new NCBIRankFinder();
	public CandidateFinder()
	{
		m_databaseReader.connect();
	}
	
	public void close()
	{
		m_databaseReader.close();
		m_indexReader.close();
	}
	public BioNERCandidate[] getCandidatesForGeneMentionString(String geneStr)
	{
		Vector<String> tokenVector = GeneMentionTokenizer.getTokens(geneStr);
		tokenVector.add(geneStr.replaceAll("\\W", " "));

		BioNERCandidate[] candidates = m_indexReader.searchIDs(tokenVector);
		//BioNERCandidate[] candidates = m_ncbiFinder.getCandidates(geneStr);
		
		String[] ids = new String[candidates.length];
        for (int i=0; i<candidates.length; i++) {
             ids[i] = candidates[i].getRecordID();
        }
		HashMap<String, BioNERRecord> recordTable = m_databaseReader.searchRecords(ids);


        // debug 
        if (false) {
	        StringBuilder sb = new StringBuilder();
			for (int i=0; i< candidates.length; i++) {
				ids[i] = candidates[i].getRecordID();
				sb.append(" " + ids[i]);
			}
	        if (recordTable == null) {
	            System.out.println("xxx CandidateFinder.getCandidatesForGeneMentionString: " + geneStr + 
	                " null record table " + sb);
	        }
	        else {
	            System.out.println("xxx CandidateFinder.getCandidatesForGeneMentionString: " + geneStr + 
	                " found " + recordTable.size() + " matching records  " + sb);
	        }
        }


		for (int i=0; i<candidates.length; i++) {
			String id = candidates[i].getRecordID();
			BioNERRecord record = recordTable.get(id);
			if (record==null) {
				candidates[i]=null;
			}
			else {
				candidates[i].setRecord(record); 
            }
		}
		
		int size=0;
		for (int i=0; i<candidates.length; i++) {
			if(candidates[i]!=null) size++;
		}
		if (size==candidates.length) {
            return candidates;
        }
		BioNERCandidate[] newCandidates = new BioNERCandidate[size];
		
		for (int i=0,j=0; i<candidates.length; i++) {
			if (candidates[i]!=null) {
				newCandidates[j] = candidates[i];
				j++;
			}
		}
		return newCandidates;
	}
	
	public static void main(String args[])
	{
		CandidateFinder finder = new CandidateFinder();
		long beginTime = System.currentTimeMillis();
		BioNERCandidate[] candidates = finder.getCandidatesForGeneMentionString("semaphorin");
		long endTime = System.currentTimeMillis();
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecord().getSpeciesID().equals("9606"))
				System.out.println(candidates[i].toString());
		}
		long time = endTime - beginTime;
		System.out.println("Time:"+time);
	}
	
}
