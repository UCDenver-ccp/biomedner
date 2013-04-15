package bioner.normalization.gmclassification.feature;

import java.util.Vector;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.ChunkHeaderRecognizer;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class GMHeaderPluralFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private static ChunkHeaderRecognizer chunkHeaderRecognizer = ChunkHeaderRecognizer.getRecognizer();
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		String gmText = entity.getText();
		BioNERSentence sentence = entity.get_Sentence();
		Vector<Integer> headerIndexVector = chunkHeaderRecognizer.getChunkHeaders(sentence, entity.getTokenBeginIndex(), entity.getTokenEndIndex());
		String[] postags = PosTagStore.getPosTag(sentence, sentence.getDocument());
		for(Integer index : headerIndexVector)
		{
			if(postags[index].equals("NNS") && gmText.endsWith("s")) return "1";
		}
		return "0";
	}
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMHeaderPlural";
	}
	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
