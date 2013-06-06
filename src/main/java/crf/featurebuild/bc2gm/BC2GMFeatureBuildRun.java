package crf.featurebuild.bc2gm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import crf.featurebuild.CorpFeatureBuilder;
import crf.featurebuild.FeatureBuildDocumentBuilder;
import crf.featurebuild.FeatureBuilder;
import crf.featurebuild.LabelBuilder;


public class BC2GMFeatureBuildRun {

	

	public static void main(String[] args) {

		// input
		String docFileName = args[0];
		File docFile = new File(docFileName); 				//GM.train.in

	
		String evalFileName = args[1];
		File evalFile = new File(evalFileName ); 	//GM.train.in.eval

		// output
		String trainDataFileName = args[2];					// "../../BC2GM/TrainData.gm.crfpp"
		File trainDataFile = new File(trainDataFileName);

	
		GlobalConfig.ReadConfigFile();
		//FeatureBuildDocumentBuilder docBuilder = new BC2GMFeatureBuildDocumentBuilder();
		BC2GMFeatureBuildDocumentBuilder docBuilder = new BC2GMFeatureBuildDocumentBuilder();
		
		FeatureBuilder featureBuilder = new FeatureBuilder();
		LabelBuilder labelBuilder = new LabelBuilder();
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(trainDataFile));
		
			int num=0;
			BioNERDocument[] documents = docBuilder.buildDocuments(docFile, evalFile);
			for(int j=0; j<documents.length; j++)
			{
				BioNERDocument document = documents[j];
				num++;
				System.out.print("Processing #"+num+"....");
				for(BioNERSentence sentence : document.getAllSentence())
				{
					Vector<String> featureVector = featureBuilder.buildFeature(sentence);
					Vector<String> labelVector = labelBuilder.buildLabel(sentence);
					for(int i=0; i<labelVector.size(); i++)
					{
						fwriter.write(featureVector.elementAt(i));
						fwriter.write(labelVector.elementAt(i));
						fwriter.newLine();
					}
					fwriter.newLine();
				}
				System.out.println("Finished!");
				documents[j]=null;
			}
			fwriter.close();
		} catch (IOException e) {
			System.out.println("error in BC2GMFeatureBuildRun:" + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
