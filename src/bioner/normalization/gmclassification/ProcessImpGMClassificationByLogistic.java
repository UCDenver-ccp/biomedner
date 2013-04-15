package bioner.normalization.gmclassification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import statistics.common.LogisticFile;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.process.BioNERProcess;

public class ProcessImpGMClassificationByLogistic implements BioNERProcess {
	private Logistic logistic;
	private String m_header;
	private GMClassificationFeatureBuilderFactory m_builderFactory;
	public ProcessImpGMClassificationByLogistic(String trainfile, GMClassificationFeatureBuilderFactory builderFactory)
	{
		m_builderFactory = builderFactory;
		try {
			logistic = new Logistic();
	        ArffLoader atf1 = new ArffLoader(); 
        	File inputFile = new File( trainfile );
 			atf1.setFile(inputFile);
			Instances instancesTrain = atf1.getDataSet();
			
	        instancesTrain.setClassIndex(0);
	        logistic.buildClassifier(instancesTrain);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogisticFile train = new LogisticFile( trainfile );
		m_header = train.getHeader();
	}
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNEREntity[] allEntityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : allEntityArray)
			{
				double score = getGMConfidenceScore(entity);
				entity.setScore(score);
				if(score>0.1) sentence.addEntity(entity);
			}
		}
	}
	private double getGMConfidenceScore(BioNEREntity entity) {
		// TODO Auto-generated method stub
		
		
		String[] featureStrs = m_builderFactory.getFeatures(entity);
		
		StringBuffer sb = new StringBuffer("?");
		
		for(int j=0; j<featureStrs.length; j++)
		{
			sb.append(",");
			sb.append(featureStrs[j]);
		}
		String dataString = m_header + sb.toString();
		InputStream in = new ByteArrayInputStream(dataString.getBytes());
        
		ArffLoader atf_test = new ArffLoader();
		try {
			atf_test.setSource(in);
			Instances instancesTest = atf_test.getDataSet();
	        instancesTest.setClassIndex( 0 );
			double[] result = logistic.distributionForInstance(instancesTest.instance(0));
			double score = result[0];
			return score;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
		
		return 0.0;
	}

}
