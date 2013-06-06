package bioner.normalization.gmclassification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import statistics.common.LogisticFile;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpGMClassificationBySVM implements BioNERProcess {
	private LibSVM libSVM;
	private String m_header;
	private GMClassificationFeatureBuilderFactory m_builderFactory;
	public ProcessImpGMClassificationBySVM(String trainfile, GMClassificationFeatureBuilderFactory builderFactory)
	{
		m_builderFactory = builderFactory;
		try {
			File modelFile = new File(trainfile+".svm_model");
			if(modelFile.exists())
			{
				libSVM=(LibSVM)weka.core.SerializationHelper.read(trainfile+".svm_model");
			}
			else
			{
				libSVM = new LibSVM();
				String[] options=weka.core.Utils.splitOptions("-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.0010 -P 0.1 -B 0");
				//String[] options=weka.core.Utils.splitOptions("-K 0");
				libSVM.setOptions(options);
				
		        ArffLoader atf1 = new ArffLoader(); 
	        	File inputFile = new File( trainfile );
	 			atf1.setFile(inputFile);
				Instances instancesTrain = atf1.getDataSet();
				
		        instancesTrain.setClassIndex(0);
		        libSVM.buildClassifier(instancesTrain);
		        //weka.core.SerializationHelper.write(trainfile+".svm_model", libSVM);
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
				if(entity.containLabel(GlobalConfig.ENTITY_LABEL_CRF))
				{
					//if(score>0.2) sentence.addEntity(entity);
					sentence.addEntity(entity);
				}
				else
				{
					//if(score>0.5) sentence.addEntity(entity);
					sentence.addEntity(entity);
				}
				//sentence.addEntity(entity);
			}
		}
	}
	public double getGMConfidenceScore(BioNEREntity entity) {
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
			double[] result = libSVM.distributionForInstance(instancesTest.instance(0));
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
