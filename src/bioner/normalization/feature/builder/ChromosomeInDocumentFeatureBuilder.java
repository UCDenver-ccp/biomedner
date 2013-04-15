package bioner.normalization.feature.builder;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class ChromosomeInDocumentFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	private static BioNERDocument m_currentDocument = null;
	private static Vector<String> m_currentVector = null;
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		
		
		if(m_currentDocument != candidate.getEntity().getDocument())
		{
			m_currentDocument = candidate.getEntity().getDocument();
			m_currentVector = new Vector<String>();
			for(BioNERSentence sentence : m_currentDocument.getAllSentence())
			{
				addChromosome(sentence,m_currentVector);
			}
		}
		
		Vector<String> chromoVector = m_currentVector;
		
		if(chromoVector.isEmpty()) return "0";
		
		String chromoStr = candidate.getRecord().getAttribute("chromosome");
		if(chromoStr==null) return "0";
		chromoStr = chromoStr.toLowerCase();
		for(String chromoInContext : chromoVector)
		{
			if(chromoStr.equals(chromoInContext)) return "1";
		}
		
		return "0";
	}

	protected void addChromosome(BioNERSentence sentence, Vector<String> chromosomeVector)
	{
		String sentenceText = sentence.getSentenceText();
		Pattern pattern = Pattern.compile("\\b[0-9]+[qp][0-9\\W]+\\b");
		Matcher matcher = pattern.matcher(sentenceText);
		while(matcher.find())
		{
			String subStr = sentenceText.substring(matcher.start(), matcher.end());
			int pos = subStr.indexOf('q');
			if(pos<0) pos = subStr.indexOf('p');
			String chromoStr = subStr.substring(0, pos);
			chromosomeVector.add(chromoStr);
		}
		pattern = Pattern.compile("\\bchromosome [0-9xXyY]+\\b");
		matcher = pattern.matcher(sentenceText);
		while(matcher.find())
		{
			String subStr = sentenceText.substring(matcher.start(), matcher.end());
			int pos = subStr.indexOf(' ');
			String chromoStr = subStr.substring(pos+1);
			chromosomeVector.add(chromoStr.toLowerCase());
		}
	}
}
