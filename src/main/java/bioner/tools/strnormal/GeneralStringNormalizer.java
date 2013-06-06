///////////////////////////////////////////////////////////////
//Usage: This is a implement for interface StringNormalizer.
//		It uses the general method to normalize a string. It read some replace rules from a file, and then replace the word in the string.
//Author: Liu Jingchen
//Date: 2009/12/21
///////////////////////////////////////////////////////////////
package bioner.tools.strnormal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.global.GlobalConfig;
import bioner.tools.nlptools.IteratedLovinsStemmer;

public class GeneralStringNormalizer implements StringNormalizer {

	private Vector<ReplacePair> ruleVector = new Vector<ReplacePair>();
	private IteratedLovinsStemmer stemmer = new IteratedLovinsStemmer();
	public GeneralStringNormalizer()
	{
		readReplaceRulePairFile(GlobalConfig.REPLACE_RULE_FILE_PATH);
	}
	@Override
	public String normalizeString(String str) {
		// TODO Auto-generated method stub
		String normalStr = str.toLowerCase();
		
		if(str.contains("("))
		{
			int i=0;
			i++;
		}
		
		for(ReplacePair rule : ruleVector)
		{
			normalStr = normalStr.replaceAll(rule.getOrignalStr(), rule.getNewStr());
		}
		if(normalStr.endsWith(".")) 
		{
			normalStr = normalStr.substring(0, normalStr.length()-1);
		}
		String[] parts = normalStr.split("\\s+|\\~");
		normalStr = "";
		for(int i=0; i<parts.length; i++)
		{
			
			parts[i] = toSingle(parts[i]);
			
			
			normalStr += parts[i];
			if(i<parts.length-1)
			{
				normalStr += " ";
			}
		}
		normalStr = normalStr.trim();
		return normalStr;
	}

	private void readReplaceRulePairFile(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				if(line.contains("|"))
				{
					ReplacePair pair = new ReplacePair();
					pair.ReadStrRule(line);
					ruleVector.add(pair);
				}
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String toSingle(String word)
	{
		String newWord = word;
		int length = word.length();
		if(word.endsWith("ies"))
		{
			
			newWord = word.substring(0,length-3)+"y";
		}
		else if(word.endsWith("s")&& length>3)
		{
			
			newWord = word.substring(0,length-1);
		}
		else if(word.endsWith("men"))
		{
			newWord = word.substring(0,length-3)+"man";
		}
		else if(word.equals("chicken"))
		{
			newWord = "chick";
		}
		else if(word.equals("mice"))
		{
			newWord = "mouse";
		}
		return newWord;
	}
}
