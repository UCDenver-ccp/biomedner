package bioner.application.speciesner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import bioner.application.speciesner.statistics.ProteinSpeciesNumStatistics;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;
import bioner.tools.strnormal.StringNormalizer;
import bioner.tools.strnormal.StringNormalizerFactory;

public class ProcessImpAddSpeciesInProteinName implements BioNERProcess {

	Hashtable<String, Vector<String>> proteinTable = new Hashtable<String, Vector<String>>();
	Hashtable<String, Vector<String>> proteinSpeciesTable;
	private StringNormalizer normalizer = StringNormalizerFactory.getStringNormalizer();
	public ProcessImpAddSpeciesInProteinName()
	{
		ReadProteinFile();
		ProteinSpeciesNumStatistics statistics = new ProteinSpeciesNumStatistics();
		proteinSpeciesTable = statistics.getProteinSpeceisTable();
	}
	
	private void ReadProteinFile()
	{
		String filename = "J:/Species_NER/gold.txt";
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\s+");
				if(parts.length>0)
				{
					String id = parts[0];
					String proteinName = parts[3];
					
					Vector<String> proteinVector = proteinTable.get(id);
					if(proteinVector==null)
					{
						proteinVector = new Vector<String>();
						proteinTable.put(id, proteinVector);
					}
					if(!proteinVector.contains(proteinName))
						proteinVector.add(proteinName);
					
				}
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean containSpecies(String name)
	{
		if(name.length()<2) return false;
		char c = name.charAt(1);
		if(!(c>='A'&&c<='Z') && c!='-') return false;
		if(name.startsWith("h")) return true;
		if(name.startsWith("r")) return true;
		if(name.startsWith("m")) return true;
		if(name.startsWith("d")) return true;
		if(name.startsWith("y")) return true;
		return false;
	}
	private String getSpeciesID(String name)
	{
		if(name.startsWith("h")) return "NCBI@9606";
		if(name.startsWith("r")) return "NCBI@10116";
		if(name.startsWith("m")) return "NCBI@10090";
		if(name.startsWith("d")) return "NCBI@7227";
		if(name.startsWith("y")) return "NCBI@4932";
		return null;
	}
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		Vector<String> proteinVector = proteinTable.get(document.getID());
		
		if(proteinVector==null) return;
		for(String proteinName : proteinVector)
		{
			String normalizedName = normalizer.normalizeString(proteinName);
			Vector<String> speciesVector = proteinSpeciesTable.get(normalizedName);
			if(containSpecies(proteinName))
			{
				for(BioNERSentence sentence : document.getAllSentence())
				{
					int pos = sentence.getSentenceText().indexOf(proteinName);
					if(pos>0)
					{
						BioNEREntity entity = new BioNEREntity();
						entity.set_Type(GlobalConfig.ORGANISM_TYPE_LABEL);
						String speciesID = getSpeciesID(proteinName);
						entity.addID(speciesID);
						entity.set_Sentence(sentence);
						entity.set_position(pos, pos+proteinName.length());
						sentence.addEntity(entity);
					}
				}
			}
			else if(speciesVector!=null && speciesVector.size()==1)
			{
				for(BioNERSentence sentence : document.getAllSentence())
				{
					int pos = sentence.getSentenceText().indexOf(proteinName);
					if(pos>0)
					{
						BioNEREntity entity = new BioNEREntity();
						entity.set_Type(GlobalConfig.ORGANISM_TYPE_LABEL);
						String speciesID = speciesVector.elementAt(0);
						entity.addID(speciesID);
						entity.set_Sentence(sentence);
						entity.set_position(pos, pos+proteinName.length());
						sentence.addEntity(entity);
					}
				}
			}
		}
	}

}
