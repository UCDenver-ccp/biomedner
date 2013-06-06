package bioner.normalization.data.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bioner.global.GlobalConfig;

public class DatabaseConfig {
	public static String DATABASE_HOST = "localhost";
	public static String DATABASE_NAME = "EntrezGene";
	public static String DATABASE_USERNAME = "annotation";
	public static String DATABASE_PASSWORD = "12345";
	public static String DATABASE_GENEINFO_TABLE_NAME = "gene_info";
	public static String DATABASE_JSON_TABLE_NAME = "json";
	public static String DATABASE_SPECIES_TABLE_NAME = "species";
	public static String DATAFILE_DIR = GlobalConfig.ROOT_DIR+"xml/";
	public static String DATABASE_DRIVER_NAME = "mysql";
	public static String DATABASE_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	public static int THREAD_NUM = 1;
	public static void ReadConfigFile()
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(GlobalConfig.CONFIG_FILE_PATH));
			String line;
			while((line=freader.readLine()) != null)
			{
				if(line.startsWith("DATABASE_HOST"))
				{
					String valueStr = getValueStr(line);
					DATABASE_HOST = valueStr;
				}
				else if(line.startsWith("DATABASE_NAME"))
				{
					String valueStr = getValueStr(line);
					DATABASE_NAME = valueStr;
				}
				else if(line.startsWith("DATABASE_USERNAME"))
				{
					String valueStr = getValueStr(line);
					DATABASE_USERNAME = valueStr;
				}
				else if(line.startsWith("DATABASE_PASSWORD"))
				{
					String valueStr = getValueStr(line);
					DATABASE_PASSWORD = valueStr;
				}
				else if(line.startsWith("DATABASE_GENEINFO_TABLE_NAME"))
				{
					String valueStr = getValueStr(line);
					DATABASE_GENEINFO_TABLE_NAME = valueStr;
				}
				else if(line.startsWith("DATABASE_JSON_TABLE_NAME"))
				{
					String valueStr = getValueStr(line);
					DATABASE_JSON_TABLE_NAME = valueStr;
				}
				else if(line.startsWith("DATAFILE_DIR"))
				{
					String valueStr = getValueStr(line);
					DATAFILE_DIR = valueStr;
				}
				else if(line.startsWith("THREAD_NUM"))
				{
					String valueStr = getValueStr(line);
					THREAD_NUM = Integer.parseInt(valueStr);
				}
				else if(line.startsWith("DATABASE_DRIVER_NAME"))
				{
					DATABASE_DRIVER_NAME = getValueStr(line);
				}
				else if(line.startsWith("DATABASE_DRIVER_CLASS"))
				{
					DATABASE_DRIVER_CLASS = getValueStr(line);
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
}
