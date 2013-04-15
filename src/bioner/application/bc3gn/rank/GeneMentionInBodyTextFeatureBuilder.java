package bioner.application.bc3gn.rank;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSection;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class GeneMentionInBodyTextFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		BioNERDocument document = candidate.getEntity().getDocument();
		BioNERSection section = candidate.getEntity().getSection();
		if(section != null && section != document.getAbstractSection())
		{
			if(section.getType().equals("text")) return "1";
		}
		return "0";
	}

}
