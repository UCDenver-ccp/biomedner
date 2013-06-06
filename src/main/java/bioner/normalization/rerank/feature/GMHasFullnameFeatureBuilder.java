package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class GMHasFullnameFeatureBuilder implements RerankFeatureBuilder {

	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String speciesID = candidate.getRecord().getID();
		Vector<BioNEREntity> entityVector = map.get(speciesID);
		
		Vector<String> gmStrVector = new Vector<String>();
		for(BioNEREntity entity : entityVector)
		{
			String gmStr = entity.getText();
			if(!gmStrVector.contains(gmStr))
			{
				gmStrVector.add(gmStr);
			}
		}
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(String gmStr : gmStrVector)
		{
			String fullname = fullNameMap.get(gmStr);
			if(fullname!=null && gmStrVector.contains(fullname)) return "1";
		}
		
		
		return "0";
	}

}
