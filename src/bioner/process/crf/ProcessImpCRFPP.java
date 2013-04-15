package bioner.process.crf;

import java.util.Vector;

import org.chasen.crfpp.Tagger;

import crf.featurebuild.FeatureBuilder;


import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;
import bioner.process.proteinner.GetNERSentence;

public class ProcessImpCRFPP implements BioNERProcess {
	static {
	  try {
	    System.loadLibrary("CRFPP");
	  } catch (UnsatisfiedLinkError e) {
	    System.err.println("Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
	    System.exit(1);
	  }
	}
	private Tagger tagger;
	private String m_label;
	public ProcessImpCRFPP()
	{
		System.err.println("Load CRF++ model from "+GlobalConfig.CRF_MODEL_FILEPATH);
		tagger = new Tagger("-m "+GlobalConfig.CRF_MODEL_FILEPATH/*+" -v 3 -n2"*/);
		m_label = GlobalConfig.ENTITY_LABEL_CRF;
	}
	public ProcessImpCRFPP(String modelPath, String label)
	{
		System.err.println("Load CRF++ model from "+modelPath);
		tagger = new Tagger("-m "+modelPath/*+" -v 3 -n2"*/);
		m_label = label;
	}
	
	private FeatureBuilder m_featureBuilder = new FeatureBuilder();
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : GetNERSentence.getNERSentence(document))
		{
			processSentence(sentence);
		}
		
	}
	private void processSentence(BioNERSentence sentence)
	{
		tagger.clear();
		Vector<String> featureVector = m_featureBuilder.buildFeature(sentence);
		
		for(int i=0; i<featureVector.size(); i++)
		{
			tagger.add(featureVector.elementAt(i));
		}
		tagger.parse();
		String[] labels = new String[featureVector.size()];
		for(int i=0; i<labels.length; i++)
		{
			labels[i] = tagger.y2(i);
		}
		
		PostprocessLabels.postProcessLabels(labels, sentence.getTokens());
		
		int i=0;
		while(i<labels.length)
		{
			String label = labels[i];
			if(label.startsWith("B"))
			{
				String typeLabel = label.substring(2);
				String innerLabel = "I-"+typeLabel;
				int j=i+1;
				while(j<labels.length && labels[j].equals(innerLabel))
				{
					j++;
				}
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				entity.setTokenIndex(i, j-1);
				entity.addLabel(m_label);
				sentence.addEntity(entity);
				i = j;
			}
			else
			{
				i++;
			}
		}
	}
}
