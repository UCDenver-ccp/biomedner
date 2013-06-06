package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.normalization.gmclassification.feature.GMWordDistanceFeatureBuilder;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class GMIncorrectSpeciesMaxDistanceFeatureBuilder implements
		RerankFeatureBuilder {
	private String m_correctID;
	public GMIncorrectSpeciesMaxDistanceFeatureBuilder(String correctID)
	{
		m_correctID = correctID;
	}
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		double scoreMax = 0.0;
		Vector<BioNEREntity> gmVector = map.get(candidate.getRecord().getID());
		for(BioNEREntity gmEntity : gmVector)
		{
			double score = getScore(gmEntity);
			if(scoreMax<score) scoreMax = score;
		}
		
		return Double.toString(scoreMax);
	}
	private double getScore(BioNEREntity entity)
	{
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(entity.getDocument());
		int minDis = Integer.MAX_VALUE;
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(speciesEntity.hasID(m_correctID)) continue;
			if(speciesEntity.get_Sentence() != entity.get_Sentence()) continue;
			for(int i=speciesEntity.getTokenBeginIndex(); i<=speciesEntity.getTokenEndIndex(); i++)
			{
				int dis = GMWordDistanceFeatureBuilder.getDistance(entity, i);
				if(minDis>dis) minDis=dis;
			}
		}
		
		if(minDis==0) return 1.5;
		if(minDis==Integer.MAX_VALUE) return 0.0;
		return 1.0/(double)minDis;
	}

}
