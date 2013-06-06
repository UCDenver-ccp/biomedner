package bioner.normalization.feature.builder;

import java.util.HashMap;

import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.BioNERRecord;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

/**
 * Return the min edit distance between the entity and the record's all names.
 * The names include the symbol and all its synonyms.
 * @author Liu Jingchen
 *
 */
public class MinEditDistanceFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String entityStr = candidate.getEntity().getText();
		BioNERRecord record = candidate.getRecord();
		
		
		entityStr = entityStr.toLowerCase().trim();
		String symbolStr = record.getSymbol().toLowerCase().trim();
		double min = (double)EditDistance.getEditDistance(entityStr, symbolStr)/(double)Math.max(entityStr.length(), symbolStr.length());
		String[] synonyms = record.getSynonyms();
		if(synonyms != null)
		{
			for(int i=0; i<synonyms.length; i++)
			{
				String synonymStr = synonyms[i].toLowerCase().trim();
				double dis = (double)EditDistance.getEditDistance(entityStr, synonymStr)/(double)Math.max(entityStr.length(), synonymStr.length());
				if(dis<min)
				{
					min = dis;
				}
			}
		}
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(candidate.getEntity().getDocument());
		entityStr = fullNameMap.get(candidate.getEntity().getText());
		if(entityStr!=null)
		{
			entityStr = entityStr.toLowerCase().trim();
			synonyms = record.getSynonyms();
			if(synonyms != null)
			{
				for(int i=0; i<synonyms.length; i++)
				{
					String synonymStr = synonyms[i].toLowerCase().trim();
					double dis = (double)EditDistance.getEditDistance(entityStr, synonymStr)/(double)Math.max(entityStr.length(), synonymStr.length());
					if(dis<min)
					{
						min = dis;
					}
				}
			}
		}
		return Double.toString(min);
		/*double score=0.0;
		if(min==0) score=1.0;
		else if(min<=1) score=0.8;
		else if(min<=2) score=0.6;
		else if(min<=3) score=0.5;
		else if(min<=5) score=0.2;
		else if(min<=10) score=0.1;
		else score=0.0;
		return Double.toString(score);*/
	}

}
