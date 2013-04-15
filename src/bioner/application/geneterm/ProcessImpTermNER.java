package bioner.application.geneterm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.process.BioNERProcess;

public class ProcessImpTermNER implements BioNERProcess {
	private Vector<String> termVector = new Vector<String>();
	
	public ProcessImpTermNER()
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader("../../GeneTerm/termlist.txt"));
			String line;
			while((line=freader.readLine()) != null)
			{
				line = line.toLowerCase().trim();
				if(line.length()>0)
				{
					termVector.add(line);
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
		for(BioNERSentence sentence : document.getAllSentence())
		{
			ProcessSentence(sentence);
		}
	}
	private void ProcessSentence(BioNERSentence sentence)
	{
		String sentenceText = sentence.getSentenceText().toLowerCase();
		for(String termText : termVector)
		{
			int pos = 0;
			while(pos>=0 && pos<sentenceText.length())
			{
				pos = sentenceText.indexOf(termText, pos);
				if(pos>=0)
				{
					BioNEREntity entity = new BioNEREntity();
					entity.set_Type("TERM");
					entity.set_Sentence(sentence);
					entity.set_position(pos, pos+termText.length());
					sentence.addEntity(entity);
					pos++;
				}
			}
		}
	}

}
