package bioner.normalization.rerank;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;

public class BuildGeneIDVectorMap {
	public static void buildGeneIDVectorMap(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> map, Vector<BioNERCandidate> vector, int rank)
	{
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				Vector<String> speciesVector = new Vector<String>();
				BioNERCandidate[] candidates = entity.getCandidates();
				for(int i=0; i<candidates.length&&i<rank; i++)
				{
					String speciesID = candidates[i].getRecord().getSpeciesID();
					if(!speciesVector.contains(speciesID))
					{
						speciesVector.add(speciesID);
						BioNERCandidate candidate = candidates[i];
						String geneID = candidate.getRecord().getID();
						Vector<BioNEREntity> entityVector = map.get(geneID);
						if(entityVector==null)
						{
							entityVector = new Vector<BioNEREntity>();
							map.put(geneID, entityVector);
							if(vector!=null) vector.add(candidate.clone());
						}
						entityVector.add(entity);	
					}
				}
			}
		}
	}
}
