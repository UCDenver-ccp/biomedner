package bioner.application.bc2gn;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.GMCoveredRateFeatureBuilder;
import bioner.process.BioNERProcess;
import bioner.tools.nlptools.CombinedNameRecognizer;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class ProcessImpBC2GNFilterAfterGetCandidate implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				removeNonHumanCandidates(entity);
				if(!entity.containLabel(GlobalConfig.ENTITY_LABEL_CRF))
				{
					removeNotMatchedCandidates(entity);
					removeFullNameNotMatchedCandidates(entity);
					removeByCombinedName(entity);
				}
			}
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entityArray)
			{
				if(entity.getCandidates().length>0) sentence.addEntity(entity);
			}
		}
	}
	
	private void removeByCombinedName(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERCandidate[] candidates = entity.getCandidates();
		if(candidates.length==1) return;
		HashMap<String, String> nameMap = CombinedNameRecognizer.getCombinedNameMap(entity.getDocument());
		if(nameMap==null) return;
		String combinedName = nameMap.get(entity.getText());
		if(combinedName==null) return;
		Vector<BioNERCandidate> candidateVector = new Vector<BioNERCandidate>();
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(combinedName);
		String gmText = combinedName;
		
		/*for(int i=0; i<candidates.length; i++)
		{
			String symbol = candidates[i].getRecord().getSymbol();
			if(gmText.equals(symbol)) candidateVector.add(candidates[i]);
		}*/
		/*if(candidateVector.isEmpty())
		{
			for(int i=0; i<candidates.length; i++)
			{
				for(String synonym : candidates[i].getRecord().getSynonyms())
				{
					if(gmText.equals(synonym)) 
						candidateVector.add(candidates[i]);
				}
				
			}
		}*/
		String[] parts = gmText.split("\\W+");
		int tokenNum = parts.length;
		
		if(candidateVector.isEmpty())
		{
			for(int i=0; i<candidates.length; i++)
			{
				double maxR =0.0;
				double maxP =0.0;
				for(String synonym : candidates[i].getRecord().getSynonyms())
				{
					Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
					double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
					
					double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
					
					if(!gmText.matches("[a-z\\W]+") && !synonym.matches("[a-z\\W]+"))
					{
						if(gmText.toLowerCase().equals(synonym.toLowerCase()))
						{
							valueP = 1.0;
							valueR = 1.0;
						}
					}
					if(valueP>maxP) maxP = valueP;
					if(valueR>maxR) maxR = valueR;
					if(tokenNum<3)
					{
						if(valueP>0.9 && valueR>0.9)
						{
							candidateVector.add(candidates[i]);
							break;
						}
					}
					else
					{
						if(valueP>0.49 && valueR>0.49)
						{
							candidateVector.add(candidates[i]);
							break;
						}
					}
				}
				//if(maxP>0.60 && maxR>0.9) 
				//	candidateVector.add(candidates[i]);
			}
		}
		if(!candidateVector.isEmpty())
		{
			BioNERCandidate[] newCandidates = new BioNERCandidate[candidateVector.size()];
			for(int i=0; i<newCandidates.length; i++)
			{
				newCandidates[i] = candidateVector.elementAt(i);
			}
			entity.setCandidates(newCandidates);
		}
	}

	private void removeNonHumanCandidates(BioNEREntity entity)
	{
		BioNERCandidate[] candidates = entity.getCandidates();
		int size=0;
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecord().getSpeciesID().equals("9606")) size++;
		}
		BioNERCandidate[] humanCandidates = new BioNERCandidate[size];
		int index=0;
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecord().getSpeciesID().equals("9606"))
			{
				humanCandidates[index] = candidates[i];
				index++;
			}
		}
		entity.setCandidates(humanCandidates);
	}
	private void removeNotMatchedCandidates(BioNEREntity entity)
	{
		BioNERCandidate[] candidates = entity.getCandidates();
		Vector<BioNERCandidate> candidateVector = new Vector<BioNERCandidate>();
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(entity.getText());
		String gmText = entity.getText();
		
		/*for(int i=0; i<candidates.length; i++)
		{
			String symbol = candidates[i].getRecord().getSymbol();
			if(gmText.equals(symbol)) candidateVector.add(candidates[i]);
		}*/
		/*if(candidateVector.isEmpty())
		{
			for(int i=0; i<candidates.length; i++)
			{
				for(String synonym : candidates[i].getRecord().getSynonyms())
				{
					if(gmText.equals(synonym)) 
						candidateVector.add(candidates[i]);
				}
				
			}
		}*/
		String[] parts = gmText.split("\\W+");
		int tokenNum = parts.length;
		
		if(candidateVector.isEmpty())
		{
			for(int i=0; i<candidates.length; i++)
			{
				double maxR =0.0;
				double maxP =0.0;
				for(String synonym : candidates[i].getRecord().getSynonyms())
				{
					Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
					double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
					
					double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
					
					if(!gmText.matches("[a-z\\W]+") && !synonym.matches("[a-z\\W]+"))
					{
						if(gmText.toLowerCase().replaceAll("\\W+", "").equals(synonym.toLowerCase().replaceAll("\\W+", "")))
						{
							valueP = 1.0;
							valueR = 1.0;
						}
					}
					if(valueP>maxP) maxP = valueP;
					if(valueR>maxR) maxR = valueR;
					if(tokenNum<3)
					{
						if(valueP>0.9 && valueR>0.9)
						{
							candidateVector.add(candidates[i]);
							break;
						}
					}
					else
					{
						if(valueP>0.49 && valueR>0.49)
						{
							candidateVector.add(candidates[i]);
							break;
						}
					}
				}
				//if(maxP>0.60 && maxR>0.9) 
				//	candidateVector.add(candidates[i]);
			}
		}
		BioNERCandidate[] newCandidates = new BioNERCandidate[candidateVector.size()];
		for(int i=0; i<newCandidates.length; i++)
		{
			newCandidates[i] = candidateVector.elementAt(i);
		}
		entity.setCandidates(newCandidates);
	}
	
	
	
	private void removeFullNameNotMatchedCandidates(BioNEREntity entity)
	{
		BioNERCandidate[] candidates = entity.getCandidates();
		Vector<BioNERCandidate> candidateVector = new Vector<BioNERCandidate>();
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(entity.get_Sentence().getDocument());
		String fullname = fullNameMap.get(entity.getText());
		if(fullname==null) return;
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(fullname);
		String gmText = fullname;
		for(int i=0; i<candidates.length; i++)
		{
			for(String synonym : candidates[i].getRecord().getSynonyms())
			{
				Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
				double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
				
				double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
				
				if(!gmText.matches("[a-z\\W]+") && !synonym.matches("[a-z\\W]+"))
				{
					if(gmText.toLowerCase().equals(synonym.toLowerCase()))
					{
						valueP = 1.0;
						valueR = 1.0;
					}
				}
				
				if(valueP>0.49 && valueR>0.49)
				{
					candidateVector.add(candidates[i]);
					break;
				}
			}
			
		}
		BioNERCandidate[] newCandidates = new BioNERCandidate[candidateVector.size()];
		for(int i=0; i<newCandidates.length; i++)
		{
			newCandidates[i] = candidateVector.elementAt(i);
		}
		entity.setCandidates(newCandidates);
	}
	
	public static void main(String[] args)
	{
		CandidateFinder finder = new CandidateFinder();
		String text="RIP1";
		BioNERSentence sentence = new BioNERSentence(text, 0);
		BioNERCandidate[] candidates = finder.getCandidatesForGeneMentionString(text);
		BioNEREntity entity = new BioNEREntity();
		entity.set_Sentence(sentence);
		entity.setTokenIndex(0, sentence.getTokens().length-1);
		entity.setCandidates(candidates);
		ProcessImpBC2GNFilterAfterGetCandidate filter = new ProcessImpBC2GNFilterAfterGetCandidate();
		filter.removeNonHumanCandidates(entity);
		filter.removeNotMatchedCandidates(entity);
		candidates = entity.getCandidates();
		if(candidates.length==0) System.out.println("Not match!");
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i].getRecord().getSpeciesID().equals("9606"))
				System.out.println(candidates[i].toString());
		}
	}

}
