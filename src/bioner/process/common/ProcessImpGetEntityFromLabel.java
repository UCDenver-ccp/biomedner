package bioner.process.common;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpGetEntityFromLabel implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			processSentence(sentence);
		}
	}
	
	private void processSentence(BioNERSentence sentence)
	{
		sentence.clearEntities();
		BioNERToken[] tokens = sentence.getTokens();
		int beginPos = 0;
		int endPos = 0;
		String currentType = null;
		boolean inEntity = false;
		for(int i=0; i<tokens.length; i++)
		{
			String label = tokens[i].getLable();
			if(label.startsWith("B"))
			{
				if(inEntity)
				{
					endPos = i-1;
					BioNEREntity entity = new BioNEREntity();
					entity.set_Sentence(sentence);
					String typelabel;
					if(currentType.equals("PRO")) typelabel = GlobalConfig.PROTEIN_TYPE_LABEL;
					else typelabel = GlobalConfig.ORGANISM_TYPE_LABEL;
					entity.set_Type(typelabel);
					
					int begin = tokens[beginPos].getBegin();
					int end = tokens[endPos].getEnd();
					entity.set_position(begin, end);
					entity.setTokenBeginIndex(beginPos);
					entity.setTokenEndIndex(endPos);
					sentence.addEntity(entity);
				}
				beginPos = i;
				int pos = label.indexOf('-');
				currentType = label.substring(pos+1);
				inEntity = true;
				
				
			}
			else if(!label.equals("I-"+currentType) && !label.equals("E-"+currentType) && inEntity)
			{
				endPos = i-1;
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				String typelabel;
				if(currentType.equals("PRO")) typelabel = GlobalConfig.PROTEIN_TYPE_LABEL;
				else typelabel = GlobalConfig.ORGANISM_TYPE_LABEL;
				entity.set_Type(typelabel);
				
				int begin = tokens[beginPos].getBegin();
				int end = tokens[endPos].getEnd();
				entity.set_position(begin, end);
				entity.setTokenBeginIndex(beginPos);
				entity.setTokenEndIndex(endPos);
				sentence.addEntity(entity);
				
				inEntity = false;
			}
		}
		if(inEntity)
		{
			endPos = tokens.length-1;
			BioNEREntity entity = new BioNEREntity();
			entity.set_Sentence(sentence);
			String typelabel;
			if(currentType.equals("PRO")) typelabel = GlobalConfig.PROTEIN_TYPE_LABEL;
			else typelabel = GlobalConfig.ORGANISM_TYPE_LABEL;
			entity.set_Type(typelabel);
			
			int begin = tokens[beginPos].getBegin();
			int end = tokens[endPos].getEnd();
			entity.set_position(begin, end);
			entity.setTokenBeginIndex(beginPos);
			entity.setTokenEndIndex(endPos);
			sentence.addEntity(entity);
		}
	}

}
