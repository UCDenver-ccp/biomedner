package bioner.application.bc3gn.rank;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SameGMDifferIDFeatureBuilder implements
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
		String gmStr = candidate.getEntity().getText();
		Vector<String> idVector = m_currentMap.get(gmStr);
		if(idVector==null) return "0";
		int size = idVector.size();
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
				Vector<String> idVector = map.get(gmStr);
				if(idVector==null)
				{
					idVector = new Vector<String>();
					map.put(gmStr, idVector);
				}
				String id = candidates[0].getRecord().getID();
				if(!idVector.contains(id)) idVector.add(id);
			}
		}
		return map;
	}
}
