package bioner.normalization;

import statistics.rank.RankNet;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.process.BioNERProcess;

public class ProcessImpFirstRankByRankNet implements BioNERProcess {

	private RankNet m_rankNet = new RankNet();
	private FirstRankFeatureBuilder m_featureBuilder;
	public ProcessImpFirstRankByRankNet(String filename, FirstRankFeatureBuilder featureBuilder)
	{
		m_featureBuilder = featureBuilder;
		m_rankNet.train(filename, null);
	}
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
		BioNERCandidate[] candidates = entity.getCandidates();
		if(candidates==null) return;
		for(int i=0; i<candidates.length; i++)
		{
			String[] featureStrs = m_featureBuilder.getFeatures(candidates[i]);
			double[] features = new double[featureStrs.length+1];
			features[0] = 0;
			for(int j=0; j<featureStrs.length; j++)
			{
				features[j+1] = Double.parseDouble(featureStrs[j]);
			}
			double score = m_rankNet.rankingScore(features);
			candidates[i].setScore(score);
			features = new double[featureStrs.length];
			for(int j=0; j<features.length; j++)
			{
				features[j] = Double.parseDouble(featureStrs[j]);
			}
			candidates[i].setFeatures(features);
		}
		RankCandidate.RankCandidate(candidates);
		entity.setCandidates(candidates);
	}

}
