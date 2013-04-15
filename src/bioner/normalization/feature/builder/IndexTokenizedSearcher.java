package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.IndexReader;
import bioner.normalization.data.index.IndexReaderFactory;

public class IndexTokenizedSearcher {
	private static BioNERDocument m_currentDocument = null;
	private static HashMap<String, BioNERCandidate[]> m_map = new HashMap<String, BioNERCandidate[]>();
	private static IndexReader m_indexReader = IndexReaderFactory.createGeneIndexReader();
	public static BioNERCandidate[] getCandidates(String query, BioNERDocument document)
	{
		if(m_currentDocument!=document) 
		{
			m_currentDocument = document;
			m_map.clear();
		}
		BioNERCandidate[] candidates = m_map.get(query);
		if(candidates!=null) return candidates;
		Vector<String> tokenVector = GeneMentionTokenizer.getTokens(query);
		candidates = m_indexReader.searchIDs(tokenVector);
		m_map.put(query, candidates);
		return candidates;
	}
}
