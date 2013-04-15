package crf.featurebuild.builder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import crf.featurebuild.TokenFeatureBuilder;

public class AminoAcidPositionFeatureBuilder implements TokenFeatureBuilder {

	private Vector<String> wordVector = new Vector<String>();
	private boolean m_caseSense = false;
	public AminoAcidPositionFeatureBuilder(String filename, boolean casesense)
	{
		m_caseSense = casesense;
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					if(!m_caseSense)
					{
						line = line.toLowerCase();
					}
					line = line.trim();
					wordVector.add(line);
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
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		String tokenStr = tokens[index].getText();
		if(!m_caseSense)
		{
			tokenStr = tokenStr.toLowerCase();
		}
		tokenStr = tokenStr.trim();
		for(String word : wordVector)
		{
			if(tokenStr.matches(word+"[0-9]+")) return "1";
		}
		return "0";
	}

}
