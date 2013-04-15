package bioner.normalization.data.index;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bioner.global.GlobalConfig;

public class IndexConfig {
	public static String GENE_INDEX_DIRECTORY = GlobalConfig.ROOT_DIR+"index/gene";
	//public static String INDEX_DIRECTORY = "../../EntrezGene/index_human";
	
	public static String SPECIES_INDEX_DIRECTORY = GlobalConfig.ROOT_DIR+"index/species";
	public static String COMMON_SPEICIES_FILENAME = GlobalConfig.ROOT_DIR+"data/dict/common_species.txt";
	public static int MAX_RESULT_NUM = 50;

	public static void ReadConfigFile()
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(GlobalConfig.CONFIG_FILE_PATH));
			String line;
			while((line=freader.readLine()) != null)
			{
				if(line.startsWith("GENE_INDEX_DIRECTORY"))
				{
					String valueStr = getValueStr(line);
					GENE_INDEX_DIRECTORY = valueStr;
				}
				else if(line.startsWith("SPECIES_INDEX_DIRECTORY"))
				{
					String valueStr = getValueStr(line);
					SPECIES_INDEX_DIRECTORY = valueStr;
				}
				else if(line.startsWith("COMMON_SPEICIES_FILENAME"))
				{
					String valueStr = getValueStr(line);
					COMMON_SPEICIES_FILENAME = valueStr;
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
	private static String getValueStr(String line)
	{
		int pos = line.indexOf('=');
		String valueStr = line.substring(pos+1).trim();
		return valueStr;
	}
	public static void main(String[] args)
	{
		ReadConfigFile();
	}
}
