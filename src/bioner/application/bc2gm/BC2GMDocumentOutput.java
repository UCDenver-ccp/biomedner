package bioner.application.bc2gm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import bioner.application.api.BioNERDocumentOutput;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;

public class BC2GMDocumentOutput implements BioNERDocumentOutput {

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
			fwriter = new BufferedWriter(new FileWriter("../../BC2GM/gm.eval"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	synchronized public void outputDocument(BioNERDocument document) {
		// TODO Auto-generated method stub
		String sentenceStr = document.getAbstractSentences()[0].getSentenceText();
		for(BioNEREntity  entity : document.getAbstractSentences()[0].getAllEntities())
		{
			//if(!entity.get_Type().equals(GlobalConfig.PROTEIN_TYPE_LABEL)) continue;
			int begin = entity.get_Begin();
			int end = entity.get_End();
			int unspaceNum = 0;
			int spaceNum = 0;
			
			for(int i=0; i<=begin; i++)
			{
				try{
				if(sentenceStr.charAt(i)==' ') spaceNum++;
				else unspaceNum++;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.println(sentenceStr);
					System.out.println("i="+i+" begin="+begin+" end="+end);
					System.exit(0);
				}
			}
			begin -= spaceNum;
			
			unspaceNum = 0;
			spaceNum = 0;
			for(int i=0; i<end; i++)
			{
				try{
				if(sentenceStr.charAt(i)==' ') spaceNum++;
				else unspaceNum++;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.println(sentenceStr);
					System.out.println("i="+i+" begin="+begin+" end="+end);
					System.exit(0);
				}
			}
			end -= spaceNum;
			
			String line = document.getID()+"|";
			
			if(entity.getText().endsWith(".")) end--;
			
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
