package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class HighestRankFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String geneID = candidate.getRecord().getID();
		Vector<BioNEREntity> entityVector = map.get(geneID);
		int min = Integer.MAX_VALUE;
		for(BioNEREntity entity : entityVector)
		{
			BioNERCandidate[] candidates = entity.getCandidates();
			for(int i=0; i<candidates.length; i++)
			{
				if(candidates[i].getRecord().getID().equals(geneID))
				{
					if(i<min) min=i;
					break;
				}
			}
			if(min==0) break;
		}
		return Double.toString(1.0/(double)(min+1));
	}

}
