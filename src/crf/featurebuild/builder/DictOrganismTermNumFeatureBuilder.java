package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.tools.dictionary.BioNERTerm;
import crf.featurebuild.TokenFeatureBuilder;

public class DictOrganismTermNumFeatureBuilder implements TokenFeatureBuilder {

	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		int num = 0;
		for(BioNERTerm term : tokens[index].getTermVector())
		{
			if(term.getType().equals(GlobalConfig.ORGANISM_TYPE_LABEL))
			{
				num++;
			}
		}
		
		
		
		if(num<=0) return "0";
		if(num<=50) return "50";
		if(num<=100) return "100";
		if(num<=500) return "500";
		if(num<=1000) return "1000";
		if(num<=2000) return "2000";
		return "N";
	}

}
