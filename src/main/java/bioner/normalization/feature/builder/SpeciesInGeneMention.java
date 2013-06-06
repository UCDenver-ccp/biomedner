package bioner.normalization.feature.builder;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SpeciesInGeneMention implements NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String geneText = candidate.getEntity().getText();
		String[] tokens = geneText.split("[\\s\\W]+");
		String speciesID = candidate.getRecord().getSpeciesID();
		for(String token : tokens)
		{
			if(token.matches("[a-z]+[A-Z].*"))
			{
				String speciesStr = getSpeciesStr(geneText);
				if(speciesID.equals(getSpeciesID(speciesStr))) 
					return "1";
			}
		}
		
		return "0";
	}

	private String getSpeciesStr(String geneStr)
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
	private String getSpeciesID(String speciesStr)
	{
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
