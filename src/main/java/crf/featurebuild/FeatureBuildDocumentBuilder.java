package crf.featurebuild;

import bioner.data.document.BioNERDocument;

public interface FeatureBuildDocumentBuilder {
	public abstract BioNERDocument[] buildDocuments();
}
