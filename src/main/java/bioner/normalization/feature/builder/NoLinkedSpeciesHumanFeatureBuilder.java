package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class NoLinkedSpeciesHumanFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private static HashMap<String, Vector<String>> m_currentTalbe = null;
	private static BioNERDocument m_currentDocument = null;
	private static GeneSpeciesLinker m_linker = new GeneSpeciesLinker();
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		if(!candidate.getRecord().getSpeciesID().equals("9606")) return "0.0";
		BioNERDocument document = candidate.getEntity().getDocument();
		if(document != m_currentDocument)
		{
			m_currentDocument = document;
			m_currentTalbe = m_linker.getSpeciesTable(m_currentDocument);
		}
		BioNERCandidate[] candidates = candidate.getEntity().getCandidates();
		String gmStr = candidate.getEntity().getText();
		Vector<String> speciesIDVector = m_currentTalbe.get(gmStr);
		if(speciesIDVector==null) return "1.0";
		
		for(int i=0; i<candidates.length; i++)
		{
			if(speciesIDVector.contains(candidates[i].getRecord().getSpeciesID()))
			{
				return "0.0";
			}
		}
		
		return "1.0";
	}

}
