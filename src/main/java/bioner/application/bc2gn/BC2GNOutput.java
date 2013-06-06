package bioner.application.bc2gn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERDocumentOutput;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.data.BioNERCandidate;

public class BC2GNOutput implements BioNERDocumentOutput {

	private BufferedWriter fwriter = null;
	private String output_filename = null;
	public BC2GNOutput()
	{
		
	}
	public BC2GNOutput(String filename)
	{
		output_filename = filename;
	}
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
		
		int pos = GlobalConfig.BC2_GN_TEST_RESULT_OUTPUTPATH.lastIndexOf('/');
		String dirStr = GlobalConfig.BC2_GN_TEST_RESULT_OUTPUTPATH.substring(0, pos);
		File root = new File(dirStr);
		if(!root.exists())
		{
			root.mkdirs();
		}
		try {
			if(output_filename==null)
				fwriter = new BufferedWriter(new FileWriter(GlobalConfig.BC2_GN_TEST_RESULT_OUTPUTPATH));
			else
				fwriter = new BufferedWriter(new FileWriter(output_filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	synchronized public void outputDocument(BioNERDocument document) {
		// TODO Auto-generated method stub
		HashMap<String, Vector<String>> resultTable = new HashMap<String, Vector<String>>();
		String id = document.getID();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				
				BioNERCandidate[] candidates = entity.getCandidates();
				if(candidates.length>0)
				{
					for(int i=0; i<1 && i<candidates.length; i++)
					{
						String geneID = candidates[i].getRecordID();
						String speciesID = candidates[i].getRecord().getSpeciesID();
						double score = candidates[i].getScore();
						//if(score<18) continue;
						if(speciesID.equals("9606"))
						{
							Vector<String> textVector = resultTable.get(geneID);
							if(textVector==null)
							{
								textVector = new Vector<String>();
								resultTable.put(geneID, textVector);
							}
							String text = entity.getText();
							if(!textVector.contains(text))
							{
								textVector.add(text);
							}
						}
					}//for(int i=0; i<2 && i<candidates.length; i++)
					
				}//if(candidates.length>0)
			}//for each entity
		}
		try {
			for(String geneID : resultTable.keySet())
			{
				Vector<String> textVector = resultTable.get(geneID);
				StringBuffer lineBuffer = new StringBuffer(id+"\t"+geneID);
				for(String text : textVector)
				{
					lineBuffer.append("\t"+text);
				}
				fwriter.write(lineBuffer.toString());
				fwriter.newLine();
			}
			
			fwriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void outputGeneIDs(BioNERDocument document, HashMap<String, Vector<BioNEREntity>> geneIDMap, BioNERCandidate[] candidates)
	{
		String docID = document.getID();
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i]==null) continue;
			try {
				fwriter.write(docID+"\t"+candidates[i].getRecord().getID()+"\t"+Double.toString(candidates[i].getScore()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector<String> gmVector = new Vector<String>();
			Vector<BioNEREntity> entityVector = geneIDMap.get(candidates[i].getRecord().getID());
			for(BioNEREntity entity : entityVector)
			{
				if(!gmVector.contains(entity.getText()))
				{
					try {
						fwriter.write("\t"+entity.getText());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gmVector.add(entity.getText());
				}
			}
			try {
				fwriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
