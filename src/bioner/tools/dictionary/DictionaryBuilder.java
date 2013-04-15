///////////////////////////////////////////////////////////////////
//Usage: This class is a builder for the dictionary. It provides the function of building a index structure in the dictionary.
//		It can detect whether the index file exists. It will use the index file as possible.
//		A implement for DictionaryFileParser interface should be provided to this class when you use it. 
//Author: Liu Jingchen
//Date: 2009/12/8
///////////////////////////////////////////////////////////////////
package bioner.tools.dictionary;

import java.io.File;
import java.util.Vector;


import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

import bioner.tools.dictner.SentenceNERProcessFactory;
import bioner.tools.dictner.TokenNormalize;
import bioner.tools.dictner.Tokenizer;
import bioner.tools.strnormal.StringNormalizer;
import bioner.tools.strnormal.StringNormalizerFactory;

public class DictionaryBuilder {
	private static Tokenizer tokenizer = SentenceNERProcessFactory.createTokenizer();
	private static TokenNormalize tokenNormal = SentenceNERProcessFactory.createrTokenNormalize();
	private static StringNormalizer strNormalizer = StringNormalizerFactory.getStringNormalizer();
	public static BioNERDictionary createDictionary(String filename, String type, DictionaryFileParser dictFileParser)
	{
		BioNERDictionary dict;
		
		
		dict = createDictionary(filename, dictFileParser);
		
		dict.setType(type);
		return dict;
	}
	
	private static BioNERDictionary createDictionaryUseFileIndex(String filename, DictionaryFileParser dictFileParser)
	{
		BioNERTerm[] terms = dictFileParser.createTerms(filename);
		BioNERDictionary dict = new BioNERDictionary();
		for(BioNERTerm term : terms)
		{
			Vector<String> describ = term.getDescribe();
			for(int i=0; i<describ.size(); i++)
			{
				String orginalDescrib = describ.elementAt(i);
				String normalDescrib = strNormalizer.normalizeString(orginalDescrib);
				describ.set(i, normalDescrib);
			}
		}
		dict.setTermArray(terms);
		System.out.println("Begin to read index file:");
		dict.readIndexTableFromFile(filename+".index");
		return dict;
	}
	
	private static BioNERDictionary createDictionary(String filename, DictionaryFileParser dictFileParser)
	{
		BioNERDictionary dict = new BioNERDictionary();
		File checkFile = new File(filename+".normal");
		if(checkFile.exists())
		{
			System.out.println("Begin to read normalized dictionary file:");
			dict.ReadNormalizedDictionaryFromFile(filename+".normal");
		}
		else
		{
			System.out.println("Begin to read dictionary file:");
			BioNERTerm[] terms = dictFileParser.createTerms(filename);
			for(BioNERTerm term : terms)
			{
				Vector<String> describ = term.getDescribe();
				for(int i=0; i<describ.size(); i++)
				{
					String orginalDescrib = describ.elementAt(i);
					String normalDescrib = strNormalizer.normalizeString(orginalDescrib);
					describ.set(i, normalDescrib);
				}
			}
			dict.setTermArray(terms);
		}
		
		
		checkFile = new File(filename+".index");
		if(checkFile.exists())
		{
			System.out.println("Begin to read index file:");
			dict.readIndexTableFromFile(filename+".index");
		}
		else
		{
			System.out.println("Begin to add index:");
			int num=0;
			BioNERTerm[] terms = dict.getAllTerms();
			for(BioNERTerm term : terms)
			{
				num++;
				if(num%1000==0) System.out.println("Adding index for "+num+" term");
				//add index to the dictionary. Use the same tokenize and normalize method as the text sentence to the describe of the term.
				//Then add token--term index pair to the dictionary.
				
				
				Vector<String> describVector = term.getDescribe();
				for(int i=0; i<describVector.size(); i++)
				{
					String describ = describVector.elementAt(i);
					BioNERSentence termSentence = new BioNERSentence(describ,0);
					BioNERToken[] tokens = tokenizer.Tokenize(termSentence);
					for(BioNERToken token : tokens)
					{
						String word = token.getText();
						if(WordJudge.isWordIndex(word))
						{
							dict.addIndexPair(word, term);
						}
					}
				}
				
			}
		}
		return dict;
	}
}
