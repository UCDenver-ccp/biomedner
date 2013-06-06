package bioner.normalization.rerank;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.bc3gn.rank.GeneIDRerankFeatureBuilderFactory;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.GeneIDRerankFeatureBuilder;
import bioner.normalization.RankCandidate;
import bioner.normalization.data.BioNERCandidate;

import statistics.common.LogisticFile;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class GeneRerankBySVM {
	private LibSVM libSVM;
	private String m_header;
	private GeneIDRerankFeatureBuilder m_featureBuilder;
	public GeneRerankBySVM(String trainfile, GeneIDRerankFeatureBuilder featureBuilder)
	{
		m_featureBuilder = featureBuilder;
		try {
			File modelFile = new File(trainfile+".svm_model");
			//if(modelFile.exists())
			//{
			//	libSVM=(LibSVM)weka.core.SerializationHelper.read(trainfile+".svm_model");
			//}
			//else
			//{
				libSVM = new LibSVM();
				String[] options=weka.core.Utils.splitOptions("-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.0010 -P 0.1 -B 0");
				libSVM.setOptions(options);
		        ArffLoader atf1 = new ArffLoader(); 
	        	File inputFile = new File( trainfile );
	 			atf1.setFile(inputFile);
				Instances instancesTrain = atf1.getDataSet();
				
		        instancesTrain.setClassIndex(0);
		        libSVM.buildClassifier(instancesTrain);
		        System.out.println(libSVM.toString());
		    //    weka.core.SerializationHelper.write(trainfile+".svm_model", libSVM);
			//}
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
			if(ifHasIDGM(map.get(candidates[i].getRecordID())))
			{
				candidates[i].setScore(0.99);
				continue;
			}
			String[] featureStrs = m_featureBuilder.getFeatures(document, map, candidates[i]);
			
			StringBuffer sb = new StringBuffer("1");
			
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
				double[] result = libSVM.distributionForInstance(instancesTest.instance(0));
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
	private static boolean ifHasIDGM(Vector<BioNEREntity> gmVector)
	{
		if(gmVector==null) return false;
		for(BioNEREntity entity : gmVector)
		{
			if(entity.containLabel(GlobalConfig.ENTITY_LABEL_IDGM)) return true;
		}
		return false;
	}
}
