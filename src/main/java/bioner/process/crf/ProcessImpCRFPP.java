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
		System.out.println("ProcessImpCRFPP: loading jni lib libcrfpp....");

        // need libCRFPP_JNI.so as well as libcrfpp.so.0.0.0 on the path
        // the first is the SWIG generated java-cpp bridge, the other is
        // the real code
        // If this runs slow, check the CFLAGS and CXXFLAGS in the Makefile
        // for the library and make sure it's built with -o3, not -ggdb
        // (optimized, not debugged)
	    System.loadLibrary("CRFPP_JNI");
		System.out.println("...loaded, calling....");
	  } 
	  catch (UnsatisfiedLinkError e) {
	    System.err.println("Error in ProcessImpCRFPP (static): Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
	    System.err.println("   On Mac OS X,  DYLD_LIBRARY_PATH contains \'.\'");
	    System.err.println("   On Mac OS X,  the library should be called libCRFPP_JNI.dylib");
	    System.err.println("   On Linux,  LD_LIBRARY_PATH contains \'.\'");
	    System.err.println("   On Linux,  the library should be called libCRFPP_JNI.so");
	    System.err.println("" + e);
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
	  try {
		tagger = new Tagger("-m "+modelPath/*+" -v 3 -n2"*/);
	  } 
	  catch (UnsatisfiedLinkError e) {
	    System.err.println("Error in ProcessImpCRFPP (ctor): Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
	    System.err.println("   On Mac OS X,  DYLD_LIBRARY_PATH contains \'.\'\n" + e);
	    System.err.println("   On Mac OS X,  the library should be called libcrfpp.jnilib");
		System.err.println("   called here with " + modelPath +  " label:" + label);
	    System.err.println("" + e);
		throw new RuntimeException(e);
	  }
		m_label = label;
	}
	
	private FeatureBuilder m_featureBuilder = new FeatureBuilder();
	@Override
	public void Process(BioNERDocument document) {
		for (BioNERSentence sentence : GetNERSentence.getNERSentence(document)) {
			processSentence(sentence);
		}
		
	}

	private void processSentence(BioNERSentence sentence) {
		tagger.clear();
		Vector<String> featureVector = m_featureBuilder.buildFeature(sentence);
		
		for (int i=0; i<featureVector.size(); i++) {
			tagger.add(featureVector.elementAt(i));
		}

		tagger.parse();
		String[] labels = new String[featureVector.size()];
		for (int i=0; i<labels.length; i++) {
			labels[i] = tagger.y2(i);
		}
		
		PostprocessLabels.postProcessLabels(labels, sentence.getTokens());
		
		int i=0;
		while (i<labels.length) {
			String label = labels[i];
			if (label.startsWith("B")) {
				String typeLabel = label.substring(2);
				String innerLabel = "I-"+typeLabel;
				int j=i+1;
				while (j<labels.length && labels[j].equals(innerLabel)) {
					j++;
				}
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				entity.setTokenIndex(i, j-1);
				entity.addLabel(m_label);
				sentence.addEntity(entity);
				i = j;
			}
			else {
				i++;
			}
		}
	}
}
