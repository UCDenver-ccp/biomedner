package bioner.normalization.gmclassification.feature;

import java.util.HashMap;
import java.util.Vector;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.ChunkHeaderRecognizer;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class GMFullnameHeaderPluralFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private static ChunkHeaderRecognizer chunkHeaderRecognizer = ChunkHeaderRecognizer.getRecognizer();
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(entity.get_Sentence().getDocument());
		String fullNameText = fullNameMap.get(entity.getText());
		String gmText = entity.getText();
		String[] words = entity.getText().split("\\s+");
		for(String word : words)
		{
			fullNameText = fullNameMap.get(word);
			if(fullNameText!=null) break;
		}
		if(fullNameText==null) return "0";
		BioNERDocument document = entity.get_Sentence().getDocument();
		for(BioNERSentence sentence : document.getAllSentence())
		{//Find the sentence containing this full name
			int pos = sentence.getSentenceText().indexOf(fullNameText);
			if(pos<0) continue;
			
			//Get the token index of the full name
			int beginIndex = sentence.getTokenIndex(pos);
			int endIndex = sentence.getTokenIndex(pos+fullNameText.length()-1);
			Vector<Integer> headerIndexVector = chunkHeaderRecognizer.getChunkHeaders(sentence, beginIndex, endIndex);
			String[] postags = PosTagStore.getPosTag(sentence, sentence.getDocument());
			for(Integer index : headerIndexVector)
			{
				if(postags[index].equals("NNS") && gmText.endsWith("s")) return "1";
			}
		}
		
		
		return "0";
	}
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "GMFullNameHeaderPlural";
	}
	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

}
