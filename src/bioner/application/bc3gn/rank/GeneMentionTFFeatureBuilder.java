package bioner.application.bc3gn.rank;

import java.util.HashMap;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class GeneMentionTFFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private HashMap<String, Integer> m_currentTFTable = null;
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
		
		String gmStr = candidate.getEntity().getText();
		Integer tf = m_currentTFTable.get(gmStr);
		
		if(tf != null) return tf.toString();
		return "0";
	}
	private HashMap<String, Integer> getTFTable(BioNERDocument document)
	{
		HashMap<String, Integer> table = new HashMap<String, Integer>();
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				Integer tf = table.get(entity.getText());
				if(tf==null) tf = 0;
				tf++;
				table.put(entity.getText(), tf);
			}
		}
			
		
		return table;
	}
}
