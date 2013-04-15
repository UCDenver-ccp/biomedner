package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.normalization.IDGMRecognizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class HasGMIDInDocumentFeatureBuilder implements NormalizationPairFeatureBuilder {


	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<String> idGMVector = IDGMRecognizer.getIDVector(candidate.getEntity().getDocument());
		String id = candidate.getRecord().getID();
		if(idGMVector.contains(id)) return "1";
		return "0";
	}

}
