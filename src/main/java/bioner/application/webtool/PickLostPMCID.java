package bioner.application.webtool;

import java.io.File;

import bioner.normalization.data.database.DatabaseConfig;

public class PickLostPMCID {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DatabaseJSONTableReader jsonReader = new DatabaseJSONTableReader();
		jsonReader.connect();
		String[] pmcidArray = jsonReader.getAllPMCID();
		jsonReader.close();
		DatabaseConfig.ReadConfigFile();
		File rootFile  = new File("/home/ljc/WebTool/xml_all");
		File[] files = rootFile.listFiles();
		for(File file : files)
		{
			String filename = file.getName();
			int pos = filename.indexOf('.');
			String pmcid = filename.substring(0, pos);
			boolean haveID = false;
			for(String id : pmcidArray)
			{
				if(id.equals(pmcid))
				{
					haveID = true;
					break;
				}
			}
			if(!haveID)
			{
				System.out.println(pmcid);
			}
		}
	}

}
