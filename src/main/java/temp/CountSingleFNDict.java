package temp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.process.proteinner.ProteinDictionaryBuilder;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictionary.BioNERTerm;
import bioner.tools.strnormal.StringNormalizer;
import bioner.tools.strnormal.StringNormalizerFactory;

public class CountSingleFNDict {

	/**
	 * @param args
	 */
	private static BioNERDictionary proteinDict =  ProteinDictionaryBuilder.getProteinDictionary();
	private static StringNormalizer normalizer = StringNormalizerFactory.getStringNormalizer();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vector<String> resultVector = getResults("J:/workspace/CRFTools/singlefn.txt");
		
		int hasTokenNum = 0;
		int isRecordNum = 0;
		for(String result : resultVector)
		{
			result = normalizer.normalizeString(result);
			String[] tokens = result.split("\\W+");
			boolean hasDictToken = false;
			for(String token : tokens)
			{
				if(token.matches("[0-9]+|\\w")) continue;
				BioNERTerm[] terms = proteinDict.getTermsByIndex(token);
				
				if(terms != null)
				{
					hasDictToken = true;
					break;
				}
			}
			if(hasDictToken)
			{
				hasTokenNum++;
			}
		}
		System.out.println("Has Token Num:"+hasTokenNum+" Total Num:"+resultVector.size());
		
	}
	
	public static Vector<String> getResults(String filename)
	{
		Vector<String> resultVector = new Vector<String>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\|");
				if(parts.length==4)
				{
					resultVector.add(parts[3]);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultVector;
	}
}
