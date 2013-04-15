package bioner.normalization.gmclassification.feature;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.ChunkHeaderRecognizer;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMHeaderWordFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private static ChunkHeaderRecognizer chunkHeaderRecognizer = ChunkHeaderRecognizer.getRecognizer();
	private Pattern m_pattern;
	private String m_str;
	public GMHeaderWordFeatureBuilder(String word)
	{
		m_pattern = Pattern.compile("\\b"+word+"s?\\b");
		m_str = word;
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERSentence sentence = entity.get_Sentence();
		Vector<Integer> headerIndexVector = chunkHeaderRecognizer.getChunkHeaders(sentence, entity.getTokenBeginIndex(), entity.getTokenEndIndex());
		for(Integer index : headerIndexVector)
		{
			String headerWord = sentence.getTokens()[index].getText();
			Matcher matcher = m_pattern.matcher(headerWord);
			if(matcher.matches())
			{
				return "1";
			}
		}
		return "0";
	}
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMHeaderWord";
	}
	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
