/////////////////////////////////////////////////////////////////////////
//Usage: This class is used to build a hash table, containing proteinAC-->OrgnismID pairs
//Author: Liu Jingchen
//Date: 2009/12/10
/////////////////////////////////////////////////////////////////////////
package bioner.process.orgnismnormal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import bioner.global.GlobalConfig;

public class ProteinOrgnismTableBuilder {
	private static Hashtable<String,String> m_table = null;
	
	public static Hashtable<String,String> getTable()
	{
		if(m_table==null)
		{
			m_table = createTable(GlobalConfig.PROTEIN_DICT_PATH);
		}
		return m_table;
	}
	
	private static Hashtable<String,String> createTable(String filename)
	{
		Hashtable<String,String> table = new Hashtable<String, String>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			String currentAC = "";
			String currentOS = "";
			while((line=freader.readLine()) != null)
			{
				if(line.startsWith("AC:"))
				{
					String subStr = line.substring(3);
					String parts[] = subStr.split("~");
					currentAC = parts[0];
				}
				else if(line.startsWith("OS:"))
				{
					String subStr = line.substring(3);
					String parts[] = subStr.split("~");
					currentOS = parts[0];
					table.put(currentAC, currentOS);
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
		return table;
	}
}
