package bioner.application.webtool;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.File;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;


public class PlainTextDocumentBuilder_Test {

  	String fileString = "/Users/roederc/work/git/biomedner/src/main/resources/2660273.txt";
  	File file = new File("/Users/roederc/work/git/biomedner/src/main/resources/2660273.txt");
	String lines[] = {
		"The NOD/RIP2 Pathway Is Essential for Host Defenses Against Chlamydophila pneumoniae Lung Infection",
		"Author Summary",
		"Here we investigated the role of the Nod\\/Rip2 pathway in host responses to Chlamydophila pneumoniae induced pneumonia in mice .",
		"Rip2 \\/ mice infected with C. pneumoniae exhibited impaired iNOS expression and NO production , and delayed neutrophil recruitment to the lungs ."
	};

	@Test
	public void test() {
 		PlainTextDocumentBuilder reader =  new  PlainTextDocumentBuilder();
        BioNERDocument doc = reader.getOneDocument(fileString);
        StringBuilder sb = new StringBuilder();

        BioNERSentence[] sentences = doc.getAllSentence();

		if (false) { 
        for (BioNERSentence sentence : doc.getAllSentence()) {
            sb.append(sentence.getSentenceText());
            sb.append("\n");
			System.out.println(sentence.getSentenceText());
        }
		}


		assertEquals(lines[0], sentences[0].getSentenceText());
		assertEquals(lines[1], sentences[1].getSentenceText());
		assertEquals(lines[2], sentences[2].getSentenceText());
		assertEquals(lines[3], sentences[3].getSentenceText());
    }
}
