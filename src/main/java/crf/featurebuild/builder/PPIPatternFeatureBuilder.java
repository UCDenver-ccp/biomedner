package crf.featurebuild.builder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.tools.nlptools.PorterUsedStemmer;
import crf.featurebuild.TokenFeatureBuilder;

public class PPIPatternFeatureBuilder implements TokenFeatureBuilder {
	private String[][] m_patterns = null;//The all patterns.
	private PorterUsedStemmer m_stemmer = new PorterUsedStemmer();
	
	private BioNERSentence m_currentSentence = null;//Remember the current sentence to avoid repeat matching.
	private String[] m_currentLabels = null;//The label result for current sentence. We can get result directly from here.
	
	public PPIPatternFeatureBuilder()
	{
		readPatternFile("./data/dict/goodptn.dat");
	}
	
	private void readPatternFile(String filename)
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
			m_patterns = new String[size][];
			freader = new BufferedReader(new FileReader(filename));
			int i=0;
			while((line=freader.readLine()) != null)
			{
				m_patterns[i] = getSinglePattern(line);
				i++;
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
	
	private String[] getSinglePattern(String line)
	{
		line = line.replaceAll("PROTEIN/PROTEIN|[0-9]+", "").trim();
		String[] pattern = line.split("\\s+");
		
		for(int i=0; i<pattern.length; i++)
		{
			int pos = pattern[i].indexOf('/');
			if(pos>0)
			{
				pattern[i] = pattern[i].substring(0, pos);
				pattern[i] = m_stemmer.stem(pattern[i]);
			}
		}
		
		return pattern;
	}
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		if(m_currentSentence != sentence)
		{
			m_currentSentence = sentence;
			matchCurrentSentence();
		}
		if(m_currentLabels == null)
		{
			return "0";
		}
		return m_currentLabels[index];
	}
	
	private void matchCurrentSentence()
	{
		BioNERToken[] bioNERTokens = m_currentSentence.getTokens();
		String[] tokens = new String[bioNERTokens.length];
		m_currentLabels = new String[bioNERTokens.length];
		for(int i=0; i<tokens.length; i++)
		{
			tokens[i] = bioNERTokens[i].getText().toLowerCase();
			tokens[i] = m_stemmer.stem(tokens[i]);
			m_currentLabels[i] = "0";
		}
		
		for(String[] pattern : m_patterns)
		{
			//For each pattern, try to match.
			String[] labels = matchOnePattern(tokens, pattern);
			
			//Add the matching result to current result.
			if(labels != null)
			{
				for(int i=0; i<m_currentLabels.length; i++)
				{
					if(!labels[i].equals("0"))
					{
						m_currentLabels[i] = labels[i];
					}
				}
			}
		}
	}
	private String[] matchOnePattern(String[] tokens, String[] pattern)
	{
		String[] labels = new String[tokens.length];
		int patternIndex = 0;
		for(int i=0; i<tokens.length; i++)
		{
			labels[i] = "0";
		}
		for(int i=0; i<tokens.length; i++)
		{
			if(tokens[i].equals(pattern[patternIndex]))
			{
				labels[i] = tokens[i];
				patternIndex++;
				if(patternIndex==pattern.length)
				{
					return labels;
				}
			}
		}
		return null;
	}

}
