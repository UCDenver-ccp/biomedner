package bioner.application.speciesner;

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

public class SpeciesNERDocumentBuilder implements BioNERDocumentBuilder {

	private SentenceSpliter sentenceSpliter = NLPToolsFactory.getSentenceSpliter();
	private String SPECIES_NER_DATA_PATH = "../../Species_NER/text/";
	@Override
	public BioNERDocument[] buildDocuments() {
		// TODO Auto-generated method stub
		File root = new File(SPECIES_NER_DATA_PATH);
		File[] files = root.listFiles();
		BioNERDocument[] docs = new BioNERDocument[files.length];
		for(int i=0; i<files.length; i++)
		{
			docs[i] = new BioNERDocument();
			String id = files[i].getName();
			docs[i].setID(id);
			try {
				BufferedReader freader = new BufferedReader(new FileReader(files[i]));
				String line;
				if(!id.contains("_"))
				{
					line = freader.readLine();
					docs[i].setTitle(line);
					freader.readLine();
				}
				line = freader.readLine();
				freader.close();
				BioNERSection section = new BioNERSection();
				BioNERParagraph paragraph = new BioNERParagraph();
				
				String[] sentences = sentenceSpliter.sentenceSplit(line);
				paragraph.setText(line);
				paragraph.setSentence(sentences);
				section.addParagraph(paragraph);
				docs[i].setAbstractSection(section);
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
