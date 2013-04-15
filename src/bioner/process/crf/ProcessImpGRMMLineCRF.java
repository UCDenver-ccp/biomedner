package bioner.process.crf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import crf.featurebuild.ConvertToGRMMFormat;
import crf.featurebuild.TokenFeatureBuilder;
import crf.featurebuild.TokenFeatureBuilderFactory;
import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.SerialPipes;
import edu.umass.cs.mallet.base.pipe.TokenSequence2FeatureVectorSequence;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.pipe.iterator.PipeInputIterator;
import edu.umass.cs.mallet.base.types.Alphabet;
import edu.umass.cs.mallet.base.types.Instance;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.Label;
import edu.umass.cs.mallet.base.types.Labels;
import edu.umass.cs.mallet.base.types.LabelsSequence;
import edu.umass.cs.mallet.base.util.FileUtils;
import edu.umass.cs.mallet.grmm.learning.ACRF;
import edu.umass.cs.mallet.grmm.learning.ACRFEvaluator;
import edu.umass.cs.mallet.grmm.learning.ACRFTrainer;
import edu.umass.cs.mallet.grmm.learning.GenericAcrfData2TokenSequence;
import edu.umass.cs.mallet.grmm.learning.MultiSegmentationEvaluatorACRF;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;
import bioner.process.proteinner.GetNERSentence;
import bsh.EvalError;

public class ProcessImpGRMMLineCRF implements BioNERProcess {

	private ACRF m_CRF = null;
	private TokenFeatureBuilder[] featureBuilderPipeline = TokenFeatureBuilderFactory.createTokenFeatureBuilderPipeline();
	private Pipe m_pipe = null;
	private static InstanceList training=null;
	public ProcessImpGRMMLineCRF()
	{
		m_CRF = (ACRF)FileUtils.readGzippedObject(new File("../../grmm/acrf_bc2.ser.gz"));
		GenericAcrfData2TokenSequence basePipe = new GenericAcrfData2TokenSequence();
		basePipe.setFeaturesIncludeToken(false);
		basePipe.setIncludeTokenText(false);
		m_pipe = new SerialPipes (new Pipe[] {
			        basePipe,
			        new TokenSequence2FeatureVectorSequence (true, true),
			});
		PipeInputIterator trainSource;
		try {
			if(training==null)
			{
				System.out.println("Reading GRMM training data....");
				trainSource = new LineGroupIterator (new FileReader ("../../grmm/TrainData.bc2.grmm"), Pattern.compile ("^\\s*$"), true);
				training = new InstanceList (m_pipe);
				training.add(trainSource);
				PipeInputIterator testSource;
			    testSource = new LineGroupIterator (new FileReader ("../../grmm/TestData.bc2.grmm"), Pattern.compile ("^\\s*$"), true);
			    InstanceList testing = new InstanceList (m_pipe);
			    testing.add (testSource);
			    ACRFEvaluator eval = new MultiSegmentationEvaluatorACRF(new String[]{"B-PRO"}, new String[]{"I-PRO"}, 0);
			    eval.test (m_CRF, testing, "Testing");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//training = (InstanceList)FileUtils.readGzippedObject(new File("../../grmm/training.ser.gz"));
		//training.getTargetAlphabet();
	   
	}
	
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
		String featureString =  getInstanceForSentence(sentence);
		PipeInputIterator testSource = new LineGroupIterator (new StringReader (featureString), Pattern.compile ("^\\s*$"), true);
		
		InstanceList testing = new InstanceList (m_pipe);
		
		testing.add(testSource);
		List labelsSeq = m_CRF.getBestLabels(testing);
		LabelsSequence lblseq = (LabelsSequence)labelsSeq.get(0);
		
		BioNERToken[] tokens = sentence.getTokens();
		String[] labels = new String[tokens.length];
		for(int i=0; i<labels.length; i++)
		{
			String labelStr = lblseq.get(i).toString();
			labels[i] = labelStr;
		}
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
				entity.addLabel(GlobalConfig.ENTITY_LABEL_CRF);
				sentence.addEntity(entity);
				i = j;
			}
			else
			{
				i++;
			}
		}
		
	}
	private String getInstanceForSentence(BioNERSentence sentence)
	{
		BioNERToken[] tokenArray = sentence.getTokens();
		Vector<String> crfppFeatureVector = new Vector<String>();
		for(int j=0; j<tokenArray.length; j++)
		{
			String line = "";
			String[] features = new String[featureBuilderPipeline.length];
			
			for(int k=0; k<featureBuilderPipeline.length; k++)
			{
				features[k] = featureBuilderPipeline[k].buildFeature(sentence, j);
			}
			for(int k=0; k<features.length; k++)
			{
				line +=features[k]+" ";
			}
			line += " O";
			crfppFeatureVector.add(line);
		}
		String[] grmmFeatures = ConvertToGRMMFormat.convertToGRMMFormat(crfppFeatureVector, 2, 2);
		StringBuffer sb = new StringBuffer ();
		for(int i=0; i<grmmFeatures.length; i++)
		{
			sb.append(grmmFeatures[i]);
			sb.append("\n");
		}
 		return sb.toString();
	}
}
