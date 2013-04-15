///////////////////////////////////////////////////
//Usage: This class is to deal with the AIMedCorp format input data.
//Author: Liu Jingchen
//Date: 2009/12/2
///////////////////////////////////////////////////
package crf.featurebuild;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bioner.data.document.BioNERDocument;
import bioner.global.GlobalConfig;

public class CorpFeatureBuilder {

	public void buildCorpFeature(FeatureBuildDocumentBuilder docBuilder,  String outputDir)
	{
		if(!outputDir.endsWith("/"))
		{
			outputDir += "/";
		}
		File outputFile = new File(outputDir);
		if(!outputFile.exists())
		{
			outputFile.mkdirs();
		}
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		int threadNum = GlobalConfig.THREAD_NUM;
		FeatureBuildDocumentThreadArranger threadArranger = new FeatureBuildDocumentThreadArranger(documents);
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputDir));
		
		
			Thread[] threads = new Thread[threadNum];
			for(int i=1; i<=threadNum; i++)
			{
				Runnable run = new FeatureBuildProcessDocumentTread(threadArranger, outputDir, i-1);
				threads[i] = new Thread(run);
				threads[i].start();
			}
			for(int i=1; i<=threadNum; i++)
			{
				
				threads[i].join();
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
