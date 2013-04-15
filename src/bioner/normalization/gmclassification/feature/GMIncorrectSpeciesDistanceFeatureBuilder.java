package bioner.normalization.gmclassification.feature;

import java.util.Vector;

import bioner.data.document.BioNEREntity;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMIncorrectSpeciesDistanceFeatureBuilder implements
		GMClassificationFeatureBuilder {

	private String m_correctID;
	public GMIncorrectSpeciesDistanceFeatureBuilder(String correctID)
	{
		m_correctID = correctID;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
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
		
		if(minDis==0) return "1.5";
		if(minDis==Integer.MAX_VALUE) return "0";
		return Double.toString(1.0/(double)minDis);
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "IncorrectSpecies";
	}

}
