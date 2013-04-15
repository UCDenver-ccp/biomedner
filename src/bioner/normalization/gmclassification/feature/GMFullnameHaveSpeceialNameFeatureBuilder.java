package bioner.normalization.gmclassification.feature;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class GMFullnameHaveSpeceialNameFeatureBuilder implements
		GMClassificationFeatureBuilder {

	private Pattern m_pattern;
	private String m_str;
	public GMFullnameHaveSpeceialNameFeatureBuilder(String word)
	{
		m_pattern = Pattern.compile(".*\\b"+word+"s?\\b.*");
		m_str = word;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(entity.get_Sentence().getDocument());
		String fullNameText = fullNameMap.get(entity.getText());
		String[] words = entity.getText().split("\\s+");
		for(String word : words)
		{
			fullNameText = fullNameMap.get(word);
			if(fullNameText!=null) break;
		}
		if(fullNameText==null) return "0";
		Matcher matcher = m_pattern.matcher(fullNameText);
		if(matcher.matches()) return "1";
		return "0";
	}
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMFullNameContains";
	}
	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
