package bioner.normalization.feature.builder;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.LuceneIndexSemanticReader;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class LuceneSemanticScoreFeatureBuilder implements
		NormalizationPairFeatureBuilder {
	private static LuceneIndexSemanticReader semanticReader = LuceneIndexSemanticReader.getLuceneIndexSemanticReader();
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERCandidate[] semanticCandidateArray = semanticReader.searchDocument(candidate.getEntity().getDocument());
		double value = 0.0;
		String geneID = candidate.getRecord().getID();
		for(int i=0; i<semanticCandidateArray.length; i++)
		{
			double semanticScore = semanticCandidateArray[i].getScore();
			if(geneID.equals(semanticCandidateArray[i].getRecordID()) && semanticScore>value)
			{
				value = semanticScore;
				break;
			}
		}
		
		return Double.toString(value);
	}

}
