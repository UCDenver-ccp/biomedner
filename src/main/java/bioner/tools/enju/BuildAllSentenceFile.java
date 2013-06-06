package bioner.tools.enju;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.BC2GNDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNERSentence;

public class BuildAllSentenceFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder("../../BC2GN/data/trainingData");
		BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../software/bc2gn_all.txt"));
		BioNERDocument[] documents = docBuilder.buildDocuments();
		for(int i=0; i<documents.length; i++)
		{
			for(BioNERSentence sentence : documents[i].getAllSentence())
			{
				fwriter.write(sentence.getSentenceText());
				fwriter.newLine();
			}
		}
		docBuilder = new BC2GNDocumentBuilder("../../BC2GN/data/testingData");
		documents = docBuilder.buildDocuments();
		for(int i=0; i<documents.length; i++)
		{
			for(BioNERSentence sentence : documents[i].getAllSentence())
			{
				fwriter.write(sentence.getSentenceText());
				fwriter.newLine();
			}
		}
		fwriter.close();
	}

}
