package bioner.normalization.feature.builder;

import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;

public class SymbolNumberSimilarityFeatureBuilder implements
		NormalizationPairFeatureBuilder {

	@Override
	public String extractFeature(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String entityStr = candidate.getEntity().getText();
		String recordStr = candidate.getRecord().getSymbol();
		double sim = getNumberSimilarityOfTwoString(entityStr, recordStr);
		return Double.toString(sim);
	}
	public static double getNumberSimilarityOfTwoString(String str1, String str2)
	{
		Vector<String> numberVector1 = getNumberStrVector(str1);
		Vector<String> numberVector2 = getNumberStrVector(str2);
		if(numberVector1.isEmpty() && numberVector2.isEmpty())
			return 1;
		
		if(numberVector1.isEmpty() && numberVector2.size()==1)
		{
			String numStr = numberVector2.elementAt(0);
			if(numStr.equals("1")) return 1;
		}
		
		if(numberVector1.isEmpty() || numberVector2.isEmpty())
			return 0;
		int covered = 0;
		for(String numStr : numberVector1)
		{
			if(numberVector2.contains(numStr))
				covered++;
		}
		double coverRate1 = (double)covered / (double)numberVector1.size();
		
		covered = 0;
		for(String numStr : numberVector2)
		{
			if(numberVector1.contains(numStr))
				covered++;
		}
		double coverRate2 = (double)covered / (double)numberVector2.size();
		
		double avg = 2 * coverRate1 * coverRate2 / (coverRate1 + coverRate2);
		
		if(Double.isNaN(avg)) return 0;
		return avg;
	}
	
	public static Vector<String> getNumberStrVector(String str)
	{
		Vector<String> numberStrVector = new Vector<String>();
		
		Vector<String> tokenVector = GeneMentionTokenizer.getTokens(str);
		
		for(String tokenStr : tokenVector)
		{
			if(tokenStr.matches("[0-9]+|[a-zA-Z]|[ivxIVX]+") | GeneMentionTokenizer.isGreek(tokenStr))
			{
				if(tokenStr.matches("[ivxIVX]+"))
				{
					tokenStr = convertRomanNumber(tokenStr);
				}
				if(tokenStr != null)
				{
					tokenStr = tokenStr.toLowerCase().trim();
					if(!numberStrVector.contains(tokenStr))
						numberStrVector.add(tokenStr);
				}
			}
		}
		
		return numberStrVector;
	}
	private static String convertRomanNumber(String numberStr)
	{
		numberStr = numberStr.toLowerCase().trim();
		if(numberStr.equals("i")) return "1";
		if(numberStr.equals("ii")) return "2";
		if(numberStr.equals("iii")) return "3";
		if(numberStr.equals("iv")) return "4";
		if(numberStr.equals("v")) return "5";
		if(numberStr.equals("vi")) return "6";
		if(numberStr.equals("vii")) return "7";
		if(numberStr.equals("viii")) return "8";
		if(numberStr.equals("ix")) return "9";
		if(numberStr.equals("x")) return "10";
		return null;
	}
	
	public static void main(String[] args)
	{
		SymbolNumberSimilarityFeatureBuilder builder = new SymbolNumberSimilarityFeatureBuilder();
		Vector<String> numVector = builder.getNumberStrVector("copine I");
		for(String numStr : numVector)
		{
			System.out.println(numStr);
		}
		String str1 = "copine I";
		String str2 = "CPNE1";
		double sim = builder.getNumberSimilarityOfTwoString(str1, str2);
		System.out.println(sim);
	}
}
