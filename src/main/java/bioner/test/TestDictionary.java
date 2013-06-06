package bioner.test;

import bioner.process.proteinner.ProteinDictionaryBuilder;
import bioner.tools.dictionary.BioNERDictionary;

public class TestDictionary {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BioNERDictionary dict = ProteinDictionaryBuilder.getProteinDictionary();
		dict.writeIndexTableToFile("../../dict/dict.index");
		dict.WriterNormalizedDictionaryToFile("../../dict/dict.normal");
	}

}
