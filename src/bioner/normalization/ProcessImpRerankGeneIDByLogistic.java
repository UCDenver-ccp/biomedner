package bioner.normalization;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import statistics.common.LogisticFile;

import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import bioner.application.bc3gn.rank.GeneIDRerankFeatureBuilderFactory;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.process.BioNERProcess;

public class ProcessImpRerankGeneIDByLogistic implements BioNERProcess {

	private Logistic logistic;
	private String m_header;
	public ProcessImpRerankGeneIDByLogistic(String trainfile) {
		try {
	   		logistic = new Logistic();
	        ArffLoader atf1 = new ArffLoader(); 
        	File inputFile = new File( trainfile );
 			atf1.setFile(inputFile);
			Instances instancesTrain = atf1.getDataSet();
			
	        instancesTrain.setClassIndex(0);
	        logistic.buildClassifier(instancesTrain);
		} catch (IOException e) {
            System.err.println("ProcessImpRerankGeneIDByLogistic:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (Exception e) {
            System.err.println("ProcessImpRerankGeneIDByLogistic:" + e);
		    e.printStackTrace();
            throw new RuntimeException(e);
		}
		LogisticFile train = new LogisticFile( trainfile );
		m_header = train.getHeader();
	}

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				processEntity(entity);
			}
		}
	}
	
	private void processEntity(BioNEREntity entity)
	{
		BioNERCandidate[] candidates = entity.getCandidates();
		if(candidates==null) return;
		for(int i=0; i<candidates.length && i<=0; i++)
		{
			String[] featureStrs = GeneIDRerankFeatureBuilderFactory.getFeatures(candidates[i]);
			
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
				candidates[i].setScore(score);
			} catch (IOException e) {
                System.err.println("ProcessImpRerankGeneIDByLogistic:" + e);
				e.printStackTrace();
                throw new RuntimeException(e);
			} catch (Exception e) {
                System.err.println("ProcessImpRerankGeneIDByLogistic:" + e);
				e.printStackTrace();
                throw new RuntimeException(e);
			}
	        
		}
		//RankCandidate.RankCandidate(candidates);
		entity.setCandidates(candidates);
	}


}
