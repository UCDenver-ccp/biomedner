package bioner.application.webtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.global.GlobalConfig;
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.SentenceSpliter;

public class StringTextDocumentBuilder implements BioNERDocumentBuilder {

	private static SentenceSpliter sentenceSpliter = NLPToolsFactory.getSentenceSpliter();
	private String m_filename;
	public StringTextDocumentBuilder(String filename)
	{
		m_filename = filename;
	}
	public StringTextDocumentBuilder()
	{
		m_filename = "";
	}
	public static BioNERDocument getOneDocument(String text)
	{
		BioNERDocument doc = new BioNERDocument();
		String id="0000";
		doc.setID(id);
		try {
			BufferedReader freader = new BufferedReader(new StringReader(text));
			String line = freader.readLine();
			if(line!=null)
			{
				doc.setTitle(line);
			}
			line = freader.readLine();
			if(line!=null)
			{
				String[] sentences = sentenceSpliter.sentenceSplit(line);
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
				doc.setAbstractSection(section);
			}
			while((line=freader.readLine())!=null)
			{
				String[] sentences = sentenceSpliter.sentenceSplit(line);
				String paraText = "";
				for(int j=0; j<sentences.length; j++)
				{
					paraText += sentences[j]+ " ";
				}
				BioNERSection section = new BioNERSection();
				BioNERParagraph paragraph = new BioNERParagraph();
				paragraph.setText(paraText);
				paragraph.setSentence(sentences);
				section.addParagraph(paragraph);
				doc.addSection(section);
			}
			
			freader.close();
			doc.linkComponent();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}
	@Override
	public BioNERDocument[] buildDocuments() {
		// TODO Auto-generated method stub
		File root = new File(m_filename);
		File[] files = root.listFiles();
		
		int num = 0;
		Vector<File> fileVector = new Vector<File>();
		for(File file : files)
		{
			if(!file.getName().startsWith("."))
			{
				fileVector.add(file);
			}
		}
		
		BioNERDocument[] docs = new BioNERDocument[fileVector.size()];
		for(int i=0; i<fileVector.size(); i++)
		{
			File file = fileVector.elementAt(i);
			docs[i] = new BioNERDocument();
			String id = file.getName();
			int pos = id.indexOf('.');
			id = id.substring(0, pos);
			docs[i].setID(id);
			try {
				BufferedReader freader = new BufferedReader(new FileReader(file));
				String line = freader.readLine();
				docs[i].setTitle(line);
				freader.readLine();
				line = freader.readLine();
				freader.close();
				String[] sentences = sentenceSpliter.sentenceSplit(line);
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
				docs[i].setAbstractSection(section);
				docs[i].linkComponent();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return docs;
	}

}
