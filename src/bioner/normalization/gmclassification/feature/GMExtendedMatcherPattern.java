package bioner.normalization.gmclassification.feature;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNEREntity;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;
import bioner.tools.nlptools.DocumentChunkRecognizer;

public class GMExtendedMatcherPattern implements GMClassificationFeatureBuilder {
	private Pattern m_pattern;
	public GMExtendedMatcherPattern(String patternStr)
	{
		m_pattern = Pattern.compile(patternStr);
	}
	@Override
	public String extractFeature(BioNEREntity entity) {
		// TODO Auto-generated method stub
		String gmText = getExtendedText_byChunk(entity);
		Matcher matcher = m_pattern.matcher(gmText);
		if(matcher.matches()) return "1";
		return "0";
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return "real";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "gm_extend_match";
	}
	
	private static DocumentChunkRecognizer chunkRecognizer = DocumentChunkRecognizer.getDocumentChunkRecognizer();
	public static String getExtendedText_byChunk(BioNEREntity entity)
	{
		Vector<BioNEREntity> chunkEntityVector = chunkRecognizer.getChunksEntities(entity.get_Sentence(), "NP");
		for(BioNEREntity chunkEntity : chunkEntityVector)
		{
			//Remove some unreasonable chunk
			if(chunkEntity.getText().contains(" and ") 
					|| chunkEntity.getText().contains(",")
					|| chunkEntity.getText().contains(" as well as ")
					|| chunkEntity.getText().contains("(")
					|| chunkEntity.getText().contains(")")) continue;
			if(entity.getTokenBeginIndex()>=chunkEntity.getTokenBeginIndex()
					&&entity.getTokenEndIndex()<=chunkEntity.getTokenEndIndex())
			{//Find the chunk covers this entity, return the chunk's text.
				return chunkEntity.getText();
			}
		}
		//No covered chunk found, return the entity's text.
		return entity.getText();
	}

}
