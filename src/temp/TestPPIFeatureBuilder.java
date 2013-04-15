package temp;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.process.preprocess.ProcessImpPreprocess;
import crf.featurebuild.TokenFeatureBuilder;
import crf.featurebuild.builder.OrthographicFeatureBuilder;
import crf.featurebuild.builder.PPIPatternFeatureBuilder;

public class TestPPIFeatureBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PPIPatternFeatureBuilder builder = new PPIPatternFeatureBuilder();
		BioNERSentence sentence = new BioNERSentence("aRa and bbb.",0);
		BioNERDocument document = new BioNERDocument();
		document.setID("000");
		BioNERSection section = new BioNERSection();
		BioNERParagraph paragraph = new BioNERParagraph();
		paragraph.addSentence(sentence);
		section.addParagraph(paragraph);
		document.setAbstractSection(section);
		
		int size = sentence.getTokens().length;
		for(int i=0; i<size; i++)
		{
			String label = builder.buildFeature(sentence, i);
			System.out.print(label+" ");
		}
		System.out.println();
		
		TokenFeatureBuilder fbuilder = new OrthographicFeatureBuilder("[^A-Z]*[A-Z][^A-Z]*");
		String label = fbuilder.buildFeature(sentence, 0);
		System.out.println(label);
	}

}
