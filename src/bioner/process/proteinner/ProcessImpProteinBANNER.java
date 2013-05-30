package bioner.process.proteinner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import banner.BannerProperties;
import banner.Sentence;
import banner.processing.PostProcessor;
import banner.tagging.CRFTagger;
import banner.tagging.Mention;
import banner.tokenization.Token;
import banner.tokenization.Tokenizer;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpProteinBANNER implements BioNERProcess {

	private BannerProperties properties = BannerProperties.load(GlobalConfig.ROOT_DIR+"data/banner.properties");
	private Tokenizer tokenizer = properties.getTokenizer();
	private CRFTagger tagger;
	private PostProcessor postProcessor = properties.getPostProcessor();
	public ProcessImpProteinBANNER()
	{
		try {
			tagger = CRFTagger.load(new File(GlobalConfig.ROOT_DIR+"train/gene_model_v02.bin"), properties.getLemmatiser(), properties.getPosTagger());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			getEntity(sentence);
		}
	}
	
	private void getEntity(BioNERSentence sentence)
	{
		Sentence sentence2 = new Sentence(sentence.getSentenceText());
		tokenizer.tokenize(sentence2);
	    tagger.tag(sentence2);
	    if (postProcessor != null)
	        postProcessor.postProcess(sentence2);
	    List<Mention> mentionList = sentence2.getMentions();
	    for(Mention mention : mentionList)
	    {
	    	BioNEREntity entity = new BioNEREntity();
	    	entity.set_Sentence(sentence);
	    	List<Token> tokenList = sentence2.getTokens();
	    	
	    	entity.set_position(tokenList.get(mention.getStart()).getStart(), tokenList.get(mention.getEnd()-1).getEnd()-1);
	    	entity.addLabel(GlobalConfig.ENTITY_LABEL_BANNER);
	    	sentence.addEntity(entity);
	    }
	}
	
	public static void main(String[] args)
	{
		String text = "SLAP-2 interacts with Cbl in vivo in a phosphorylation independent manner and with ZAP-70 and T cell receptor zeta chain upon T cell receptor activation.";
		BioNERSentence sentence = new BioNERSentence(text, 0);
		
		ProcessImpProteinBANNER process = new ProcessImpProteinBANNER();
		process.getEntity(sentence);
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			System.out.println(entity.getLabelVector().elementAt(0)+":"+entity.getText());
		}
	}

}
