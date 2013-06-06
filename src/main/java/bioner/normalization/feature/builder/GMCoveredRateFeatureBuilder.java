package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.IndexReader;
import bioner.normalization.data.index.IndexReaderFactory;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class GMCoveredRateFeatureBuilder implements NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		Vector<String> gmTokenVector = GeneMentionTokenizer.getTokens(candidate.getEntity().getText());
		double max = 0.0;
		String gmText = candidate.getEntity().getText().toLowerCase().replaceAll("\\W+", "");
		for(String synonym : candidate.getRecord().getSynonyms())
		{
			if(gmText.equals(synonym.toLowerCase().replaceAll("\\W+", ""))) return "1.0";
			Vector<String> synonymTokenVector = GeneMentionTokenizer.getTokens(synonym);
			double value = getCoveredRate(gmTokenVector, synonymTokenVector);
			if(max < value) max = value;
		}
		return Double.toString(max);
	}

	private static IndexReader indexReader = IndexReaderFactory.createGeneIndexReader();

	public static double getCoveredRate(Vector<String> vector_1, Vector<String> vector_2) {

		if (vector_1==null || vector_2==null) {
             return 0.0;
        }
		if (vector_1.isEmpty() || vector_2.isEmpty()) {
            return 0.0;
        }

		double score = 0.0;
		double normal = 0.0;
		for (String str : vector_1) {
			double idf = indexReader.getIDF(str);
			normal += idf;
			if (isContained(str, vector_2)) {
                score += idf;
            }

		}
		double value = score/normal;
		return value;
	}

	private static boolean isContained(String str, Vector<String> vector) {

        // WTF 
		if (str.equals("1")) {//When the str is 1, if the other name has no digital, return true.
			boolean hasNum = false;
			for(String strV : vector) {
				if(strV.matches("[0-9]+")) {
					hasNum = true;
					break;
				}
			}
			if(!hasNum) {
				return true;
            }
		}

		for(String strV : vector) {
			if(strV.toLowerCase().equals(str.toLowerCase())) return true;

            /***
			if(strV.matches("[a-z]+") && str.matches("[a-z]+")) {
				if(strV.equals(str)) return true;
			}
			else if(!strV.matches("[a-z]+") && !str.matches("[a-z]+")) {
				if(strV.toLowerCase().equals(str.toLowerCase())) return true;
			}
			else if(strV.matches("[A-Z][a-z]{4,}") && str.matches("[a-z]+")) {
				if(strV.toLowerCase().equals(str.toLowerCase())) return true;
			}
			else if(strV.matches("[a-z]+") && str.matches("[A-Z][a-z]{4,}")) {
				if(strV.toLowerCase().equals(str.toLowerCase())) return true;
			}
			else if(strV.matches("[bcdfghjklmnpqrstvwxyz]+") || str.matches("[bcdfghjklmnpqrstvwxyz]+")) {
				if(strV.toLowerCase().equals(str.toLowerCase())) return true;
			}
            **/

			else if(str.toLowerCase().equals("alpha")) {
				if(strV.equals("A")) return true;
			}
			else if(strV.toLowerCase().equals("alpha")) {
				if(str.equals("A")) return true;
			}
			else if(str.toLowerCase().equals("beta")) {
				if(strV.equals("B")) return true;
			}
			else if(strV.toLowerCase().equals("beta")) {
				if(str.equals("B")) return true;
			}
		}
		
		return false;
	}
	

}
