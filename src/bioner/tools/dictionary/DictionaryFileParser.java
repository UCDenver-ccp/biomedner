/////////////////////////////////////////////////////////////////////////////
//Usage: This is an interface for dictionary file parser. The input is only the filename of the dictionary file. It should read the file and build the term array.
//Author: Liu Jingchen
//Date: 2009/12/21
/////////////////////////////////////////////////////////////////////////////
package bioner.tools.dictionary;

public interface DictionaryFileParser {
	public abstract BioNERTerm[] createTerms(String filename);
}
