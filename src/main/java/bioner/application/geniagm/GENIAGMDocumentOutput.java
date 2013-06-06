package bioner.application.geniagm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import bioner.application.api.BioNERDocumentOutput;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;

public class GENIAGMDocumentOutput implements BioNERDocumentOutput {

	private BufferedWriter fwriter = null;
	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		try {
			fwriter = new BufferedWriter(new FileWriter("../../GENIAGM/gm.eval"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	synchronized public void outputDocument(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNEREntity  entity : document.getAbstractSentences()[0].getAllEntities())
		{
			if(!entity.get_Type().equals(GlobalConfig.PROTEIN_TYPE_LABEL)) continue;
			int begin = entity.get_Begin();
			int end = entity.get_End();
			
			String line = document.getID()+"|";
			line += begin+" "+end+"|"+entity.getText();
			try {
				fwriter.write(line);
				fwriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
