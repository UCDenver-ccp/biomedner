package bioner.process.knowledgebase;


import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictionary.DictionaryBuilder;
import bioner.tools.dictionary.DictionaryFileParser;

public class KnowledgeBaseDictBuilder {
	private static BioNERDictionary drugDict = null;
	private static DictionaryFileParser drugDictFileParser = new DrugDictFileParser();
	public static BioNERDictionary getDrugDictionary()
	{
		if(drugDict==null) 
		{
			drugDict = DictionaryBuilder.createDictionary("./data/dict/drugbank.txt", "DRUG", drugDictFileParser);
		}
		return drugDict;
	}
	private static BioNERDictionary diseaseDict = null;
	private static DictionaryFileParser diseaseDictFileParser = new DiseaseDictFileParser();
	public static BioNERDictionary getDiseaseDictionary()
	{
		if(diseaseDict==null) 
		{
			diseaseDict = DictionaryBuilder.createDictionary("./data/dict/disease.txt", "DISEASE", diseaseDictFileParser);
		}
		return diseaseDict;
	}
}
