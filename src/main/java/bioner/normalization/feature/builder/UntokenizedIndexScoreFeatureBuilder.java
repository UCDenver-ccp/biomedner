package bioner.normalization.feature.builder;

import java.util.HashMap;
import bioner.data.document.BioNERDocument;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class UntokenizedIndexScoreFeatureBuilder implements
		NormalizationPairFeatureBuilder {
	
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String queryStr = candidate.getEntity().getText();
		BioNERDocument document = candidate.getEntity().getDocument();
		
		
		BioNERCandidate[] candidates = IndexUntokenizedSearcher.getCandidates(queryStr, document);
		String idStr = candidate.getRecordID();
		double score = 0.0;
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecordID().equals(idStr))
			{
				score = candidates[i].getScore();
				//score -= 3.0;
				//if(score<0) score=0.0;
				break;
			}
		}
		/*HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		queryStr = fullNameMap.get(candidate.getEntity().getText());
		if(queryStr!=null)
		{
			candidates = IndexUntokenizedSearcher.getCandidates(queryStr, document);
			for(int i=0; i<candidates.length; i++)
			{
				if(candidates[i].getRecordID().equals(idStr))
				{
					if(candidates[i].getScore()>score) score = candidates[i].getScore();
					//score -= 3.0;
					//if(score<0) score=0.0;
					break;
				}
			}
		}*/
		return Double.toString(score);
		
	}
	
	
	
}
