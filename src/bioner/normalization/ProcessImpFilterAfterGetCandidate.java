package bioner.normalization;

import java.util.HashMap;
import java.util.Vector;
import java.util.ArrayList;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.builder.GMCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.process.BioNERProcess;

public class ProcessImpFilterAfterGetCandidate implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {

		HashMap<String, Integer> countMap = getGMCountMap(document);

        ArrayList<BioNERSentence> debugList = new ArrayList<BioNERSentence>();

        // FILTER
		for (BioNERSentence sentence : document.getAllSentence()) {
			filterByScore(sentence, countMap);
		}
	
        // MATCH	
        for (BioNERSentence sentence : document.getAllSentence()) {
			for (BioNEREntity entity : sentence.getAllEntities()) {
				removeNotMatchedCandidates(entity);
			}
        }

        // CANDIDATES???
        for (BioNERSentence sentence : document.getAllSentence()) {

            // rebuild a sentence's entities list with only those who have candidates
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for (BioNEREntity entity : entityArray) {
				if (entity.getCandidates().length > 0) {
                     sentence.addEntity(entity);
                }
			}
		}

        // PRINT RESULTS
        for (BioNERSentence sen : debugList) {
            for (BioNEREntity entity : sen.getAllEntities()) {
                System.out.println("     D " + entity);    
            }
        }
	}

private static void dump(String label, Vector<String> vec) {
        System.out.print(label + " \"");
        for (String s:vec) {
            System.out.print(s + "\", \"");
        }
        System.out.println("");
}

       // WTF?!!	
	private void removeNotMatchedCandidates(BioNEREntity entity) {
		BioNERCandidate[] candidates = entity.getCandidates();
		Vector<BioNERCandidate> candidateVector = new Vector<BioNERCandidate>();
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(entity.getText());
		String gmText = entity.getText();
        System.out.println("====== Entity (recognized mention): " + entity.getIDNum() 
            + ", \"" + entity.getText() 
            + "\", " + entity.getScore() 
            + " begin:" + entity.get_Begin()
            + " end:" + entity.get_End());
        System.out.print("label vector: ");
        for (String s : entity.getLabelVector()) {
                System.out.print(s + ", ");
        }
        System.out.println(" ....has " + candidates.length + " candidates");
        dump("Tokens:", gmTokenVector);
	
			for (int i=0; i < candidates.length; i++) {
                boolean candidateAdded=false;
                System.out.println(" ---- Caniddate (potential id)" + i 
                                + "  id: " + candidates[i].getRecord().getID() 
                                + ", speciesID: " + candidates[i].getRecord().getSpeciesID()
                                + ", symbol: " + candidates[i].getRecord().getSymbol());
				for (String synonym : candidates[i].getRecord().getSynonyms()) {
                    System.out.println("      synonym " + synonym );
					Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
                    System.out.println("      ---->gm vector:" + gmTokenVector.size() +  " synonym vector: " + synonymTokenVector.size());
                    dump("      synonym tokenss:", synonymTokenVector);
					double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
					double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);
				    System.out.println("      valueP:" + valueP + " valueR:" + valueR);	
					if (!gmText.matches("[a-z\\W]+") && !synonym.matches("[a-z\\W]+")) {
                        System.out.println("xx.removeNotMatchedCandidates: " + gmText + ", " + synonym  + " do match the regex"); 
						if (gmText.toLowerCase().replaceAll("\\W+", "").equals(synonym.toLowerCase().replaceAll("\\W+", ""))) {
							valueP = 1.0;
							valueR = 1.0;
                            System.out.println("xx.removeNotMatchedCandidates: " + gmText + ", " + synonym  + " do match the  special regex"); 
						}
					}
                    else {
                        System.out.println("xx.removeNotMatchedCandidates: " + gmText + ", " + synonym  + " do not match the regex"); 
                    }
					if (valueP>0.49 && valueR>0.49) {
						candidateVector.add(candidates[i]);
                        candidateAdded=true;
						break;
					}
				}
			}
        System.out.print("\n");

		//BioNERCandidate[] newCandidates = new BioNERCandidate[candidateVector.size()];
		//for (int i=0; i<newCandidates.length; i++) {
		//	newCandidates[i] = candidateVector.elementAt(i);
		//}
		entity.setCandidates(candidateVector.toArray(new BioNERCandidate[0]));
	}

	
	private void filterByScore(BioNERSentence sentence, HashMap<String, Integer> countMap) {
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities(); // <---  !
		
		for (BioNEREntity entity : entityArray) {
			String text = entity.getText();
			Integer count = countMap.get(text);
			BioNERCandidate[] candidates = entity.getCandidates();
			if (candidates==null || candidates.length==0 ||count==null)  {
                continue;
            }
			double score = candidates[0].getScore();


				if (score > 1.9) {
                    sentence.addEntity(entity); 
                }
                else {
                    System.out.println("ProcessImpFilterAfterGetCandidate.filterByScore() score is too low: " + score + " " + text);
                }
/*
			if (count >= 4) {
				if (score > 3.0) {
				    sentence.addEntity(entity);
                }
			}
			else if (count >= 3) {
				if (score > 4.0) {
                    sentence.addEntity(entity);
                }
			}
			else if (count >= 2) {
				if (score > 5.0) {
                    sentence.addEntity(entity);
                }
			}
			else {
				//if (score > 2.0) {
				if (score > 1.9) {
                    sentence.addEntity(entity); 
                }
			}
*/
		}
	}

    // keys are entities' text strings
    // values are the number of times that key appears with an entity
	private HashMap<String, Integer> getGMCountMap(BioNERDocument document)
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (BioNERSentence sentence : document.getAllSentence()) {
			for (BioNEREntity entity : sentence.getAllEntities()) {
				String text = entity.getText();
				Integer num = map.get(text);
				if (num==null) { num=0; }
				num++;
				map.put(text, num);
			}
		}
		return map;
	}
	
	private void filterGeneIDbyExplicitSpecies(BioNEREntity entity, Vector<BioNEREntity> speciesEntityVector)
	{
		Vector<String> speciesIDVector = getExplicitSpeciesID(entity, speciesEntityVector);
		if(speciesIDVector.isEmpty()) return;
		BioNERCandidate[] candidates = entity.getCandidates();
		int size=0;
		for(int i=0; i<candidates.length; i++)
		{
			if(speciesIDVector.contains(candidates[i].getRecord().getSpeciesID())) size++;
		}
		BioNERCandidate[] correctCandidates = new BioNERCandidate[size];
		int index=0;
		for(int i=0; i<candidates.length; i++)
		{
			if(speciesIDVector.contains(candidates[i].getRecord().getSpeciesID()))
			{
				correctCandidates[index] = candidates[i];
				index++;
			}
		}
		entity.setCandidates(correctCandidates);
	}
	
	private Vector<String> getExplicitSpeciesID(BioNEREntity entity, Vector<BioNEREntity> speciesEntityVector)
	{
		Vector<String> speciesIDVector = new Vector<String>();
		String gmText = entity.getText();
		String speciesInGM = getSpeciesStrInGM(gmText);
		String speciesIDInGM = getSpeciesIDInGM(speciesInGM);
		if(!speciesIDInGM.equals(""))
		{
			speciesIDVector.add(speciesIDInGM);
			return speciesIDVector;
		}
		BioNERSentence gmSentence = entity.get_Sentence();
		for(BioNEREntity speciesEntity : speciesEntityVector)
		{
			if(speciesEntity.get_Sentence()==gmSentence)
			{
				int speciesEntityEnd = speciesEntity.getTokenEndIndex();
				if(speciesEntityEnd>=entity.getTokenBeginIndex()-1 && speciesEntityEnd<=entity.getTokenEndIndex())
				{
					for(String speciesID : speciesEntity.getID())
					{
						speciesIDVector.add(speciesID);
					}
					return speciesIDVector;
				}
			}
		}
		
		return speciesIDVector;
	}
	private String getSpeciesStrInGM(String geneStr)
	{
		for(int i=0; i<geneStr.length(); i++)
		{
			char c = geneStr.charAt(i);
			if(Character.isUpperCase(c))
			{
				return geneStr.substring(0, i);
			}
		}
		return null;
	}
	private String getSpeciesIDInGM(String speciesStr)
	{
		if(speciesStr==null) return "";
		if(speciesStr.equals("h")) return "9606";
		if(speciesStr.equals("r")) return "10116";
		if(speciesStr.equals("m")) return "10090";
		if(speciesStr.equals("d")) return "7227";
		if(speciesStr.equals("me")) return "7227";
		if(speciesStr.equals("mel")) return "7227";
		if(speciesStr.equals("y")) return "4932";
		
		return "";
	}
}
