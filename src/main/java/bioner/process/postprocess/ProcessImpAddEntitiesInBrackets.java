package bioner.process.postprocess;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.process.BioNERProcess;

public class ProcessImpAddEntitiesInBrackets implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			processSentence(sentence);
		}
	}
	private void processSentence(BioNERSentence sentence)
	{//In Axxx Bxxx Cxxx ( ABC ), if Axxx Bxxx Cxxx is labeled as an entity, label ABC as well
		BioNERToken[] tokens = sentence.getTokens();
		
		for(int i=1/*We don't need the ( in the beginning*/; i<tokens.length; i++)
		{
			if(tokens[i].getText().equals("(") && i<tokens.length-1)
			{
				if(!tokens[i-1].getLable().equals("O") && tokens[i].getLable().equals("O") && tokens[i+1].getLable().equals("O"))//BOO or IOO
				{
					//Get the position of )
					int end = i;
					for(;end<tokens.length; end++)
					{
						if(tokens[end].getText().equals(")"))
						{
							break;
						}
					}
					
					//Check whether all the tokens between ( and ) is labeled as O
					//We only label them as entity when they are all O
					boolean allIsO = true;
					for(int j=i+1; j<end; j++)
					{
						if(!tokens[j].getLable().equals("O"))
						{
							allIsO = false;
							break;
						}
					}
					if(allIsO)
					{
					
						String labelType = tokens[i-1].getLable().substring(2);//Get the PRO from B-PRO
						tokens[i+1].setLable("B-"+labelType);
						String ILabel = "I-"+labelType;
						for(int j=i+2; j<end; j++)//The ( and ) should not be labeled
						{
							tokens[j].setLable(ILabel);
						}
					}
				}
			}
		}
	}

}
