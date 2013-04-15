///////////////////////////////////////////////////////////////
//Usage: This is a class to judge whether a word can used as an index for one term. It will judge a word according to stop words list or other rules.
//Author: Liu Jingchen
//Date: 2009/12/8
///////////////////////////////////////////////////////////////
package bioner.tools.dictionary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.global.GlobalConfig;

public class WordJudge {
	private static Vector<String> stopwordVector = ReadStopWordList();
	private static Vector<String> ReadStopWordList()
	{
		String filename = GlobalConfig.SOTPWORD_LIST_PATH;
		Vector<String> wordVector = new Vector<String>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				line = line.toLowerCase().trim();
				wordVector.add(line);
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wordVector;
	}
	public static boolean isWordIndex(String word)
	{
		if(word.length()<=0) return false;
		if(word.matches("\\s+")) return false;
		word = word.toLowerCase().trim();
		if(stopwordVector.contains(word)) return false;
		return true;
	}
}
