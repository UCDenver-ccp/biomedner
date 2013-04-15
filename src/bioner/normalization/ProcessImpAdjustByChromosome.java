package bioner.normalization;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.process.BioNERProcess;

public class ProcessImpAdjustByChromosome implements BioNERProcess {

	private int MAX_SEARCH_NUM = 2;
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		processEntityVector(document.getAllEntity());
	}
	
	private void processEntityVector(Vector<BioNEREntity> entityVector)
	{
		String maxSpeciesID = getMaxSpeciesID(entityVector);
		if(maxSpeciesID==null) return;
		String maxChromosomeStr = getMaxChromosome(entityVector, maxSpeciesID);
		if(maxChromosomeStr==null) return;
		for(BioNEREntity entity : entityVector)
		{
			BioNERCandidate[] candidates = entity.getCandidates();
			if(candidates.length<=0) continue;
			String speciesID = candidates[0].getRecord().getSpeciesID();
			String chromosomeStr = candidates[0].getRecord().getAttribute("chromosome");
			if(speciesID.equals(maxSpeciesID) && chromosomeStr.equals(maxChromosomeStr)) continue;
			for(int i=1; i<MAX_SEARCH_NUM && i<candidates.length; i++)
			{
				speciesID = candidates[i].getRecord().getSpeciesID();
				chromosomeStr = candidates[i].getRecord().getAttribute("chromosome");
				if(chromosomeStr != null && speciesID.equals(maxSpeciesID) && chromosomeStr.equals(maxChromosomeStr))
				{
					BioNERCandidate temp_candidate = candidates[0];
					candidates[0] = candidates[i];
					candidates[i] = temp_candidate;
					break;
				}
			}
		}
	}
	
	private String getMaxSpeciesID(Vector<BioNEREntity> entityVector)
	{
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();
		Vector<String> entityTextVector = new Vector<String>();
		for(BioNEREntity entity : entityVector)
		{
			if(entityTextVector.contains(entity.getText())) continue;
			entityTextVector.add(entity.getText());
			BioNERCandidate[] candidates = entity.getCandidates();
			for(int i=0; i<MAX_SEARCH_NUM && i<candidates.length; i++)
			{
				String speciesStr = candidates[i].getRecord().getSpeciesID();
				if(speciesStr != null && !speciesStr.equals("null"))
				{
					Integer count = countMap.get(speciesStr);
					if(count==null) count = 0;
					count++;
					countMap.put(speciesStr, count);
				}
				
			}
		}
		
		int max = 0;
		String maxSpeciesID = null;
		for(String keyStr : countMap.keySet())
		{
			int count = countMap.get(keyStr);
			if(count > max)
			{
				max = count;
				maxSpeciesID = keyStr;
			}
		}
		if(max <= 1) return null;
		if(max < entityTextVector.size()/2) return null;
		return maxSpeciesID;
	}
	
	private String getMaxChromosome(Vector<BioNEREntity> entityVector, String speciesID)
	{
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();
		Vector<String> entityTextVector = new Vector<String>();
		for(BioNEREntity entity : entityVector)
		{
			if(entityTextVector.contains(entity.getText())) continue;
			entityTextVector.add(entity.getText());
			BioNERCandidate[] candidates = entity.getCandidates();
			for(int i=0; i<MAX_SEARCH_NUM && i<candidates.length; i++)
			{
				if(candidates[i].getRecord().getSpeciesID().equals(speciesID))
				{
					String chromosomeStr = candidates[i].getRecord().getAttribute("chromosome");
					if(chromosomeStr != null && !chromosomeStr.equals("null"))
					{
						Integer count = countMap.get(chromosomeStr);
						if(count==null) count = 0;
						count++;
						countMap.put(chromosomeStr, count);
					}
				}
			}
		}
		
		int max = 0;
		int second_max = 0;
		String maxChromosomeStr = null;
		for(String keyStr : countMap.keySet())
		{
			int count = countMap.get(keyStr);
			if(count==max) second_max = max;
			if(count > max)
			{
				second_max = max;
				max = count;
				maxChromosomeStr = keyStr;
			}
		}
		if(max <= 1) return null;
		if(max == second_max) return null;
		if(max < entityTextVector.size()/2) return null;
		return maxChromosomeStr;
	}
}
