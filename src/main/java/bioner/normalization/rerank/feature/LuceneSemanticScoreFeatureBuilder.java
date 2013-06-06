package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.LuceneIndexSemanticReader;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class LuceneSemanticScoreFeatureBuilder implements RerankFeatureBuilder {

	private static LuceneIndexSemanticReader semanticReader = LuceneIndexSemanticReader.getLuceneIndexSemanticReader();
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		
		BioNERCandidate[] semanticCandidateArray = semanticReader.searchDocument(document);
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
