package bioner.process.postprocess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpAddSimilarEntities implements BioNERProcess {

	protected String[] greekList = null;
	protected String[] stopwords= null;
	
	public ProcessImpAddSimilarEntities()
	{
		readStopWords(GlobalConfig.SOTPWORD_LIST_PATH);
		readGreekNumFile(GlobalConfig.GREEK_LIST_FILEPATH);
	}
	
	private void readStopWords(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int size = 0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					size++;
				}
			}
			freader.close();
			stopwords = new String[size];
			freader = new BufferedReader(new FileReader(filename));
			int i=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					stopwords[i] = line.toLowerCase().trim();
					i++;
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
	protected void readGreekNumFile(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int size = 0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					size++;
				}
			}
			freader.close();
			greekList = new String[size];
			freader = new BufferedReader(new FileReader(filename));
			int num=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					greekList[num] = line.toLowerCase().trim();
					num++;
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
	
	protected boolean isGreek(String word)
	{
		String lowerCaseWord = word.toLowerCase().trim();
		for(int i=0; i<greekList.length; i++)
		{
			if(greekList[i].equals(lowerCaseWord)) return true;
		}
		return false;
	}
	protected boolean isRomaNumber(String word)
	{
		if(word.matches("[IVXivx]+")) return true;
		return false;
	}
	protected boolean isUsefulWord(String word)
	{
		if(word==null) return false;
		if(word.length()<=1) return false;
		String lowerCaseWord = word.toLowerCase();
		for(int i=0; i<this.stopwords.length; i++)
		{
			if(stopwords[i].equals(lowerCaseWord)) return false;
		}
		if(word.contains("DNA") || word.contains("RNA")) return false;//remove DNA and RNA
		
		
		if(word.matches("\\-{0,1}[0-9]+(\\.[0-9]+){0,1}")) return false;
		if(word.matches("\\W+")) return false; 
		if(!word.matches("\\w+")) return false;//remove special chars like .,
		
		if(isRomaNumber(word)) return false;//remove Roman numbers
		if(isGreek(word)) return false;//remove Greek
		if(word.matches("[A-Z][a-z]+")) return false;//Remove words with only initial letter capital
		//only keep the words with capital letters or digital
		for(int i=0; i<word.length(); i++)
		{
			char c = word.charAt(i);
			if(c>='A' && c<='Z') return true;
			if(c>='0' && c<='9') return true;
		}
		
		return false;
	}
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNERToken[] tokens = sentence.getTokens();
			for(int i=0; i<tokens.length; i++)
			{
				if(!tokens[i].getLable().equals("O") && isUsefulWord(tokens[i].getText()))
				{
					for(BioNERSentence sentenceOther : document.getAllSentence())
					{
						BioNERToken[] tokensOther = sentenceOther.getTokens();
						for(int j=0; j<tokensOther.length; j++)
						{
							if(isSimilar(tokens[i].getText(), tokensOther[j].getText()) && tokensOther[j].getLable().equals("O") && isUsefulWord(tokensOther[j].getText()))
							{
								int begin_i = i;
								int begin_j = j;
								while(begin_i>0 && begin_j>0)
								{
									if(isSimilar(tokens[begin_i-1].getText(), tokensOther[begin_j-1].getText()) && !tokens[begin_i-1].getLable().equals("O"))
									{
										begin_i--;
										begin_j--;
									}
									else
									{
										break;
									}
								}
								int end_i = i;
								int end_j = j;
								while(end_i<tokens.length-1 && end_j<tokensOther.length-1)
								{
									if(isSimilar(tokens[end_i+1].getText(), tokensOther[end_j+1].getText())&& !tokens[end_i+1].getLable().equals("O"))
									{
										end_i++;
										end_j++;
									}
									else
									{
										break;
									}
								}//while find end_j
								String labelType = tokens[i].getLable().substring(2);
								tokensOther[begin_j].setLable("B-"+labelType);
								String ILabel = "I-"+labelType;
								for(int k=begin_j+1; k<=end_j; k++)
								{
									tokensOther[k].setLable(ILabel);
								}
							}//if
						}//for tokensOther
					}//for sentenceOther
				}//if
			}
		}
	}
	
	protected boolean isSimilar(String  word_1, String word_2)
	{
		if(word_1==null || word_2==null) return false;
		
		//remove stop word
		String lowerCaseWord = word_1.toLowerCase();
		for(int i=0; i<this.stopwords.length; i++)
		{
			if(stopwords[i].equals(lowerCaseWord)) return false;
		}
		lowerCaseWord = word_2.toLowerCase();
		for(int i=0; i<this.stopwords.length; i++)
		{
			if(stopwords[i].equals(lowerCaseWord)) return false;
		}
		
		if(word_1.matches("\\W") && !word_1.equals("-")) return false;
		if(word_2.matches("\\W") && !word_2.equals("-")) return false;
		
		
		if(word_1.equals(word_2)) return true;
		
		
		
		
		//when both are int
		if(word_1.matches("[0-9]+") && word_2.matches("[0-9]+")) return true;
		
		
		String normWord_1 = word_1.replaceAll("[0-9]+", "");
		String normWord_2 = word_2.replaceAll("[0-9]+", "");
		
		//prefix
		int prefixLength = 2;
		if(normWord_1.length()>prefixLength && normWord_2.length()>prefixLength)
		{
			String prefix_1 = normWord_1.substring(0, prefixLength);
			String prefix_2 = normWord_2.substring(0, prefixLength);
			if(prefix_1.equals(prefix_2)) return true;
		}
		
		//suffix
		int suffixLength = 2;
		if(normWord_1.length()>suffixLength && normWord_2.length()>suffixLength)
		{
			String suffix_1 = normWord_1.substring(normWord_1.length()- prefixLength);
			String suffix_2 = normWord_2.substring(normWord_2.length()- prefixLength);
			if(suffix_1.equals(suffix_2)) return true;
		}
		
		//Greek and Roman
		if(isGreek(word_1) && isGreek(word_2)) return true;
		if(isRomaNumber(word_1) && isRomaNumber(word_2)) return true;
		
		//Single capital 
		if(word_1.matches("[A-Z]") && word_2.matches("[A-Z]")) return true;
		
		//change digital into 1
		normWord_1 = word_1.replaceAll("[0-9]+", "1");
		normWord_2 = word_2.replaceAll("[0-9]+", "1");
		if(normWord_1.equals(normWord_2)) return true;
		
		return false;
	}

}
