package bioner.normalization;

import statistics.rank.ListNet;
import bioner.application.bc3gn.rank.SecondRankFeatureBuilderFactory;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.process.BioNERProcess;

public class ProcessImpSecondRankByListNet implements BioNERProcess {

	private ListNet m_listNet = new ListNet(0);
	public ProcessImpSecondRankByListNet(String filename)
	{
		m_listNet.train(filename, null);
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
		int size = 5;
		if(candidates.length < size) size = candidates.length;
		BioNERCandidate[] topNCandidates = new BioNERCandidate[size];
		
		for(int i=0; i<size; i++)
		{
			topNCandidates[i] = candidates[i];
			String[] featureStrs = SecondRankFeatureBuilderFactory.getFeatures(topNCandidates[i]);
			double[] features = new double[featureStrs.length+1];
			features[0] = 0;
			for(int j=0; j<featureStrs.length; j++)
			{
				features[j+1] = Double.parseDouble(featureStrs[j]);
			}
			double score = m_listNet.rankingScore(features);
			topNCandidates[i].setScore(score);
		}
		RankCandidate.RankCandidate(topNCandidates);
		
		//normalize the score to 0.0--1.0
		/*if(candidates.length==1)
		{
			candidates[0].setScore(1.0);
		}
		else if(candidates.length>1)
		{
			double maxScore = candidates[0].getScore();
			double minScore = candidates[candidates.length-1].getScore();
			double distance = maxScore - minScore;
			for(int i=0; i<candidates.length; i++)
			{
				double score = candidates[i].getScore();
				score = (score - minScore) / distance;
				candidates[i].setScore(score);
			}
		}*/
		for(int i=0; i<size; i++)
		{
			candidates[i] = topNCandidates[i];
		}
		entity.setCandidates(candidates);
	}

}
