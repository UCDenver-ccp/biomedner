package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class ChromosomeInSentenceFeatureBuilder extends ChromosomeInDocumentFeatureBuilder implements NormalizationPairFeatureBuilder{
	private static BioNERSentence m_currentSentence= null;
	private static Vector<String> m_currentVector = null;
	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		
		
		if(m_currentSentence != candidate.getEntity().get_Sentence())
		{
			m_currentSentence = candidate.getEntity().get_Sentence();
			m_currentVector = new Vector<String>();
			
			addChromosome(m_currentSentence,m_currentVector);
			
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
}
