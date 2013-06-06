package bioner.process.postprocess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpPostProcess implements BioNERProcess {

private Vector<String> m_tabuFormatVector = new Vector<String>();
	
	public ProcessImpPostProcess()
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
				if(isEntityOK(entity, sentence))
				{
					sentence.addEntity(entity);
				}
			}
		}
	}
	private boolean isEntityOK(BioNEREntity entity, BioNERSentence sentence)
	{
		BioNERToken[] tokens = sentence.getTokens();
		
		int beginTokenIndex = -1;
		int endTokenIndex = -1;
		int min = Integer.MAX_VALUE;
		for(int i=0; i<tokens.length; i++)
		{
			if(tokens[i].getBegin()<=entity.get_Begin() && tokens[i].getEnd()>entity.get_Begin())
			{
				beginTokenIndex = i;
				break;
			}
			int dis = tokens[i].getBegin() - entity.get_Begin();
			if(dis<0) dis = -dis;
			if(dis<min)
			{
				beginTokenIndex = i;
				min = dis;
			}
		}
		
		min = Integer.MAX_VALUE;
		for(int i=0; i<tokens.length; i++)
		{
			if(tokens[i].getBegin()<entity.get_End() && tokens[i].getEnd()>=entity.get_End())
			{
				endTokenIndex = i;
				break;
			}
			int dis = tokens[i].getEnd() - entity.get_End();
			if(dis<0) dis = -dis;
			if(dis<min)
			{
				endTokenIndex = i;
				min = dis;
			}
		}
		
		for(int i= beginTokenIndex; i<=endTokenIndex; i++)
		{
			if(!tokens[i].getTermVector().isEmpty())
			{
				return true;
			}
		}
		
		return false;
	}
}
