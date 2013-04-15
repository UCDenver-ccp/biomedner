package bioner.normalization;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.global.GlobalConfig;
import bioner.tools.dictionary.WordJudge;

/**
 * This class is a tokenizer for gene mentions recognized from context, as well as from knowledge base.
 * @author Liu Jingchen
 *
 */
public class GeneMentionTokenizer {
	
	private static Vector<String> greakVector = getGreekList(GlobalConfig.GREEK_LIST_FILEPATH);
	private static Vector<String> getGreekList(String filename)
	{
		Vector<String> list = new Vector<String>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				line = line.toLowerCase().trim();
				if(line.length()>0)
					list.add(line);
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * Tokenize the given String.
	 * @param mention String the String to tokenize.
	 * @return A vector of tokens. The set of token has no order. Repetitive tokens only kept one.  
	 */
	public static Vector<String> getTokens(String mention)
	{
		Vector<String> tokenVector = new Vector<String>();
		
		//String[] parts = mention.split("((\\W&^\\.)|\\-|\\_)+");
		String[] parts = mention.split("\\W+|\\_+");
		
		for(String part : parts)
		{
			//if(!WordJudge.isWordIndex(part) && !part.matches("[a-zA-Z]")) continue;//Exclude stop words.
			//if(part.equals("gene") || part.equals("protein")) continue;//Exclude stop words.
			//But don't filter single letters like I or a. This can be a part of gene name.
			
			int begin = 0;
			while(begin>=0)
			{
				int pos = getNextSplitPostion(part, begin);
				if(pos<0) break;
				String tokenStr = part.substring(begin, pos);
				//if(!tokenVector.contains(tokenStr))
				if(tokenStr.length()>0)
				{
					
					for(String token : splitGreek(tokenStr))
					{
						if(token.matches("[ivxIVX]+"))
						{
							token = convertRomanNumber(token);
						}
						//tokenVector.add(token.toLowerCase().trim());
						tokenVector.add(token.trim());
					}
				}
				begin = pos;
			}
			
		}
		
		return tokenVector;
	}
	private static Vector<String> splitGreek(String str)
	{
		Vector<String> resultVector = new Vector<String>();
		for(String greekStr : greakVector)
		{
			if(str.endsWith(greekStr))
			{
				resultVector.add(greekStr);
				str = str.replaceAll(greekStr, " ");
			}
		}
		str = str.trim();
		if(str.length()>0)
			resultVector.add(str);
		return resultVector;
	}
	
	/**
	 * 
	 * @param str String 
	 * @param begin int the begin position
	 * @return The next position to split the given String, from the begin position.
	 */
	public static int getNextSplitPostion(String str, int begin)
	{
		if(begin >= str.length()) return -1;
		int length = str.length() - 1;
		
		for(int i=begin; i<length; i++)
		{
			char currentChar = str.charAt(i);
			char nextChar = str.charAt(i+1);
			
			//letter to digit
			if(Character.isLetter(currentChar) && Character.isDigit(nextChar)) return i+1;
			//digit to letter
			if(Character.isDigit(currentChar) && Character.isLetter(nextChar)) return i+1;
			//lower case to upper case
			if(Character.isLowerCase(currentChar) && Character.isUpperCase(nextChar)) return i+1;
		}
		return str.length();
	}
	
	/**
	 * Judge whether a string is a Greek letter.
	 * @param str String
	 * @return boolean whether it is a Greek letter.
	 */
	public static boolean isGreek(String str)
	{
		return greakVector.contains(str.toLowerCase().trim());
	}
	
	private static String convertRomanNumber(String numberStr)
	{
		numberStr = numberStr.toLowerCase().trim();
		if(numberStr.equals("i")) return "1";
		if(numberStr.equals("ii")) return "2";
		if(numberStr.equals("iii")) return "3";
		if(numberStr.equals("iv")) return "4";
		if(numberStr.equals("v")) return "5";
		if(numberStr.equals("vi")) return "6";
		if(numberStr.equals("vii")) return "7";
		if(numberStr.equals("viii")) return "8";
		if(numberStr.equals("ix")) return "9";
		if(numberStr.equals("x")) return "10";
		return numberStr;
	}
	
	public static void main(String[] args)
	{
		String str = "semaphorin A";
		GeneMentionTokenizer tokenizer = new GeneMentionTokenizer();
		Vector<String> tokenVector = tokenizer.getTokens(str);
		for(String token : tokenVector)
		{
			System.out.print(token+" ");
		}
	}
}
