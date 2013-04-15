package temp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import bioner.data.builder.GENIADocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;

public class ConvertGENIAToGM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GENIADocumentBuilder builder = new GENIADocumentBuilder();
		BioNERDocument[] documents = builder.buildDocuments("./data/GENIA_corp/GENIAcorpus3.02.xml");
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../GENIAGM/all_sentence.txt"));
			BufferedWriter fwriter_result = new BufferedWriter(new FileWriter("../../GENIAGM/all_result.txt"));
			
			for(int i=0; i<1500; i++)
			{
				BioNERDocument document = documents[i];
				BioNERSentence[] sentences = document.getAllSentence();
				for(int j=0; j<sentences.length; j++)
				{
					BioNERSentence sentence = sentences[j];
					String sentenceID = document.getID()+"N"+j;
					fwriter.write(sentenceID+" "+sentence.getSentenceText().trim());
					fwriter.newLine();
					for(BioNEREntity entity : sentence.getAllEntities())
					{
						fwriter_result.write(sentenceID+"|"+entity.get_Begin()+" "+entity.get_End()+"|"+entity.getText());
						fwriter_result.newLine();
					}
				}
			}
			
			fwriter.close();
			fwriter_result.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
