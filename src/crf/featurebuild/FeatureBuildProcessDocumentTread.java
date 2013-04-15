package crf.featurebuild;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.process.BioNERProcess;

public class FeatureBuildProcessDocumentTread implements Runnable {

	private FeatureBuildDocumentThreadArranger m_DocArranger = null;
	private String m_outputDir = null;
	private int m_threadNum = -1;
	private FeatureBuilder m_featureBuilder = new FeatureBuilder();
	private LabelBuilder m_labelBuilder = new LabelBuilder();
	
	private TokenFeatureBuilder[] featureBuilderPipeline = TokenFeatureBuilderFactory.createTokenFeatureBuilderPipeline();
	public FeatureBuildProcessDocumentTread(FeatureBuildDocumentThreadArranger DocArranger, String outputDir, int threadNum)
	{
		m_DocArranger = DocArranger;
		m_outputDir = outputDir;
		m_threadNum = threadNum;
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int roundNum = 0;
		while(true)
		{
			int num = m_DocArranger.getNextNum();
			//int num = roundNum * GlobalConfig.THREAD_NUM + m_threadNum;
			roundNum++;
			BioNERDocument document = m_DocArranger.getDocument(num);
			if(document==null) return;
			
			String pmid = document.getID();
			File checkFile = new File(m_outputDir+num+"."+pmid+".feature");
			if(checkFile.exists()) continue;
			
			
			clearDocEntity(document);
			
			
			
			
			
			String id = document.getID();
			long beginTime = System.currentTimeMillis();
			System.out.println("Begin to process #"+num+" document "+id);
			
			
			
			
			
			BioNERSentence[] sentenceArray = document.getAbstractSentences();
			
			
			try {
				BufferedWriter fwriter = new BufferedWriter(new FileWriter(m_outputDir+num+"."+id+".feature"));
				for(int i=0; i<sentenceArray.length; i++)
				{
					Vector<String> featureVector = m_featureBuilder.buildFeature(sentenceArray[i]);
					Vector<String> labelVector = m_labelBuilder.buildLabel(sentenceArray[i]);
					for(int j=0; j<labelVector.size(); j++)
					{
						
					}
					
					if(i!=sentenceArray.length-1) fwriter.write("<SENTENCE>");
					fwriter.newLine();
				}
				fwriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Finished processing #"+num+" document "+id);
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			System.out.println("time used:"+time+"ms");

		}
	}
	private void clearDocEntity(BioNERDocument doc)
	{
		for(BioNERSentence sentence : doc.getAbstractSentences())
		{
			sentence.clearEntities();
		}
		for(BioNERSentence sentence : doc.getFullTextSentences())
		{
			sentence.clearEntities();
		}
	}

}
