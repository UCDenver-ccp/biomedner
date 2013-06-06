package bioner.application.geneterm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import bioner.application.api.BioNERDocumentOutput;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;

public class GTOutput implements BioNERDocumentOutput {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void outputDocument(BioNERDocument document) {
		// TODO Auto-generated method stub
		if(!haveBothGeneTerm(document))
		{
			return;
		}
		if(!haveGeneTermSentence(document))
		{
			return;
		}
		try {
			String pmid = document.getID();
			BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../GeneTerm/result/"+pmid+".html"));
			for(BioNERSentence sentence : document.getAllSentence())
			{
				
				String sentenceText = sentence.getSentenceText();
				
				if(!haveBothGeneTerm(sentence))
				{
					fwriter.write(sentenceText);
					fwriter.newLine();
					continue;
				}
				
				int entityNum = sentence.getAllEntities().length;
				InsertNode[] insertNodes = new InsertNode[entityNum*2];
				int i=0;
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					
					
					if(entity.get_Type().equals("GENE"))
					{
						insertNodes[i] = new InsertNode(entity.get_Begin(),"<font color=red>");
						i++;
						insertNodes[i] = new InsertNode(entity.get_End(),"</font>");
						i++;
					}
					else
					{
						insertNodes[i] = new InsertNode(entity.get_Begin(),"<font color=blue>");
						i++;
						insertNodes[i] = new InsertNode(entity.get_End(),"</font>");
						i++;
					}
					
				}
				
				sortInsertNode(insertNodes);
				
				StringBuffer strBuffer = new StringBuffer(sentenceText);
				for(i=0; i<insertNodes.length; i++)
				{
					strBuffer.insert(insertNodes[i].getPos(), insertNodes[i].getText());
				}
				sentenceText = strBuffer.toString()+"<br>";
				fwriter.write(sentenceText);
				fwriter.newLine();
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sortInsertNode(InsertNode[] insertNodes)
	{
		for(int i=0; i<insertNodes.length; i++)
		{
			for(int j=i; j<insertNodes.length; j++)
			{
				if(insertNodes[j].getPos()>insertNodes[i].getPos())
				{
					InsertNode temp = insertNodes[j];
					insertNodes[j] = insertNodes[i];
					insertNodes[i] = temp;
				}
			}
		}
	}
	
	private boolean haveBothGeneTerm(BioNERSentence sentence)
	{
		boolean haveGene = false;
		boolean haveTerm = false;
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			if(entity.get_Type().equals("GENE"))
			{
				haveGene = true;
				break;
			}
		}
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			if(entity.get_Type().equals("TERM"))
			{
				haveTerm = true;
				break;
			}
		}
		return haveGene && haveTerm;
	}
	private boolean haveBothGeneTerm(BioNERDocument document)
	{
		boolean haveGene = false;
		boolean haveTerm = false;
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				if(entity.get_Type().equals("GENE"))
				{
					haveGene = true;
					break;
				}
			}
		}
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				if(entity.get_Type().equals("TERM"))
				{
					haveTerm = true;
					break;
				}
			}
		}
		return haveGene && haveTerm;
	}
	
	private boolean haveGeneTermSentence(BioNERDocument document)
	{
		for(BioNERSentence sentence : document.getAllSentence())
		{
			if(haveBothGeneTerm(sentence))
			{
				return true;
			}
		}
		return false;
	}

}
