package bioner.normalization.rerank;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.GeneIDRerankFeatureBuilder;
import bioner.normalization.RankCandidate;
import bioner.normalization.data.BioNERCandidate;

import statistics.common.LogisticFile;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class GeneRerankByLogistic {
	private Logistic logistic;
	private String m_header;
	private GeneIDRerankFeatureBuilder m_featureBuilder;
	public GeneRerankByLogistic(String trainfile, GeneIDRerankFeatureBuilder featureBuilder)
	{
		m_featureBuilder = featureBuilder;
		try {
	   		logistic = new Logistic();
	   		//String[] options=weka.core.Utils.splitOptions("-D");
	   		//logistic.setOptions(options);
	        ArffLoader atf1 = new ArffLoader(); 
        	File inputFile = new File( trainfile );
 			atf1.setFile(inputFile);
			Instances instancesTrain = atf1.getDataSet();
			
	        instancesTrain.setClassIndex(0);
	        logistic.buildClassifier(instancesTrain);
	        System.out.println(logistic.debugTipText());
	        double[][] coefficients = logistic.coefficients();
	        System.out.println("Logistic coefficients:");
	        for(int i=0; i<coefficients.length; i++)
	        {
	        	System.out.print(i+":\t");
	        	for(int j=0; j<coefficients[i].length; j++)
	        	{
	        		System.out.print(coefficients[i][j]+"\t");
	        	}
	        	System.out.println();
	        }
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
	
	public void rerank(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate[] candidates)
	{
		for(int i=0; i<candidates.length; i++)
		{
			String[] featureStrs = m_featureBuilder.getFeatures(document, map, candidates[i]);
			
			StringBuffer sb = new StringBuffer("?");
			
			for(int j=0; j<featureStrs.length; j++)
			{
				sb.append(",");
				sb.append(featureStrs[j]);
			}
			String dataString = m_header + sb.toString();
			InputStream in = new ByteArrayInputStream(dataString.getBytes());
	        
			double[] features = new double[featureStrs.length];
			for(int j=0; j<features.length; j++)
			{
				features[j] = Double.parseDouble(featureStrs[j]);
			}
			candidates[i].setFeatures(features);
			
			
			ArffLoader atf_test = new ArffLoader();
			try {
				atf_test.setSource(in);
				Instances instancesTest = atf_test.getDataSet();
		        instancesTest.setClassIndex( 0 );
				double[] result = logistic.distributionForInstance(instancesTest.instance(0));
				double score = result[0];
				candidates[i].setScore(score);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}
		RankCandidate.RankCandidate(candidates);
	}
}
