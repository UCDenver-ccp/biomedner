///////////////////////////////////////////////////////////////////
//Usage: This class is a builder for the dictionary of protein term. Only one object for dictionary is provided.
//Author: Liu Jingchen
//Date: 2009/12/8
///////////////////////////////////////////////////////////////////
package bioner.process.organismner;




import bioner.global.GlobalConfig;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictionary.DictionaryBuilder;
import bioner.tools.dictionary.DictionaryFileParser;


public class OrgnismDictionaryBuilder {
	private static BioNERDictionary orgnismDict = null;
	private static DictionaryFileParser dictFileParser = new OrganismDictionaryFileParser();
	public static BioNERDictionary getDictionary()
	{
		if(orgnismDict==null) 
		{
			orgnismDict = DictionaryBuilder.createDictionary(GlobalConfig.ORGNISM_DICT_PATH, GlobalConfig.ORGANISM_TYPE_LABEL, dictFileParser);
		}
		return orgnismDict;
	}
	
	
}
