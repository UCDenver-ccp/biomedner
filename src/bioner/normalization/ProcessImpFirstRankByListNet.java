package bioner.normalization;

import java.io.File;

import statistics.rank.ListNet;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationFeatureBuilder;
import bioner.process.BioNERProcess;

public class ProcessImpFirstRankByListNet implements BioNERProcess {

	private ListNet m_listNet = new ListNet(0);
	private FirstRankFeatureBuilder m_featureBuilder;
	public ProcessImpFirstRankByListNet(String filename, FirstRankFeatureBuilder featureBuilder)
	{
		File modelFile = new File(filename+".model");
		if(modelFile.exists())
		{
			m_listNet.readModelFromFile(filename+".model");
		}
		else
		{
            System.out.println("ProcessImpFirstRankByListNet: training and creating: " + modelFile.getAbsolutePath());
            System.out.println("ProcessImpFirstRankByListNet: training and creating: " + filename);
			m_listNet.train(filename, null);
			//m_listNet.writeModelToFile(filename+".model");
		}
		m_featureBuilder = featureBuilder;
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
		if (candidates==null) {
            System.out.println("info ProcessImpFirstRankByListNet: no candidates");
            return;
        }
		for (int i=0; i<candidates.length; i++)
		{
			String[] featureStrs = m_featureBuilder.getFeatures(candidates[i]);
			double[] features = new double[featureStrs.length+1];
			features[0] = 0;
			for(int j=0; j<featureStrs.length; j++)
			{
				features[j+1] = Double.parseDouble(featureStrs[j]);
			}
			double score = m_listNet.rankingScore(features);
			candidates[i].setScore(score);
			features = new double[featureStrs.length];
			for(int j=0; j<features.length; j++)
			{
				features[j] = Double.parseDouble(featureStrs[j]);
			}
			candidates[i].setFeatures(features);
		}
        System.out.println("info ProcessImpFirstRankByListNet, about to RankCandidate()");
		RankCandidate.RankCandidate(candidates);
        System.out.println("info ProcessImpFirstRankByListNet done RankCandidate'ing");
	
	
		//normalize the score to 0.0--1.0
		/******
        if(candidates.length==1)
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
		}
        ********/
		
		entity.setCandidates(candidates);
	}

}
