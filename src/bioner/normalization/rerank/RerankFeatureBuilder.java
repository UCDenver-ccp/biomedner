package bioner.normalization.rerank;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;

public interface RerankFeatureBuilder {
	public abstract String extractFeature(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate);
}
