package bioner.normalization;

import bioner.normalization.data.BioNERCandidate;
/**
 * The interface for getting the features and weka file header for the first rank of gene ID candidates.
 * The first rank is for the candidates of one gene mention.
 * @author Liu Jingchen
 *
 */
public interface FirstRankFeatureBuilder {
	/**
	 * get the weka file header like "@attribute feature_1 real"
	 * @return String[] every element is a string line.
	 */
	public abstract String[] getWekaAttributeFileHead();
	
	/**
	 * Get the feature vector for one candidate
	 * @param candidate the candidate to be extracted features.
	 * @return String[] every element is the value of one feature. The order should be consistent to the weka file header.
	 */
	public abstract String[] getFeatures(BioNERCandidate candidate);
}
