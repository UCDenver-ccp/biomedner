package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class GMCountRateFeatureBuilder implements RerankFeatureBuilder {
	private BioNERDocument m_currentDocument = null;
	private int m_gmNum = 0;
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		
		if(m_currentDocument!=document)
		{
			m_currentDocument=document;
			m_gmNum = 0;
			for(String geneID : map.keySet())
			{
				m_gmNum += map.get(geneID).size();
			}
		}
		
		String speciesID = candidate.getRecord().getID();
		Vector<BioNEREntity> entityVector = map.get(speciesID);
		if(entityVector==null || m_gmNum==0)
		{
			return "0.0";
		}
		return Double.toString((double)entityVector.size()/(double)m_gmNum);
	}

	

}
