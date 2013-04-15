package bioner.normalization.data.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import bioner.normalization.data.BioNERRecord;

public class EntrezGeneKnowledgeBaseReader implements KnowledgeBaseReader {

	private String m_filename;
	public EntrezGeneKnowledgeBaseReader(String filename)
	{
		m_filename = filename;
	}
	@Override
	public HashMap<String, BioNERRecord> getRecordTable() {
		// TODO Auto-generated method stub
		HashMap<String, BioNERRecord> map = new HashMap<String, BioNERRecord>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(m_filename));
			String line;
			int num = 0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()<=0 || line.startsWith("#")) continue;
				num++;
				if(num%10000==0) System.out.println("Reading #"+num+" Record");
				String[] parts = line.split("\\t+");
				BioNERRecord record = new BioNERRecord();
				record.setSpeciesID(parts[0]);
				record.setID(parts[1]);
				record.setSymbol(parts[2]);
				if(!parts[4].equals("-")) record.setSynonyms(parts[4].split("\\|"));
				map.put(parts[1], record);
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return map;
	}

	

}
