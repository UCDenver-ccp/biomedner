package bioner.normalization;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.EditDistance;
import bioner.normalization.feature.builder.SymbolNumberSimilarityFeatureBuilder;
import bioner.process.BioNERProcess;

public class ProcessImpFilterAfterRank implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			//filterByIfHaveSimilarName(sentence);
			//filterByScore(sentence);
		}
	}
	
	private void filterByScore(BioNERSentence sentence)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		
		for(BioNEREntity entity : entityArray)
		{
			BioNERCandidate[] candidates = entity.getCandidates();
			if(candidates==null || candidates.length==0) continue;
			double score = candidates[0].getScore();
			if(score>0.5) sentence.addEntity(entity);
		}
	}
	
	private void filterByIfHaveSimilarName(BioNERSentence sentence)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		
		for(BioNEREntity entity : entityArray)
		{
			if(entity.getCandidates().length<=0) continue;
			BioNERCandidate candidate = entity.getCandidates()[0];
			String[] synonyms = candidate.getRecord().getSynonyms();
			String gmStr = entity.getText();
			for(String synonym : synonyms)
			{
				if(isSimilarNames(gmStr, synonym))
				{
					sentence.addEntity(entity);
					break;
				}
			}
		}
	}
	private static boolean isSimilarNames(String gmStr, String synonym)
	{
		int editDistance = EditDistance.getEditDistance(gmStr.toLowerCase().trim(), synonym.toLowerCase().trim());
		if(editDistance > gmStr.length() || editDistance > synonym.length()) return false;
		double numberSimilarity = SymbolNumberSimilarityFeatureBuilder.getNumberSimilarityOfTwoString(gmStr, synonym);
		if(numberSimilarity < 0.99) return false;
 		return true;
	}
	
	public static void main(String[] args)
	{
		String name_1 = "cd28";
		String name_2 = "cd-28";
		boolean isSim = isSimilarNames(name_1, name_2);
		if(isSim) System.out.println("is similar names!");
		else System.out.println("not similar names!");
	}
}
