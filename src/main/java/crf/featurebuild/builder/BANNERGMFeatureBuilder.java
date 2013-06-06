package crf.featurebuild.builder;

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
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import crf.featurebuild.TokenFeatureBuilder;

public class BANNERGMFeatureBuilder implements TokenFeatureBuilder {
	private BannerProperties properties = BannerProperties.load("./data/banner.properties");
	private Tokenizer tokenizer = properties.getTokenizer();
	private CRFTagger tagger;
	private PostProcessor postProcessor = properties.getPostProcessor();
	private BioNERSentence m_currentSentence = null;
	private Vector<BioNEREntity> m_currentEntityVector = null;
	public BANNERGMFeatureBuilder()
	{
		try {
			tagger = CRFTagger.load(new File(GlobalConfig.ROOT_DIR+"train/gene_model_v02.bin"), properties.getLemmatiser(), properties.getPosTagger());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Vector<BioNEREntity> getEntityVector(BioNERSentence sentence)
	{
		Vector<BioNEREntity> vector = new Vector<BioNEREntity>();
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
	    	vector.add(entity);
	    }
	    return vector;
	}
	
	@Override
	public String buildFeature(BioNERSentence sentence, int index) {
		// TODO Auto-generated method stub
		if(m_currentSentence!=sentence)
		{
			m_currentEntityVector = getEntityVector(sentence);
			m_currentSentence = sentence;
		}
		for(BioNEREntity entity : m_currentEntityVector)
		{
			if(index<=entity.getTokenEndIndex() && index>=entity.getTokenBeginIndex())
			{
				return "1";
			}
		}
		return "0";
	}

}
