package bioner.application.bc2gm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


import bioner.application.api.BioNERDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;

public class BC2GMDocumentBuillder implements BioNERDocumentBuilder {

	@Override
	public BioNERDocument[] buildDocuments() {
		// TODO Auto-generated method stub
		String filepath = GlobalConfig.BC2_GM_TEST_DATA_FILEPATH;
		BioNERDocument[] documents = null;
		try {
			Vector<BioNERDocument> docVector = new Vector<BioNERDocument>();
			BufferedReader freader = new BufferedReader(new FileReader(filepath));
			String line;
			while((line=freader.readLine()) != null)
			{
				int pos = line.indexOf(' ');
				if(pos>0)
				{
					String id = line.substring(0, pos);
					String sentenceStr = line.substring(pos+1);
					BioNERDocument document = new BioNERDocument();
					document.setID(id);
					BioNERSentence sentence = new BioNERSentence(sentenceStr, 0);
					BioNERSection section = new BioNERSection();
					BioNERParagraph paragraph = new BioNERParagraph();
					paragraph.addSentence(sentence);
					section.addParagraph(paragraph);
					document.setAbstractSection(section);
					document.linkComponent();
					docVector.add(document);
				}
			}
			freader.close();
			
			
			
			int size = docVector.size();
			documents = new BioNERDocument[size];
			for(int i=0; i<size; i++)
			{
				documents[i] = docVector.elementAt(i);
			}
					
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return documents;
	}

	

}
