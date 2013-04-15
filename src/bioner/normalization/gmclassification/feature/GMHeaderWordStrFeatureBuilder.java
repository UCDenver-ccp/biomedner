package bioner.normalization.gmclassification.feature;

import java.util.Vector;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.ChunkHeaderRecognizer;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMHeaderWordStrFeatureBuilder implements
		GMClassificationFeatureBuilder {

	private static ChunkHeaderRecognizer chunkHeaderRecognizer = ChunkHeaderRecognizer.getRecognizer();
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		BioNERSentence sentence = entity.get_Sentence();
		Vector<Integer> headerIndexVector = chunkHeaderRecognizer.getChunkHeaders(sentence, entity.getTokenBeginIndex(), entity.getTokenEndIndex());
		int maxIndex = 0;
		for(Integer index : headerIndexVector)
		{
			if(index>maxIndex) maxIndex = index;
		}
		String headerWord = sentence.getTokens()[maxIndex].getText();
		if(headerWord.matches("\\W+")) return "null";
		return "\""+headerWord+"\"";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "string";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "HeaderWordStr";
	}

}
