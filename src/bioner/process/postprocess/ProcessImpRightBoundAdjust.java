package bioner.process.postprocess;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpRightBoundAdjust implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			adjustSentence(sentence);
		}
		
	}
	
	private void adjustSentence(BioNERSentence sentence)
	{
		expandRightBound(sentence);
		shrinkRightBound(sentence);
		expandRightSamePattern(sentence);
		splitByANDComma(sentence);
	}
	
	private void splitByANDComma(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		for(int i=1; i<tokens.length-1; i++)
		{
			if(tokens[i].getLable().equals("O")) continue;
			String tokenStr = tokens[i].getText().toLowerCase().trim();
			if(tokenStr.equals("and") || tokenStr.equals(","))
			{
				if(!isRightBound(tokens, i-1) && !isRightBound(tokens, i+1))
				{
					tokens[i].setLable("O");
					if(!tokens[i+1].getLable().equals("O"))
					{
						String label = tokens[i+1].getLable();
						int pos = label.indexOf('-');
						String newLabel = "B-"+label.substring(pos+1);
						tokens[i+1].setLable(newLabel);
					}
				}
			}
		}
	}

	private void shrinkRightBound(BioNERSentence sentence)
	{
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
					shrinkByLeftBrackets(tokens, beginPos, endPos);
				}
				beginPos = i;
				int pos = label.indexOf('-');
				currentType = label.substring(pos+1);
				inEntity = true;
				
				
			}
			else if(!label.equals("I-"+currentType) && !label.equals("E-"+currentType) && inEntity)
			{
				endPos = i-1;
				shrinkByLeftBrackets(tokens, beginPos, endPos);
				inEntity = false;
			}
		}
		if(inEntity)
		{
			endPos = tokens.length-1;
			shrinkByLeftBrackets(tokens, beginPos, endPos);
		}
	}
	private void shrinkByLeftBrackets(BioNERToken[] tokens, int beginPos, int endPos)
	{
		int leftBracketNum = 0;
		int rightBracketNum = 0;
		for(int i=beginPos; i<=endPos; i++)
		{
			String tokenStr = tokens[i].getText();
			if(tokenStr.equals("("))
			{
				leftBracketNum++;
			}
			else if(tokenStr.equals(")"))
			{
				rightBracketNum++;
			}
		}
		if(leftBracketNum!=rightBracketNum)
		{
			for(int i=beginPos; i<=endPos; i++)
			{
				String tokenStr = tokens[i].getText();
				if(tokenStr.equals("(") || tokenStr.equals(")"))
				{
					tokens[i].setLable("O");
				}
			}
		}
	}
	

	private void expandRightBound(BioNERSentence sentence)
	{
		BioNERToken[] tokens = sentence.getTokens();
		
		
		int length = tokens.length-1;
		for(int i=0; i<length; i++)
		{
			String currentLabel = tokens[i].getLable();
			String nextLabel = tokens[i+1].getLable();
			if(!currentLabel.equals("O")&&nextLabel.equals("O"))
			{
				boolean isBound = isRightBound(tokens, i+1);
				if(isBound)
				{
					int pos = currentLabel.indexOf('-');
					String newNextLabel = "I-"+currentLabel.substring(pos+1);
					tokens[i+1].setLable(newNextLabel);
				}
			}
		}
		
	}
	
	private void expandRightSamePattern(BioNERSentence sentence)
	{
		BioNERToken[] tokens = sentence.getTokens();
		
		
		int length = tokens.length-1;
		for(int i=0; i<length; i++)
		{
			String currentLabel = tokens[i].getLable();
			String nextLabel = tokens[i+1].getLable();
			if(!currentLabel.equals("O")&&nextLabel.equals("O"))
			{
				boolean hasBound =	false;
				String pattern = null;
				for(int j=i; j>=0; j--)
				{
					if(tokens[j].getLable().startsWith("B"))
					{
						break;
					}
					if(isRightBound(tokens, j))
					{
						hasBound = true;
						pattern = getBoundPattern(tokens[j].getText());
						break;
					}
				}
				if(hasBound)
				{
					int pos = currentLabel.indexOf('-');
					String newNextLabel = "I-"+currentLabel.substring(pos+1);
				
					for(int j=i+1; j<tokens.length; j++)
					{
						boolean isInterval = isIntervalToken(tokens, j);
						boolean isBound = getBoundPattern(tokens[j].getText()).equals(pattern);
						if(isBound)
						{
							for(int k=i+1; k<=j; k++)
							{
								tokens[k].setLable(newNextLabel);
							}
						}
						else if(!isInterval)
						{
							break;
						}
						
					}
				}
			}
		}
	}
	private String getBoundPattern(String word)
	{
		String pattern = "";
		for(int i=0; i<word.length(); i++)
		{
			Character c = word.charAt(i);
			if(c>='a' && c<='z')
			{
				pattern += "a";
			}
			else if(c>='A' && c<='Z')
			{
				pattern += "A";
			}
			else if(c>='0' && c<='9')
			{
				pattern += "1";
			}
			else 
			{
				pattern += c.toString();
			}
		}
		return pattern;
	}
	
	
	private boolean isRightBound(BioNERToken[] tokens, int index)
	{
		BioNERToken token = tokens[index];
		String tokenStr = token.getText();
		if(tokenStr.length()>4) return false;
		if(tokenStr.matches("[0-9]+")) return true;
		if(tokenStr.matches("[a-zA-Z][0-9]*")) return true;
		if(tokenStr.matches("[ivxdlcm]+|[IVXDLCM]+")) return true;
		if(tokenStr.matches("\\)")) return true;
		return false;
	}
	private boolean isIntervalToken(BioNERToken[] tokens, int index)
	{
		BioNERToken token = tokens[index];
		String tokenStr = token.getText();
		if(tokenStr.matches("\\,|\\-|and")) return true;
		return false;
	}
}
