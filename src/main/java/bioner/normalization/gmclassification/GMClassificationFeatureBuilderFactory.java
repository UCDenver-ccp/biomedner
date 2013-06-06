package bioner.normalization.gmclassification;



import bioner.data.document.BioNEREntity;


public interface GMClassificationFeatureBuilderFactory {
	public abstract String[] getWekaAttributeFileHead();
	
	
	
	public abstract String[] getFeatures(BioNEREntity entity);
}
