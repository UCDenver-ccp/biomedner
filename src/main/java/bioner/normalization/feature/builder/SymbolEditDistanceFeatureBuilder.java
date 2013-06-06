package bioner.normalization.feature.builder;

import java.util.HashMap;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class SymbolEditDistanceFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String entityStr = candidate.getEntity().getText().toLowerCase().trim();
		String recordStr = candidate.getRecord().getSymbol().toLowerCase().trim();
		
		int distance = EditDistance.getEditDistance(entityStr, recordStr);
		double score = (double)distance/(double)Math.max(entityStr.length(), recordStr.length());
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(candidate.getEntity().getDocument());
		entityStr = fullNameMap.get(candidate.getEntity().getText());
		if(entityStr!=null)
		{
			entityStr = entityStr.toLowerCase().trim();
			distance = EditDistance.getEditDistance(entityStr, recordStr);
			double fullNameScore = (double)distance/(double)Math.max(entityStr.length(), recordStr.length());
			if(fullNameScore<score) score = fullNameScore;
		}
		return Double.toString(score);
		/*if(distance>5) distance=10;
		double score=0.0;
		if(distance==0) score=1.0;
		else if(distance<=1) score=0.8;
		else if(distance<=2) score=0.6;
		else if(distance<=3) score=0.5;
		else if(distance<=5) score=0.2;
		else if(distance<=10) score=0.1;
		else score=0.0;
		return Double.toString(score);*/
	}

}
