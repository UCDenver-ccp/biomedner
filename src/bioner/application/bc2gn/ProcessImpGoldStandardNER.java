package bioner.application.bc2gn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.process.BioNERProcess;

public class ProcessImpGoldStandardNER implements BioNERProcess {

	private HashMap<String, Vector<String>> m_entityTable = new HashMap<String, Vector<String>>();
	
	public ProcessImpGoldStandardNER(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\t+");
				if(parts.length<3) continue;
				String docID = parts[0];
				Vector<String> entityVector = m_entityTable.get(docID);
				if(entityVector==null)
				{
					entityVector = new Vector<String>();
					m_entityTable.put(docID, entityVector);
				}
				for(int i=2; i<parts.length; i++)
				{
					entityVector.add(parts[i]);
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
		Vector<String> entityVector = m_entityTable.get(document.getID());
		for(BioNERSentence sentence : document.getAllSentence())
		{
			ProcessSentence(sentence, entityVector);
		}
	}
	
	private void ProcessSentence(BioNERSentence sentence, Vector<String> entityVector)
	{
		if(entityVector==null) return;
		String text = sentence.getSentenceText();
		Vector<BioNEREntity> entityAllVector = new Vector<BioNEREntity>();
		for(String entityStr : entityVector)
		{
			int begin = 0;
			int pos = text.indexOf(entityStr);
			while(pos>=0)
			{
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				int end = pos+entityStr.length()-1;
				entity.set_position(pos, end);
				entity.addLabel("gold");
				entityAllVector.add(entity);
				begin = end+1;
				if(begin>=text.length()) break;
				pos = text.indexOf(entityStr, begin);
			}
		}
		
		//drop the entities covered by others.
		for(BioNEREntity entity : entityAllVector)
		{
			boolean covered = false;
			for(BioNEREntity otherEntity : entityAllVector)
			{
				if(otherEntity == entity) continue;
				if(entity.get_Begin()>=otherEntity.get_Begin()
						&& entity.get_End()<= otherEntity.get_End())
				{
					covered = true;
					break;
				}
			}
			if(!covered) sentence.addEntity(entity);
		}
	}

}
