package bioner.normalization.feature.builder;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class NCBIRankFeatureBuilder implements NormalizationPairFeatureBuilder {

	private NCBIRankFinder m_ncbiRank = new NCBIRankFinder();
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String geneStr = candidate.getEntity().getText();
		String[] rankIDs = m_ncbiRank.getRank(geneStr);
		String idStr = candidate.getRecord().getID();
		
		for(int i=0; i<rankIDs.length; i++)
		{
			if(rankIDs[i].equals(idStr))
			{
				//return Double.toString(1.0/(double)(i+1));
				return Integer.toString(i);
			}
		}
		//return Integer.toString(rankIDs.length);
		return "100";
	}

}
