package bioner.application.webtool;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSentence;

/******
	title
	
	abstract-title

	abstract-sentences

	section1-title
	
	paragraph1-1-sentences

	paragraph1-2-sentences

	section2-title

	paragraph2-1-sentences

	paragraph2-2-sentences

******/

/*
	Lessons:

	1. doc.getAllSentences() is *all* sentences, including those 
		from titles and section headings,
		not just doc-->section-->paragraph,
		but it skips the abstract?	

	2. toString is not the same as getSentenceText()

*/



public class BC3GNDataFileReader_Test {

  	File file = new File("/Users/roederc/work/sources/biocreativeiii/BC3GNTraining/xmls/2660273.nxml");
    BioNERDocument doc;
	List<String> list = new ArrayList<String>();




	@Before
	public void setUp() {
 		BC3GNDataFileReader reader =  new  BC3GNDataFileReader();
    	doc = reader.getOneDocument(file);

		int i=0;
        for (BioNERSentence sentence : doc.getFullTextSentences()) {
            System.out.println(sentence.getSentenceText() + "\n");
			if (i++ < 10) {
				list.add(sentence.getSentenceText());
			}
			else {
				break;
			}
        }
	}

	/* body sentences */
		String title = "The NOD/RIP2 Pathway Is Essential for Host Defenses Against Chlamydophila pneumoniae Lung Infection";
		String one = "Introduction";
		String two =	"Chlamydophila pneumoniae is a Gram-negative obligate intracellular pathogen that is widely prevalent ( 1 ) , causes respiratory tract diseases such as pneumonia , sinusitis , and bronchitis , contributes to acceleration of atherosclerosis ( 2 ) , ( 3 ) , and is associated with development of chronic lung diseases such as asthma ( 4 ) and other disorders where chronic inflammation is a hallmark feature ( 5 ) , ( 6 ) ." ;
		String three="C. pneumoniae infects various cell types such as epithelial cells , monocytes , macrophages , smooth-muscle cells and endothelial cells , and often resides intracellularly for indefinite periods ( 7 ) .";

	@Test
	public void testFirstLine() { assertEquals(one, list.get(0) ); }

	@Test
	public void testSecondLIne() { assertEquals(two,  list.get(1));
	}

	@Test
	public void testThirdLine() { assertEquals(three, list.get(2)); }

	@Test
	public void testTitleSentence_text() {
		BioNERSentence titleSentence = doc.getTitle();
		assertEquals(title, titleSentence.getSentenceText());
	}

	@Test
	public void testSecondSentence_text() {
		Vector<BioNERSection> sections = doc.getSections();
		Vector<BioNERParagraph> paragraphs = sections.get(0).getParagraph();
		Vector<BioNERSentence> sentences = paragraphs.get(0).getSentence();

		assertEquals(two , sentences.get(0).getSentenceText());
	}


	/* abstract sentences */
		String zero  = "Author Summary";
		String first = "Here we investigated the role of the Nod\\/Rip2 pathway in host responses to Chlamydophila pneumoniae induced pneumonia in mice .";
		String second = "Rip2 \\/ mice infected with C. pneumoniae exhibited impaired iNOS expression and NO production , and delayed neutrophil recruitment to the lungs .";
		String last = "These results demonstrate that in addition to the TLR\\/MyD88 pathway , the Nod\\/Rip2 signaling pathway also plays a significant role in intracellular recognition , innate immune host responses , and ultimately has a decisive impact on clearance of C. pneumoniae from the lungs and survival of the infectious challenge .";


	@Test
	public void testAbstract() {
		BioNERSentence[] sentences = doc.getAbstractSentences();
		for (BioNERSentence bns : sentences) {
			System.out.println("---" + bns.getSentenceText() + "---");
		}

		assertEquals(zero, sentences[0].getSentenceText());
		assertEquals(first, sentences[1].getSentenceText());
		assertEquals(second, sentences[2].getSentenceText());
		assertEquals(last, sentences[8].getSentenceText());
	}

	@Test
	public void testAbstract_2() {
		BioNERSection abstractSection = doc.getAbstractSection();
		Vector<BioNERParagraph> paragraphs = abstractSection.getParagraph();
		Vector<BioNERSentence> sentences = paragraphs.get(0).getSentence();


		//assertEquals(zero, sentences.get(0).getSentenceText());
		assertEquals(zero, abstractSection.getTitleSentence().getSentenceText());

		assertEquals(first, sentences.get(0).getSentenceText());
		assertEquals(second, sentences.get(1).getSentenceText());
		assertEquals(last, sentences.get(7).getSentenceText());
	}



/*** spans ***/

	//@Test
	public void testTitleSentence_spans() {
		Vector<BioNERSection> sections = doc.getSections();
		Vector<BioNERParagraph> paragraphs = sections.get(0).getParagraph();
		Vector<BioNERSentence> sentences = paragraphs.get(0).getSentence();

		assertEquals(0,sentences.get(0).getDocBegin());
		assertEquals(10, sentences.get(0).getDocEnd());
	}

}
