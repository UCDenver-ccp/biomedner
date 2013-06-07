package bioner.application.webtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.SentenceSpliter;

public class PlainTextDocumentBuilder implements BioNERDocumentBuilder {

	private static SentenceSpliter sentenceSpliter = NLPToolsFactory.getSentenceSpliter();
	private String m_filename;

	public PlainTextDocumentBuilder(String filename)
	{
		m_filename = filename;
	}
	public PlainTextDocumentBuilder()
	{
		m_filename = "";
	}
	public static BioNERDocument getOneDocument(String filename)
	{
		BioNERDocument doc = new BioNERDocument();
		int currentDocLength=0;
		File file = new File(filename);
		String id = file.getName();
		int pos = id.indexOf('.');
		id = id.substring(0, pos);
		doc.setID(id);
		try {
			BufferedReader freader = new BufferedReader(new FileReader(file));

			// title 
			String line = freader.readLine();
			doc.setTitle(line);
			currentDocLength += line.length();
			System.out.println("PlainTextDocumentBuilder.getOneDocument(): docLength: " + currentDocLength);

			// abstract
			line = freader.readLine();

			String[] sentences = sentenceSpliter.sentenceSplit(line);
			String absText = "";
			for (int j=0; j<sentences.length; j++) {
				// CHECK: potentially adding space to source texte
				// Q: does the splitter take these out? 
				// Why is this here?? It makes the paragraph text different from the combined sentence text.
				absText += sentences[j]+ " ";
			}
			BioNERSection section = new BioNERSection();
			BioNERParagraph paragraph = new BioNERParagraph(currentDocLength);
			paragraph.setText(absText);
			paragraph.setSentence(sentences);
			section.addParagraph(paragraph);
			doc.setAbstractSection(section);

			currentDocLength += line.length();
			System.out.println("PlainTextDocumentBuilder.getOneDocument(): docLength: " + currentDocLength);
		

			// following sections:
			// one line read, one paragraph, one section
			while ((line=freader.readLine())!=null) {

				sentences = sentenceSpliter.sentenceSplit(line);
				String paraText = "";
				for(int j=0; j<sentences.length; j++)
				{
					paraText += sentences[j]+ " ";
				}
				section = new BioNERSection();
				paragraph = new BioNERParagraph(currentDocLength);
				paragraph.setText(paraText);
				paragraph.setSentence(sentences);
				section.addParagraph(paragraph);
				doc.addSection(section);

				currentDocLength += line.length();
				System.out.println("PlainTextDocumentBuilder.getOneDocument(): docLength: " + currentDocLength);
			}
			
			freader.close();
			doc.linkComponent();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

// am i high? DEBUG
		{	
	    BioNERSentence[] foo = doc.getAllSentence();
        for (BioNERSentence bns : foo) {
            System.out.println("PlainTextDocumentBuilder.getOneDcoumnet() DEBUG:   begin:" + bns.getBegin() + ", docBegin" + bns.getDocBegin());
        }
		}	


		return doc;
	}


	public static BioNERDocument getOneDocumentFromStringArray(String[] lines, String docId)
	{
		BioNERDocument doc = new BioNERDocument();
		doc.setID(docId);
		int currentDocLength=0;

		// title 
		doc.setTitle(lines[0]);
		currentDocLength += lines[0].length();


		// abstract
		{
			String[] sentences = sentenceSpliter.sentenceSplit(lines[1]);
			String absText = "";
			for(int j=0; j<sentences.length; j++) {
				absText += sentences[j]+ " ";
			}
			BioNERSection section = new BioNERSection();
			BioNERParagraph paragraph = new BioNERParagraph(currentDocLength);
			paragraph.setText(absText);
			paragraph.setSentence(sentences);
			section.addParagraph(paragraph);
			doc.setAbstractSection(section);
			currentDocLength += lines[1].length();
		}	

		// following sections:
		// one line read, one paragraph, one section
		{
			for (int i=2; i<lines.length; i++) {
				String[] sentences = sentenceSpliter.sentenceSplit(lines[i]);
				String paraText = "";
				for(int j=0; j<sentences.length; j++) {
					paraText += sentences[j]+ " ";
				}
				BioNERSection section = new BioNERSection();
				BioNERParagraph paragraph = new BioNERParagraph(currentDocLength);
				paragraph.setText(paraText);
				paragraph.setSentence(sentences);
				section.addParagraph(paragraph);
				doc.addSection(section);

				currentDocLength += lines[i].length();
			}
		}
		
		doc.linkComponent();
		{	
	    BioNERSentence[] foo = doc.getAllSentence();
        for (BioNERSentence bns : foo) {
            System.out.println("PlainTextDocumentBuilder.getOneDcoumentFromStringArray() DEBUG:   begin:" + bns.getBegin() + ", docBegin" + bns.getDocBegin());
            System.out.println("PlainTextDocumentBuilder.getOneDcoumentFromStringArray() DEBUG: " + bns.getSentenceText());
        }
		}	
		return doc;
	}

	/**
	 *  TODO: BUG this assumes only 2 lines per document
	 * @deprecated
	 */
	@Deprecated
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
			int currentDocLength=0;
			try {
				BufferedReader freader = new BufferedReader(new FileReader(file));
				String line = freader.readLine();
				docs[i].setTitle(line);
				currentDocLength += line.length();

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
				BioNERParagraph paragraph = new BioNERParagraph(currentDocLength);
				paragraph.setText(absText);
				paragraph.setSentence(sentences);
				section.addParagraph(paragraph);
				currentDocLength += line.length();

				docs[i].setAbstractSection(section);
				docs[i].linkComponent();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		{	
	    BioNERSentence[] foo = docs[i].getAllSentence();
        for (BioNERSentence bns : foo) {
            System.out.println("PlainTextDocumentBuilder.buildDocuments() DEBUG:   begin:" + bns.getBegin() + ", docBegin" + bns.getDocBegin());
        }
		}	
		}
		return docs;
	}

}
