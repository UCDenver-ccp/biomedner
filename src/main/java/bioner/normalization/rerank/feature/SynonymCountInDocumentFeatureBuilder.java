package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class SynonymCountInDocumentFeatureBuilder implements
		RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		int num=0;
		for(String synonymStr : candidate.getRecord().getSynonyms())
		{
			String patternStr = synonymStr.toLowerCase().replaceAll("\\W+", "\\\\W{0,2}");
			patternStr = "\\b"+patternStr+"\\b";
			Pattern pattern = Pattern.compile(patternStr);
			for(BioNERSentence sentence : document.getAllSentence())
			{
				String sentenceText = sentence.getSentenceText().toLowerCase();
				Matcher matcher = pattern.matcher(sentenceText);
				while(matcher.find()) num++;
			}
		}
		
		return Integer.toString(num);
	}

	public static void main(String[] args)
	{
		String gm = "abc-er bce";
		gm = gm.toLowerCase().replaceAll("\\W+", "\\\\W{0,2}");
		System.out.println(gm);
	}
}
