package bioner.normalization.data.reader;

import java.util.HashMap;

import bioner.normalization.data.BioNERRecord;

public class TestRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EntrezGeneKnowledgeBaseReader reader = new EntrezGeneKnowledgeBaseReader("J:/source/Entrez Gene/gene_info");
		HashMap<String, BioNERRecord> recordMap = reader.getRecordTable();
		
	}

}
