package crf.featurebuild.builder;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.tools.dictionary.BioNERTerm;
import crf.featurebuild.TokenFeatureBuilder;

public class DictProteinTermNumFeatureBuilder implements TokenFeatureBuilder {

	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		int num = 0;
		for(BioNERTerm term : tokens[index].getTermVector())
		{
			if(term.getType().equals(GlobalConfig.PROTEIN_TYPE_LABEL))
			{
				num++;
			}
		}
		
		
		
		/*if(num<=0) return "0";
		if(num<=10) return "10";
		if(num<=20) return "20";
		if(num<=30) return "30";
		if(num<=40) return "40";
		if(num<=50) return "50";
		if(num<=70) return "70";
		if(num<=90) return "90";
		if(num<=100) return "100";
		if(num<=150) return "150";
		if(num<=200) return "200";
		if(num<=300) return "300";
		if(num<=400) return "400";
		if(num<=500) return "500";
		if(num<=600) return "600";
		if(num<=700) return "700";
		if(num<=800) return "800";
		if(num<=900) return "900";
		if(num<=1000) return "1000";
		if(num<=2000) return "2000";*/
		if(num<=0) return "0";
		return "N";
	}

}
