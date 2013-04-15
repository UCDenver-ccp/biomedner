package bioner.application.speciesner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import bioner.application.api.BioNERDocumentOutput;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;

public class SpeciesNEROutput implements BioNERDocumentOutput {

	private BufferedWriter fwriter = null;
	private BufferedWriter nospeciesFwriter = null;
	private static String SPECIES_NER_RESULT_OUTPUTPATH = "../../Species_NER/sp_ner_result.txt";
	private Hashtable<String, Integer> speciesTFTable = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> speciesDFTable = new Hashtable<String, Integer>();
	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			fwriter.close();
			nospeciesFwriter.close();
			
			fwriter = new BufferedWriter(new FileWriter("../../Species_NER/sp_tf.txt"));
			for(String speciesID : speciesTFTable.keySet())
			{
				int tf = speciesTFTable.get(speciesID);
				int df = speciesDFTable.get(speciesID);
				fwriter.write(speciesID+"\t"+tf+"\t"+df);
				fwriter.newLine();
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
		int pos = SPECIES_NER_RESULT_OUTPUTPATH.lastIndexOf('/');
		String dirStr = SPECIES_NER_RESULT_OUTPUTPATH.substring(0, pos);
		File root = new File(dirStr);
		if(!root.exists())
		{
			root.mkdirs();
		}
		try {
			fwriter = new BufferedWriter(new FileWriter(SPECIES_NER_RESULT_OUTPUTPATH));
			nospeciesFwriter = new BufferedWriter(new FileWriter("../../Species_NER/no_species.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	synchronized public void outputDocument(BioNERDocument document) {
		// TODO Auto-generated method stub
		String id = document.getID();
		boolean hasSpecies = false;
		Vector<String> speciesIDVector = new Vector<String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				hasSpecies = true;
				String entityText = entity.getText();
				for(String entityID : entity.getID())
				{
					String line = id + "\t"+entityID+"\t"+entityText;
					try {
						fwriter.write(line);
						fwriter.newLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Integer tf = speciesTFTable.get(entityID);
					if(tf==null) tf=0;
					tf++;
					speciesTFTable.put(entityID, tf);
					if(!speciesIDVector.contains(entityID))
					{
						speciesIDVector.add(entityID);
					}
				}
			}
		}
		for(String speciesID : speciesIDVector)
		{
			Integer df = speciesDFTable.get(speciesID);
			if(df==null) df=0;
			df++;
			speciesDFTable.put(speciesID, df);
		}
		try {
			fwriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!hasSpecies)
		{
			try {
				nospeciesFwriter.write(document.getID());
				nospeciesFwriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
