package bioner.normalization;

import statistics.rank.ListNet;
import bioner.application.bc3gn.rank.GeneIDRerankFeatureBuilderFactory;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.process.BioNERProcess;

public class ProcessImpRerankGeneIDByListNet implements BioNERProcess {

	private ListNet m_listNet = new ListNet(50);
	public ProcessImpRerankGeneIDByListNet(String filename)
	{
		m_listNet.train(filename, null);
	}
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		double max = -Double.MAX_VALUE;
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				processEntity(entity);
				BioNERCandidate[] candidates = entity.getCandidates();
				if(candidates==null || candidates.length==0) continue;
				double score = entity.getCandidates()[0].getScore();
				if(Math.abs(score)>max) max = Math.abs(score);
			}
		}
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				BioNERCandidate[] candidates = entity.getCandidates();
				if(candidates==null || candidates.length==0) continue;
				double score = entity.getCandidates()[0].getScore();
				score /= max;
				entity.getCandidates()[0].setScore(score);
			}
		}
	}
	
	private void processEntity(BioNEREntity entity)
	{
		BioNERCandidate[] candidates = entity.getCandidates();
		if(candidates==null) return;
		
		for(int i=0; i<candidates.length && i<=0; i++)
		{
			String[] featureStrs = GeneIDRerankFeatureBuilderFactory.getFeatures(candidates[i]);
			double[] features = new double[featureStrs.length+1];
			features[0] = 0;
			for(int j=0; j<featureStrs.length; j++)
			{
				features[j+1] = Double.parseDouble(featureStrs[j]);
			}
			double score = m_listNet.rankingScore(features);
			candidates[i].setScore(score);
			
		}
		
		//RankCandidate.RankCandidate(candidates);
		entity.setCandidates(candidates);
	}

}
