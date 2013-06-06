package bioner.normalization;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.process.BioNERProcess;

public class ProcessImpGetCandidateID implements BioNERProcess {
	private CandidateFinder m_finder = null;
	
	public ProcessImpGetCandidateID(CandidateFinder finder)
	{
		m_finder = finder;
	}
	
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			ProcessSentence(sentence);
		}
	}
	private void ProcessSentence(BioNERSentence sentence)
	{
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			BioNERCandidate[] candidates = m_finder.getCandidatesForGeneMentionString(entity.getText());
			entity.setCandidates(candidates);
		}
	}
	
	
}
