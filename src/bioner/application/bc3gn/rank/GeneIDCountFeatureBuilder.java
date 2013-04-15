package bioner.application.bc3gn.rank;

import java.util.HashMap;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class GeneIDCountFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private HashMap<String, Double> m_currentTFTable = null;
	private BioNERDocument m_currentDocument = null;
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERDocument document = candidate.getEntity().getDocument();
		if(m_currentDocument != document)
		{
			m_currentDocument = document;
			m_currentTFTable = getTFTable(m_currentDocument);
		}
		
		String geneID = candidate.getRecord().getID();
		Double tf = m_currentTFTable.get(geneID);
		
		if(tf != null) return tf.toString();
		return "0.0";
	}
	private HashMap<String, Double> getTFTable(BioNERDocument document)
	{
		HashMap<String, Double> table = new HashMap<String, Double>();
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				for(BioNERCandidate candidate : entity.getCandidates())
				{
					Double tf = table.get(candidate.getRecord().getID());
					if(tf==null) tf = 0.0;
					tf += 1.0;
					table.put(candidate.getRecord().getID(), tf);
				}
			}
		}
			
		
		return table;
	}

}
