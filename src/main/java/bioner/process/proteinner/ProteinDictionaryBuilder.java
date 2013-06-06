///////////////////////////////////////////////////////////////////
//Usage: This class is a builder for the dictionary of protein term. Only one object for dictionary is provided.
//Author: Liu Jingchen
//Date: 2009/12/8
///////////////////////////////////////////////////////////////////
package bioner.process.proteinner;



import bioner.global.GlobalConfig;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictionary.DictionaryBuilder;
import bioner.tools.dictionary.DictionaryFileParser;

public class ProteinDictionaryBuilder {
	private static BioNERDictionary proteinDict = null;
	private static DictionaryFileParser dictFileParser = new ProteinDictionaryFileParser();
	public static BioNERDictionary getProteinDictionary()
	{
		if(proteinDict==null) 
		{
			proteinDict = DictionaryBuilder.createDictionary(GlobalConfig.PROTEIN_DICT_PATH, GlobalConfig.PROTEIN_TYPE_LABEL, dictFileParser);
		}
		return proteinDict;
	}
}
