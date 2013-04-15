///////////////////////////////////////////////////////////////////
//Usage: This class is a builder for the dictionary of protein term. Only one object for dictionary is provided.
//Author: Liu Jingchen
//Date: 2009/12/8
///////////////////////////////////////////////////////////////////
package bioner.application.geneterm;



import bioner.global.GlobalConfig;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictionary.DictionaryBuilder;
import bioner.tools.dictionary.DictionaryFileParser;

public class HumanGeneDictionaryBuilder {
	private static BioNERDictionary dict = null;
	private static DictionaryFileParser dictFileParser = new HumanGeneDictionaryFileParser();
	public static BioNERDictionary getProteinDictionary()
	{
		if(dict==null) 
		{
			dict = DictionaryBuilder.createDictionary("../../GeneTerm/gene_dic.txt", "GENE", dictFileParser);
		}
		return dict;
	}
}
