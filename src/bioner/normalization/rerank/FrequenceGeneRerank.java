package bioner.normalization.rerank;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNEREntity;
import bioner.normalization.RankCandidate;
import bioner.normalization.data.BioNERCandidate;

public class FrequenceGeneRerank {
	public static void rerank(BioNERCandidate[] candidates, HashMap<String, Vector<BioNEREntity>> map)
	{
		for(int i=0; i<candidates.length; i++)
		{
			String speciesID = candidates[i].getRecord().getID();
			Vector<BioNEREntity> entityVector = map.get(speciesID);
			if(entityVector==null)
			{
				candidates[i].setScore(0.0);
			}
			else
			{
				candidates[i].setScore(entityVector.size());
			}
		}
		RankCandidate.RankCandidate(candidates);
	}
}
