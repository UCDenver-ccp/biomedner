package crf.featurebuild;


import java.util.Vector;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

public class FeatureBuilder {
	private TokenFeatureBuilder[] featureBuilderPipeline = TokenFeatureBuilderFactory.createTokenFeatureBuilderPipeline();
	public Vector<String> buildFeature(BioNERSentence sentence)
	{
		Vector<String> featureVector = new Vector<String>();
		
		
		BioNERToken[] tokenArray = sentence.getTokens();
		
		for(int j=0; j<tokenArray.length; j++)
		{
			String[] features = new String[featureBuilderPipeline.length];
			for(int k=0; k<featureBuilderPipeline.length; k++)
			{
				features[k] = featureBuilderPipeline[k].buildFeature(sentence, j);
			}
			
			StringBuffer strBuffer = new StringBuffer();
			for(int k=0; k<features.length; k++)
			{
				strBuffer.append(features[k]+" ");
			}
			featureVector.add(strBuffer.toString());
		}
		return featureVector;
	}
}
