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
        Vector<BioNEREntity> speciesEntityVector = SpeciesEntityStore.getSpeciesEntities(document);
        ArrayList<BioNERSentence> debugList = new ArrayList<BioNERSentence>();

        // FILTER
		for (BioNERSentence sentence : document.getAllSentence()) {
			filterByScore(sentence, countMap);
		}
        // MATCH	
        for (BioNERSentence sentence : document.getAllSentence()) {
			for (BioNEREntity entity : sentence.getAllEntities()) {
                // DISABLED
                if (false) {	 
                // I think this was from experimentation at some point, it's too restrictive
				    //System.out.println("Before removeNotMathcedCandidates: " + entity.getCandidates().length );
				    removeNotMatchedCandidates(entity);
				    //System.out.println("after removeNotMathcedCandidates: " + entity.getCandidates().length );
                }
                // this was commented out in source, but matches the paper
                /////filterGeneIDbyExplicitSpecies(entity, speciesEntityVector);      
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
        //System.out.println("====== Entity (recognized mention): " + entity.getIDNum() + ", " + entity.getText()); 


        // try matching record symbol with candidate text (rarely works, commented out in original code)
        if (false) {
        for (BioNERCandidate cand : candidates) {
            System.out.println("gm text:" + gmText + " symbol:" + cand.getRecord().getSymbol());
            if (gmText.equals(cand.getRecord().getSymbol())) {
                candidateVector.add(cand);
            }
        }
        }
          
        // try exact matches on the synonyms (why?)         

	
			for (int i=0; i < candidates.length; i++) {
                boolean candidateAdded=false;
				for (String synonym : candidates[i].getRecord().getSynonyms()) {
                    //System.out.println("      synonym " + synonym );
					Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
					double valueP = GMCoveredRateFeatureBuilder.getCoveredRate(gmTokenVector, synonymTokenVector);
					double valueR = GMCoveredRateFeatureBuilder.getCoveredRate(synonymTokenVector, gmTokenVector);

                    // DEBUG
                    if (false && valueP > 0.01 && valueR > 0.01) {
                        System.out.println("      ---->" + valueP + ", " + valueR);
                        dump("      gm  tokens:", gmTokenVector);
                        dump("      syn tokens:", synonymTokenVector);
                    }

                    // regex removal TODO: explain/identify in algorithm description
					if (!gmText.matches("[a-z\\W]+") && !synonym.matches("[a-z\\W]+")) {
                        //System.out.println("xx.removeNotMatchedCandidates: " + gmText + ", " + synonym  + " do match the regex"); 
						if (gmText.toLowerCase().replaceAll("\\W+", "").equals(synonym.toLowerCase().replaceAll("\\W+", ""))) {
							valueP = 1.0;
							valueR = 1.0;
                            //System.out.println("xx.removeNotMatchedCandidates: " + gmText + ", " + synonym  + " do match the  special regex"); 
						}
					}
                    else {
                        //System.out.println("xx.removeNotMatchedCandidates: " + gmText + ", " + synonym  + " do not match the regex"); 
                    }

					if (valueP>0.49 && valueR>0.49) {
						candidateVector.add(candidates[i]);
                        candidateAdded=true;
				        System.out.println("      Candidate:" + candidates[i].getRecord().getSymbol()  + " measured up: valueP:" + valueP + " valueR:" + valueR);	
						break;
					}
				}
			}

		entity.setCandidates(candidateVector.toArray(new BioNERCandidate[0]));
        if (entity.getCandidates().length > 0) {
        System.out.println("OMGWTF: " + entity.getCandidates().length);
        }
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

            // original code here would pass an entity only if the first
            // candidate has a high enough score
            // Q:  ARE THEY SORTED?
			double score = candidates[0].getScore();

            // A: check and see if you run into a higher value
            {
                for (BioNERCandidate bnc : candidates) {
                    if (bnc.getScore() > score) {
                        System.out.println("\n\nFOUND A BIGGER ONE!!!" + score + " < " + bnc.getScore() + "\n\n");
                    }
                }
            }


				if (score > 1.9) {
                    sentence.addEntity(entity); 
                    ///System.out.println("ProcessImpFilterAfterGetCandidate.filterByScore() score is good: " + score + " " + text);
                }
                else {
                    sentence.addEntity(entity); 
                   //////// System.out.println("ProcessImpFilterAfterGetCandidate.filterByScore() score sucks add it anyway: " + score + " " + text);
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
