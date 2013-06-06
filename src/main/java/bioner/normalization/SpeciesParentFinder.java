package bioner.normalization;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import bioner.global.GlobalConfig;

public class SpeciesParentFinder {
	private static HashMap<String,String> m_parentTable = readParentTable(GlobalConfig.SPECIES_TREE_FILENAME);
	private static HashMap<String,String> readParentTable(String filename)
	{
		HashMap<String,String> table = new HashMap<String, String>();
		try {
			BufferedReader freader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			String line;
			while((line=freader.readLine())!=null)
			{
				String[] parts = line.split("\\s+");
				if(parts.length!=2) continue;
				if(!parts[0].equals(parts[1]))
					table.put(parts[0], parts[1]);
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
	public static boolean hasParent(String childID, Vector<String> parentIDVector)
	{
		for(String parentID : parentIDVector)
		{
			if(isParent(childID, parentID)) return true;
		}
		return false;
	}
	public static boolean isParent(String childID, String parentID)
	{
		String currentID = childID;
		int levelNum=0;
		while(currentID!=null)
		{
			if(currentID.equals(parentID)) return true;
			currentID = m_parentTable.get(currentID);
			levelNum++;
		}
		return false;
	}
	
	public static void main(String[] args)
	{
		boolean b = isParent("4530","3193");
		System.out.println(b);
	}
}
