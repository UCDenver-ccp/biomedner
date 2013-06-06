package bioner.application.geneterm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.SentenceSpliter;

public class GTDocumentBuilder implements BioNERDocumentBuilder {
	private SentenceSpliter sentenceSpliter = NLPToolsFactory.getSentenceSpliter();
	@Override
	public BioNERDocument[] buildDocuments() {
		// TODO Auto-generated method stub
		File root = new File("../../GeneTerm/data");
		File[] files = root.listFiles();
		BioNERDocument[] documents = new BioNERDocument[files.length];
		
		for(int i=0; i<files.length; i++)
		{
			try {
				BufferedReader freader = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
				String line;
				String fileText = "";
				while((line=freader.readLine()) != null)
				{
					if(line.length()>0)
					{
						fileText += line+" ";
					}
				}
				freader.close();
				documents[i] = new BioNERDocument();
				String id = files[i].getName();
				int pos = id.indexOf('.');
				id = id.substring(0,pos);
				documents[i].setID(id);
				
				String[] sentences = sentenceSpliter.sentenceSplit(fileText);
				String absText = "";
				for(int j=0; j<sentences.length; j++)
				{
					absText += sentences[j]+ " ";
				}
				BioNERSection section = new BioNERSection();
				BioNERParagraph paragraph = new BioNERParagraph();
				paragraph.setText(absText);
				paragraph.setSentence(sentences);
				section.addParagraph(paragraph);
				documents[i].setAbstractSection(section);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return documents;
	}

}
