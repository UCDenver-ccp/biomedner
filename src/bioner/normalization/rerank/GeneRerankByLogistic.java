package bioner.normalization.rerank;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.GeneIDRerankFeatureBuilder;
import bioner.normalization.RankCandidate;
import bioner.normalization.data.BioNERCandidate;

import statistics.common.LogisticFile;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.converters.ArffLoader;

public class GeneRerankByLogistic {
	private Logistic logistic;
	private String m_header;
	private GeneIDRerankFeatureBuilder m_featureBuilder;

	public GeneRerankByLogistic(String trainfile, GeneIDRerankFeatureBuilder featureBuilder) {
		m_featureBuilder = featureBuilder;
        File inputFile = null;
		try {
	   		logistic = new Logistic();
	   		//String[] options=weka.core.Utils.splitOptions("-D");
	   		//logistic.setOptions(options);
	        ArffLoader atf1 = new ArffLoader(); 
        	inputFile = new File( trainfile );
 			atf1.setFile(inputFile);
			Instances instancesTrain = atf1.getDataSet();
			
	        instancesTrain.setClassIndex(0);
	        logistic.buildClassifier(instancesTrain);
	        System.out.println(logistic.debugTipText());
	        double[][] coefficients = logistic.coefficients();
	        System.out.println("Logistic coefficients:");
	        for (int i=0; i<coefficients.length; i++) {
	        	System.out.print(i+":\t");
	        	for(int j=0; j<coefficients[i].length; j++)
	        	{
	        		System.out.print(coefficients[i][j]+"\t");
	        	}
	        	System.out.println();
	        }
		} catch (IOException e) {
	        System.err.println("GeneRerankByLogistic ERROR: " + e);
	        System.err.println("...ERROR possibly relatd to this file:: " + trainfile + " or this one: " + inputFile.getAbsolutePath());
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (Exception e) {
	        System.err.println("GeneRerankByLogistic ERROR: " + e);
	        System.err.println("...ERROR possibly relatd to this file:: " + trainfile + " or this one: " + inputFile.getAbsolutePath());
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		// Get header from a file
		LogisticFile train = new LogisticFile( trainfile );
		m_header = train.getHeader();
		System.out.println("HEADER: " + trainfile + "----->" + m_header + "<--------");
	}
	
	public void rerank(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate[] candidates) {

		for (int i=0; i<candidates.length; i++) {

			//String[] headerStrs = m_featureBuilder.getWekaAttributeFileHead();
			//StringBuffer headerBuffer = new StringBuffer();
			//for(int j=0; j<headerStrs.length; j++) {	
			//	headerBuffer.append(headerStrs[j]);
			//	headerBuffer.append(", ");
			//}
			//String headerHeader = "@relation gene_normalization\n@attribute class {1,0}\n";
			//String headerFooter = "\n@data\n";
			//m_header =  headerHeader + headerBuffer.toString().replace(",","\n") + headerFooter;
			//System.out.println("HEADER header:----->" + headerHeader + "<--------");
			//System.out.println("HEADER buffer:----->" + headerBuffer.toString() + "<--------");
			//System.out.println("HEADER:----->" + m_header + "<--------");

			String[] featureStrs = m_featureBuilder.getFeatures(document, map, candidates[i]);
			StringBuffer featureBuffer = new StringBuffer("1,");
			for(int j=0; j<featureStrs.length; j++) {
				featureBuffer.append(featureStrs[j]);
				featureBuffer.append(", ");
			}


			String dataString = featureBuffer.toString();
			dataString = m_header + dataString.substring(0, dataString.length() -2);
			System.out.println("dataString length: " + dataString.length());
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
				System.out.println("GeneRerankByLogistic.rerank() in string is: \n" + dataString + "\n  end of rerank() string");
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Exception e) {
				System.out.println("GeneRerankByLogistic.rerank() in string is: " + dataString + "\n  end of rerank() string");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
	        
		}
		RankCandidate.RankCandidate(candidates);
	}
}
