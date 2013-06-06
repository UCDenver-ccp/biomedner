package bioner.normalization.data.index;

import java.util.ArrayList;
import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;

public class LuceneIndexSpeicesIDFinder {
	
	private LuceneIndexReader m_indexReader = new LuceneIndexReader(IndexConfig.SPECIES_INDEX_DIRECTORY);
	public String[] getSpeicesID(String speciesStr)
	{
		
		Vector<String> tokenVector = GeneMentionTokenizer.getTokens(speciesStr);
		BioNERCandidate[] candidates = m_indexReader.searchIDs(tokenVector);
		ArrayList<String> idArray = new ArrayList<String>();
		if(candidates.length>0)
		{
			double maxScore = candidates[0].getScore();
			for(int i=0; i<candidates.length; i++)
			{
				double score = candidates[i].getScore();
				if(Math.abs(maxScore-score)<0.00000001)
				{
					idArray.add(candidates[i].getRecordID());
				}
			}
		}
		int size = idArray.size();
		String[] array = new String[size];
		for(int i=0; i<size; i++)
		{
			array[i] = idArray.get(i);
		}
		return array;
	}
	
	public static void main(String[] args)
	{
		IndexConfig.ReadConfigFile();
		LuceneIndexSpeicesIDFinder finder = new LuceneIndexSpeicesIDFinder();
		String[] ids = finder.getSpeicesID("mouse");
		for(String id : ids)
		{
		System.out.println(id);
		}
	}
}
