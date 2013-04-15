package bioner.normalization.feature;

import bioner.normalization.data.BioNERCandidate;

/**
 * The 'pair' here means one gene mention from context, and one record from knowledge base.
 * This interface extract one feature to describe the similarity between them.
 * @author Liu Jingchen
 *
 */
public interface NormalizationPairFeatureBuilder {
	/**
	 * Extract one feature for one pair of gene mention and knowledge base record.
	 * @param candidate BioNERCandidate A BioNERCandidate is OK. The BioNEREntity(gene mention) and BioNERRecord(record) can be got from it.
	 * @return String representing one feature.
	 */
	public abstract String extractFeature(BioNERCandidate candidate);
}
