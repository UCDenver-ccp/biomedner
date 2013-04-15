package bioner.normalization.rerank.feature;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;

public class GMInSectionNumFeatureBuilder implements RerankFeatureBuilder {

	private String m_sectionTitle;
	private Pattern m_pattern;
	public GMInSectionNumFeatureBuilder(String sectionTitle)
	{
		m_sectionTitle = sectionTitle.toLowerCase().trim();
		m_pattern = Pattern.compile(".*"+m_sectionTitle+".*");
	}
	@Override
	public String extractFeature(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> gmVector = map.get(candidate.getRecord().getID());
		int num=0;
		for(BioNEREntity gmEntity : gmVector)
		{
			BioNERSection currentSection = gmEntity.getSection();
			while(currentSection!=null)
			{
				BioNERSentence titleSentence =  currentSection.getTitleSentence();
				if(titleSentence!=null)
				{
					String sectionTitle = titleSentence.getSentenceText().toLowerCase().trim();
					Matcher matcher = m_pattern.matcher(sectionTitle);
					if(matcher.matches())
					{
						num++;
						break;
					}
				}
				currentSection = currentSection.getParentSection();
			}
		}
		if(gmVector.size()==0) return "0.0";
		return Double.toString((double)num/(double)gmVector.size());
	}

}
