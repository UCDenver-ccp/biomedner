package bioner.process.common;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpSetLable implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			setSentenceLabel(sentence);
		}
	}
	
	private void setSentenceLabel(BioNERSentence sentence)
	{
		BioNERToken[] tokens = sentence.getTokens();
		for(int i=0; i<tokens.length; i++)
		{
			tokens[i].setLable("O");
		}
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			int beginTokenIndex = -1;
			int endTokenIndex = -1;
			int min = Integer.MAX_VALUE;
			for(int i=0; i<tokens.length; i++)
			{
				if(tokens[i].getBegin()<=entity.get_Begin() && tokens[i].getEnd()>entity.get_Begin())
				{
					beginTokenIndex = i;
					break;
				}
				int dis = tokens[i].getBegin() - entity.get_Begin();
				if(dis<0) dis = -dis;
				if(dis<min)
				{
					beginTokenIndex = i;
					min = dis;
				}
			}
			
			min = Integer.MAX_VALUE;
			for(int i=0; i<tokens.length; i++)
			{
				if(tokens[i].getBegin()<entity.get_End() && tokens[i].getEnd()>=entity.get_End())
				{
					endTokenIndex = i;
					break;
				}
				int dis = tokens[i].getEnd() - entity.get_End();
				if(dis<0) dis = -dis;
				if(dis<min)
				{
					endTokenIndex = i;
					min = dis;
				}
			}
			
			entity.setTokenBeginIndex(beginTokenIndex);
			entity.setTokenEndIndex(endTokenIndex);
			
			String BStr, IStr, EStr;
			if(entity.get_Type().equals(GlobalConfig.PROTEIN_TYPE_LABEL))
			{
				BStr = GlobalConfig.PROTEIN_TOKEN_B_LABEL;
				IStr = GlobalConfig.PROTEIN_TOKEN_I_LABEL;
				//EStr = GlobalConfig.PROTEIN_TOKEN_E_LABEL;
				EStr = GlobalConfig.PROTEIN_TOKEN_I_LABEL;
			}else
			{
				BStr = GlobalConfig.ORGANISM_TOKEN_B_LABEL;
				IStr = GlobalConfig.ORGANISM_TOKEN_I_LABEL;
				//EStr = GlobalConfig.ORGANISM_TOKEN_E_LABEL;
				EStr = GlobalConfig.ORGANISM_TOKEN_I_LABEL;
			}
			tokens[beginTokenIndex].setLable(BStr);
			for(int i= beginTokenIndex+1; i<=endTokenIndex-1; i++)
			{
				tokens[i].setLable(IStr);
			}
			if(endTokenIndex>beginTokenIndex)
			{
				tokens[endTokenIndex].setLable(EStr);
			}
		}
	}

}
