package bioner.process.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpEntityFilter implements BioNERProcess {

	private Vector<String> m_tabuFormatVector = new Vector<String>();
	
	public ProcessImpEntityFilter()
	{
		readTabuFormatFile(GlobalConfig.ENTITYFILTER_TABULIST_PATH);
	}
	
	private void readTabuFormatFile(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					m_tabuFormatVector.add(line.toLowerCase().trim());
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
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		filterSentences(document.getAllSentence());
	}
	
	private void filterSentences(BioNERSentence[] sentences)
	{
		for(BioNERSentence sentence : sentences)
		{
			BioNEREntity[] entities = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entities)
			{
				if(isEntityOK(entity, entities))
				{
					sentence.addEntity(entity);
				}
			}
		}
	}
	private boolean isEntityOK(BioNEREntity entity, BioNEREntity[] entities)
	{
		String entityWord = entity.getText().toLowerCase().trim();
		for(String exp : m_tabuFormatVector)
		{
			if(entityWord.matches(exp)) return false;
			if(entityWord.length()>3&&entityWord.endsWith("s"))
			{
				String singleWord = entityWord.substring(0, entityWord.length()-1);
				if(singleWord.matches(exp)) return false;
			}
		}
		
		//To check overlap between entities. If a entity is overlapped by larger entity, drop it.
		for(BioNEREntity otherEntity : entities)
		{
			if(otherEntity==entity) continue;
			
			
			if(otherEntity.get_Begin()<=entity.get_Begin()&&otherEntity.get_End()>=entity.get_End())
			{
				//****************
				//   *********
				return false;
			}else if(otherEntity.get_Begin()>=entity.get_End()&&entity.get_Begin()>otherEntity.get_Begin())
			{
				//********
				//   *********
				return false;
			}else if(entity.get_Begin()>=otherEntity.get_End()&&otherEntity.get_Begin()>entity.get_Begin())
			{
				//    *********
				// *********
				return false;
			}
		}
		return true;
	}
}
