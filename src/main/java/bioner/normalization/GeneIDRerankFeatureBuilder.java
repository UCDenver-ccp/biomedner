package bioner.normalization;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
/**
 * The interface for getting the features and weka file header for the rerank of gene IDs.
 * The rerank is for the Gene IDs for one article.
 * @author liu Jingchen
 *
 */
public interface GeneIDRerankFeatureBuilder {
	/**
	 * get the weka file header like "@attribute feature_1 real"
	 * @return String[] every element is a string line.
	 */
	public abstract String[] getWekaAttributeFileHead();
	
	/**
	 * Get the feature vector for one gene ID.
	 * @param document the current document.
	 * @param map the map from gene id to its gene mentions.
	 * @param candidate the BioNERCandidate containing the gene ID.
	 * @return the feature vector.
	 */
	public abstract String[] getFeatures(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate);
}
