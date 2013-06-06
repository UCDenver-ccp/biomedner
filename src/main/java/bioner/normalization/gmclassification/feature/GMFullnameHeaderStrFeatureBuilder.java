package bioner.normalization.gmclassification.feature;

import java.util.HashMap;
import java.util.Vector;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.ChunkHeaderRecognizer;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class GMFullnameHeaderStrFeatureBuilder implements
		GMClassificationFeatureBuilder {
	private static ChunkHeaderRecognizer chunkHeaderRecognizer = ChunkHeaderRecognizer.getRecognizer();
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
		
		if(fullNameText==null) return "null";
		BioNERDocument document = entity.get_Sentence().getDocument();
		for(BioNERSentence sentence : document.getAllSentence())
		{//Find the sentence containing this full name
			int pos = sentence.getSentenceText().indexOf(fullNameText);
			if(pos<0) continue;
			
			//Get the token index of the full name
			int beginIndex = sentence.getTokenIndex(pos);
			int endIndex = sentence.getTokenIndex(pos+fullNameText.length()-1);
			Vector<Integer> headerIndexVector = chunkHeaderRecognizer.getChunkHeaders(sentence, beginIndex, endIndex);
			int maxIndex=0;
			for(Integer index : headerIndexVector)
			{
				
				if(index>maxIndex) maxIndex = index;
			}
			String headerWord = sentence.getTokens()[maxIndex].getText();
			if(headerWord.matches("\\W+")) return "null";
			return "\""+headerWord+"\"";
		}
		
		
		return "null";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "string";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "FullnameHeaderWordStr";
	}

}
