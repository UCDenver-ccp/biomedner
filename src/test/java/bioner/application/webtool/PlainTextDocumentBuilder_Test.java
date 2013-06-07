package bioner.application.webtool;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.File;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;


public class PlainTextDocumentBuilder_Test {

  File file = new File("/Users/roederc/work/sources/biocreativeiii/BC3GNTraining/xmls/2660273.nxml");

	public void sample_test() {
		assertEquals(new Integer(5), new Integer(5));
	}

	public void test() {
 		BC3GNDataFileReader reader =  new  BC3GNDataFileReader();
        BioNERDocument doc = reader.getOneDocument(file);
        StringBuilder sb = new StringBuilder();
        for (BioNERSentence sentence : doc.getFullTextSentences()) {
            sb.append(sentence.getSentenceText());
            sb.append("\n");
        }
    }
/**

o String[] lines = jcas.getDocumentText().split("\n");
        String docId = UIMA_Util.getDocumentID(jcas);
        BioNERDocument doc = PlainTextDocumentBuilder.getOneDocumentFromStringArray(lines, docId);

        // dEBUG check out this doc.
        BioNERSentence[] foo = doc.getAllSentence();
        for (BioNERSentence bns : foo) {
            System.out.println("AE DEBUG:   begin:" + bns.getBegin() + ", docBegin" + bns.getDocBegin());
        }

	}
**/
}
