package crf.featurebuild.bc2gm;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		FeatureBuildDocumentBuilder docBuilder = new BC2GMFeatureBuildDocumentBuilder();
		
		//CorpFeatureBuilder builder = new CorpFeatureBuilder();
		//builder.buildCorpFeature(docBuilder,  GlobalConfig.BC2_GM_TRAIN_OUTPUT_PATH);
		FeatureBuilder featureBuilder = new FeatureBuilder();
		LabelBuilder labelBuilder = new LabelBuilder();
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../BC2GM/TrainData.gm.crfpp"));
		
			int num=0;
			BioNERDocument[] documents = docBuilder.buildDocuments();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
