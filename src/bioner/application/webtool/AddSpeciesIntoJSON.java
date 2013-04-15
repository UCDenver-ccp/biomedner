package bioner.application.webtool;

import bioner.normalization.data.database.MySQLDatabaseReader;

public class AddSpeciesIntoJSON {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpeciesDatabaseReader speciesReader = new SpeciesDatabaseReader();
		MySQLDatabaseReader geneReader = new MySQLDatabaseReader();
		DatabaseJSONTableReader jsonReader = new DatabaseJSONTableReader();
		speciesReader.connect();
		geneReader.connect();
		jsonReader.connect();
		
		String[] pmcidArray = jsonReader.getAllPMCID();
		for(int i=0; i<pmcidArray.length; i++)
		{
			if(i%1==0) System.out.println("Processing #"+i);
			String pmcid = pmcidArray[i];
			String jsonStr = jsonReader.serachRecord(pmcid);
			JSONItem item = new JSONItem(pmcid, speciesReader, geneReader);
			item.readJSONString(jsonStr);
			item.addSpecies();
			jsonStr = item.toString();
			jsonReader.updateRecord(pmcid, jsonStr);
		}
		
		
		speciesReader.close();
		geneReader.close();
		jsonReader.close();
	}

}
