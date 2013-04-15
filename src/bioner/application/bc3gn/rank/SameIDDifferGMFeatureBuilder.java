package bioner.application.bc3gn.rank;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SameIDDifferGMFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private static BioNERDocument m_currentDocument = null;
	private static HashMap<String, Vector<String>> m_currentMap = null;
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERDocument document = candidate.getEntity().getDocument();
		if(m_currentDocument != document)
		{
			m_currentDocument = document;
			m_currentMap = getGMToIDMap(m_currentDocument);
		}
		String id = candidate.getRecord().getID();
		Vector<String> gmVector = m_currentMap.get(id);
		if(gmVector==null) return "0";
		int size = gmVector.size();
		return Integer.toString(size);
	}

	private static HashMap<String, Vector<String>> getGMToIDMap(BioNERDocument document)
	{
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				BioNERCandidate[] candidates = entity.getCandidates();
				if(candidates==null || candidates.length==0) continue;
				String gmStr = entity.getText();
				String id = candidates[0].getRecord().getID();
				Vector<String> gmVector = map.get(id);
				if(gmVector==null)
				{
					gmVector = new Vector<String>();
					map.put(id, gmVector);
				}
				
				if(!gmVector.contains(gmStr)) gmVector.add(gmStr);
			}
		}
		return map;
	}

}
