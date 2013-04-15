package bioner.normalization;

import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.process.BioNERProcess;

public class ProcessImpFilterAfterGeneIDRerank implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		Vector<Double> scoreVector = new Vector<Double>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				BioNERCandidate[] candidates = entity.getCandidates();
				if(candidates==null || candidates.length==0) continue;
				scoreVector.add(candidates[0].getScore());
			}
		}
		double threshold = getThreshold(scoreVector);
		if(threshold>0.1) threshold=0.1;
		System.out.print("Threshold="+threshold+" ");
		for(BioNERSentence sentence : document.getAllSentence())
		{
			filterByScoreThreshold(sentence, threshold);
		}
		
	}
	private void filterByScoreThreshold(BioNERSentence sentence, double threshold)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		
		for(BioNEREntity entity : entityArray)
		{
			BioNERCandidate[] candidates = entity.getCandidates();
			if(candidates==null || candidates.length==0) continue;
			double score = candidates[0].getScore();
			if(score>=threshold) sentence.addEntity(entity);
		}
	}
	private double getThreshold(Vector<Double> scoreVector)
	{
		double[] scoreArray = new double[scoreVector.size()];
		for(int i=0; i<scoreArray.length; i++)
		{
			scoreArray[i] = scoreVector.elementAt(i);
		}
		RankDouble.Rank(scoreArray);
		
		double max = -Double.MAX_VALUE;
		double threshold = 0.0;
		for(int i=0; i<scoreArray.length-1; i++)
		{
			double gradient = (scoreArray[i]-scoreArray[i+1]);///(scoreArray[i+1]);
			if(gradient>max) 
			{
				max = gradient;
				threshold = scoreArray[i];
			}
		}
		
		
		return threshold;
	}

}
