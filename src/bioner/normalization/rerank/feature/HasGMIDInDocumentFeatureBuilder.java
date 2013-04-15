package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.IDGMRecognizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class HasGMIDInDocumentFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<String> idGMVector = IDGMRecognizer.getIDVector(candidate.getEntity().getDocument());
		String id = candidate.getRecord().getID();
		if(idGMVector.contains(id)) return "1";
		return "0";
	}

}
