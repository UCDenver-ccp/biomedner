package bioner.normalization.feature.builder;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.normalization.SpeciesParentFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SpeciesLinkedFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private static HashMap<String, Vector<String>> m_currentTalbe = null;
	private static BioNERDocument m_currentDocument = null;
	private static GeneSpeciesLinker m_linker = new GeneSpeciesLinker();
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERDocument document = candidate.getEntity().getDocument();
		if(document != m_currentDocument)
		{
			m_currentDocument = document;
			m_currentTalbe = m_linker.getSpeciesTable(m_currentDocument);
		}
		String gmStr = candidate.getEntity().getText();
		Vector<String> speciesIDVector = m_currentTalbe.get(gmStr);
		
		//If the species is not found for the gene mention, return 0.0;
		if(speciesIDVector == null) return "0.0";
		
		//If the species is found and correct for this candidate, return 1.0;
		String speciesID = candidate.getRecord().getSpeciesID();
		if(speciesIDVector.contains(speciesID)) return "1.0";
		
		for(String contextSpecies : speciesIDVector)
		{
			if(SpeciesParentFinder.isParent(speciesID, contextSpecies))
				return "1.0";
		}
		
		return "0.0";
	}

}
