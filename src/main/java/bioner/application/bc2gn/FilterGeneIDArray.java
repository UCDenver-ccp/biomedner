package bioner.application.bc2gn;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class FilterGeneIDArray {
	public static void filterGeneIDArray(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate[] candidates)
	{
		//mergeGeneIDByGM(document, map, candidates);
		//filterByScoreExceptAllMatch(candidates, document, map, 0.2, 0.1);
		filterByScore(candidates, 0.2);
		//filterAllLowerCase(candidates, map, 0.2);
		//filterByWordNumber(candidates, map, 2, 0.2);
		
		//filterOnlyTitleID(candidates, document, map);
	}
	private static void filterOnlyTitleID(BioNERCandidate[] candidates,BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map) {
		// TODO Auto-generated method stub
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i]==null) continue;
			String geneID = candidates[i].getRecord().getID();
			Vector<BioNEREntity> entityVector = map.get(geneID);
			boolean allTitle = true;
			for(BioNEREntity entity : entityVector)
			{
				if(entity.get_Sentence() != document.getTitle())
				{
					allTitle = false;
					break;
				}
			}
			if(allTitle) candidates[i] = null;
		}
	}
	private static void filterByWordNumber(BioNERCandidate[] candidates, HashMap<String, Vector<BioNEREntity>> map, int wordNum,
			double threshold) {
		// TODO Auto-generated method stub
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i]==null) continue;
			String geneID = candidates[i].getRecord().getID();
			Vector<BioNEREntity> entityVector = map.get(geneID);
			int minWordNum = Integer.MAX_VALUE;
			for(BioNEREntity entity : entityVector)
			{
				String gmStr = entity.getText();
				int num = gmStr.split("\\W+").length;
				if(num<minWordNum) minWordNum = num;
			}
			if(minWordNum>wordNum && candidates[i].getScore()<threshold) candidates[i] = null;
		}
	}
	private static void filterAllLowerCase(BioNERCandidate[] candidates, HashMap<String, Vector<BioNEREntity>> map,
			double threshold) {
		// TODO Auto-generated method stub
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i]==null) continue;
			String geneID = candidates[i].getRecord().getID();
			Vector<BioNEREntity> entityVector = map.get(geneID);
			boolean isAllLowerCase = true;
			for(BioNEREntity entity : entityVector)
			{
				String gmStr = entity.getText();
				if(!gmStr.matches("[a-z\\W]+"))
				{
					isAllLowerCase = false;
					break;
				}
			}
			if(isAllLowerCase && candidates[i].getScore()<threshold) candidates[i] = null;
		}
	}
	private static void filterByScore(BioNERCandidate[] candidates, double threshold)
	{
		for(int j=0; j<candidates.length; j++)
		{
			if(candidates[j]==null) continue;
			if(candidates[j].getScore()<threshold)
			{
				candidates[j] = null;
			}
		}
	}
	private static void filterByScoreExceptAllMatch(BioNERCandidate[] candidates,BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, double threshold, double allMatchThreshod)
	{
		for(int j=0; j<candidates.length; j++)
		{
			if(candidates[j]==null) continue;
			if(candidates[j].getScore()<threshold)
			{
				boolean isAllMatch = false;
				if(candidates[j].getScore() > allMatchThreshod)
				{
					String geneID = candidates[j].getRecord().getID();
					Vector<BioNEREntity> entityVector = map.get(geneID);
					for(BioNEREntity gmEntity : entityVector)
					{
						String gmText = gmEntity.getText();
						for(String synonym : candidates[j].getRecord().getSynonyms())
						{
							if(gmText.equals(synonym))
							{
								isAllMatch = true;
								break;
							}
							else if(!gmText.matches("[a-z\\W]+") && !synonym.matches("[a-z\\W]+"))
							{
								if(gmText.toLowerCase().equals(synonym.toLowerCase()))
								{
									isAllMatch = true;
									break;
								}
							}
						}
						if(isAllMatch) break;
					}
				}
				if(!isAllMatch) candidates[j] = null;
			}
		}
	}
	
	
	private static void mergeGeneIDByGM(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate[] candidates)
	{
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i]==null) continue;
			String geneID_1 = candidates[i].getRecord().getID();
			Vector<BioNEREntity> gmVector_1 = map.get(geneID_1);
			for(int j=0; j<candidates.length; j++)
			{
				if(candidates[j]==null || j==i) continue;
				String geneID_2 = candidates[j].getRecord().getID();
				Vector<BioNEREntity> gmVector_2 = map.get(geneID_2);
				if(hasSameGM(gmVector_1, gmVector_2, fullNameMap)
						&& candidates[i].getScore()<candidates[j].getScore())
				{
					gmVector_2.addAll(gmVector_1);
					candidates[i] = null;
					break;
				}
			}
		}
	}
	private static boolean hasSameGM(Vector<BioNEREntity> gmVector_1, Vector<BioNEREntity> gmVector_2, HashMap<String, String> fullNameMap)
	{
		Vector<String> gmStrVector_1 = new Vector<String>();
		for(BioNEREntity entity : gmVector_1)
		{
			String gmStr = entity.getText();
			if(!gmStrVector_1.contains(gmStr))
				gmStrVector_1.add(gmStr);
		}
		Vector<String> gmStrVector_2 = new Vector<String>();
		for(BioNEREntity entity : gmVector_2)
		{
			String gmStr = entity.getText();
			if(!gmStrVector_2.contains(gmStr))
				gmStrVector_2.add(gmStr);
		}
		
		for(String gmStr_1 : gmStrVector_1)
		{
			String fullName = fullNameMap.get(gmStr_1);
			if(gmStrVector_2.contains(gmStr_1) || gmStrVector_2.contains(fullName)) return true;
		}
		for(String gmStr_2 : gmStrVector_2)
		{
			String fullName = fullNameMap.get(gmStr_2);
			if(gmStrVector_1.contains(gmStr_2) || gmStrVector_1.contains(fullName)) return true;
		}
		
		return false;
	}
}
