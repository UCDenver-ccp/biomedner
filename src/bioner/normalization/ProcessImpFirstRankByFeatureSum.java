package bioner.normalization;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationFeatureBuilder;
import bioner.process.BioNERProcess;

public class ProcessImpFirstRankByFeatureSum implements BioNERProcess {

	private NormalizationFeatureBuilder featureBuilder = new NormalizationFeatureBuilder();
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				processEntity(entity);
			}
		}
	}
	
	private void processEntity(BioNEREntity entity)
	{
		for(BioNERCandidate candidate : entity.getCandidates())
		{
			String[] features = featureBuilder.getFeatures(candidate);
			double score = 0.0;
			int speciesFeatureIndexEnd = features.length-2;
			for(int i=0; i<features.length; i++)
			{
				double featureDouble = Double.parseDouble(features[i]);
				if(i<=speciesFeatureIndexEnd)
				{
					score += featureDouble * 0.1;
				}
				else
				{
					score += featureDouble;
				}
			}
			candidate.setScore(score);
		}
		RankCandidate.RankCandidate(entity.getCandidates());
	}

}
