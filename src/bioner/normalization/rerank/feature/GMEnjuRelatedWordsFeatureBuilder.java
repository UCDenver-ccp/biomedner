package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;
import bioner.tools.enju.EnjuParser;
import bioner.tools.enju.EnjuWord;

public class GMEnjuRelatedWordsFeatureBuilder implements RerankFeatureBuilder {
	private Pattern m_pattern;
	public GMEnjuRelatedWordsFeatureBuilder(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> gmVector = map.get(candidate.getRecord().getID());
		for(BioNEREntity gmEntity : gmVector)
		{
			Vector<EnjuWord> wordVector = EnjuParser.getGMAllRelatedWords(gmEntity.get_Sentence(), gmEntity);
			for(EnjuWord word : wordVector)
			{
				Matcher matcher = m_pattern.matcher(word.getText());
				if(matcher.matches()) return "1";
			}
		}
		return "0";
	}

}
