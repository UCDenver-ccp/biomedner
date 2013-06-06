package bioner.application.bc3gn.rank;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSection;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class GeneMentionInAbstractFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERDocument document = candidate.getEntity().getDocument();
		BioNERSection section = candidate.getEntity().getSection();
		if(section == document.getAbstractSection()) return "1";
		
		return "0";
	}

}
