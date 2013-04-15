package bioner.normalization;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.BioNERRecord;
import bioner.normalization.data.database.DatabaseReader;
import bioner.normalization.data.database.DatabaseReaderFactory;
import bioner.normalization.feature.builder.NCBIRankFinder;
import bioner.process.BioNERProcess;

public class ProcessImpAddIDGM implements BioNERProcess {

	
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		m_databaseReader.connect();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			processSentence(sentence);
			//filterBySection(sentence);
		}
		m_databaseReader.close();
	}
	
	private Pattern m_pattern = Pattern.compile("\\b[A-Z]{2,}\\_?[0-9]{3,}\\b");
	private NCBIRankFinder m_ncbiRank = new NCBIRankFinder();
	private DatabaseReader m_databaseReader = DatabaseReaderFactory.createDatabaseReader();
	private void processSentence(BioNERSentence sentence)
	{
		String sentenceText = sentence.getSentenceText();
		Matcher matcher = m_pattern.matcher(sentenceText);
		while(matcher.find())
		{
			int begin = matcher.start();
			int end = matcher.end();
			String gmText = sentenceText.substring(begin, end);
			String[] ids = m_ncbiRank.getRank(gmText);
			if(ids.length==1)
			{
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				entity.set_position(begin, end-1);
				entity.addLabel(GlobalConfig.ENTITY_LABEL_IDGM);
				BioNERCandidate candidate = new BioNERCandidate();
				candidate.setRecordID(ids[0]);
				HashMap<String, BioNERRecord> recordTable = m_databaseReader.searchRecords(ids);
				BioNERRecord record = recordTable.get(ids[0]);
				if(record!=null)
				{
					candidate.setEntity(entity);
					candidate.setRecord(record);
					entity.setCandidates(new BioNERCandidate[]{candidate});
					sentence.addEntity(entity);
				}
			}
		}
	}
	
	private void filterBySection(BioNERSentence sentence)
	{		
		BioNERSection currentSection = sentence.getSection();
		while(currentSection!=null)
		{
			BioNERSentence titleSentence =  currentSection.getTitleSentence();
			if(titleSentence!=null)
			{
				String sectionTitle = titleSentence.getSentenceText().toLowerCase().trim();
				if(sectionTitle.contains("material") || sectionTitle.contains("method") || sectionTitle.contains("supporting") || sectionTitle.contains("supplementary"))
				{
					sentence.clearEntities();
					break;
				}
			}
			String type = currentSection.getType();
			if(type!=null)
			{
				if(type.contains("material") || type.contains("method") || type.contains("supporting") || type.contains("supplementary"))
				{
					sentence.clearEntities();
					break;
				}
			}
			currentSection = currentSection.getParentSection();
		}
	}

}
