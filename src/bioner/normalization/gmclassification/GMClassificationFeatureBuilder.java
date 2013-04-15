package bioner.normalization.gmclassification;

import bioner.data.document.BioNEREntity;

/**
 * Extract one feature for classification of gene mentions.
 * The gene mentions will be classified into two classes: correct or not, according to these features.
 * @author Liu Jingchen
 *
 */
public interface GMClassificationFeatureBuilder {
	public abstract String extractFeature(BioNEREntity entity);
	public abstract String getInfo();
	public abstract String getDataType();
}
