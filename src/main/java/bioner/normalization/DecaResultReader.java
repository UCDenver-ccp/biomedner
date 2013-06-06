package bioner.normalization;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNEREntity;

public class DecaResultReader {
	private static HashMap<String, Vector<DecaItem>> m_map = readDecaResultFile("./data/Deca_result.txt");
	private static HashMap<String, Vector<DecaItem>> readDecaResultFile(String filename)
	{
		HashMap<String, Vector<DecaItem>> map = new HashMap<String, Vector<DecaItem>>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine())!=null)
			{
				String[] parts = line.split("\\t+");
				if(parts.length!=4) continue;
				Vector<DecaItem> vector = map.get(parts[0]);
				if(vector==null)
				{
					vector = new Vector<DecaItem>();
					map.put(parts[0], vector);
				}
				DecaItem item = new DecaItem(parts[0], parts[1], parts[2], parts[3]);
				vector.add(item);
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
	
	public static Vector<String> getSpeicesIDForGeneMention(BioNEREntity entity)
	{
		Vector<String> speciesIDVector = new Vector<String>();
		String docID = entity.getDocument().getID();
		Vector<DecaItem> itemVector = m_map.get(docID);
		String gmText = entity.getText();
		String normalGMText = gmText.replaceAll("\\s+", "");
		for(DecaItem item : itemVector)
		{
			String itemStr = item.getEntityStr();
			if(gmText.equals(itemStr))
			{
				if(!speciesIDVector.contains(item.getSpeciesID()))
					speciesIDVector.add(item.getSpeciesID());
				continue;
			}
			String normalItemStr = itemStr.replaceAll("\\s+", "");
			if(normalGMText.equals(normalItemStr) || normalGMText.contains(normalItemStr) || normalItemStr.contains(normalGMText))
			{
				if(!speciesIDVector.contains(item.getSpeciesID()))
					speciesIDVector.add(item.getSpeciesID());
				continue;
			}
		}
		
		
		return speciesIDVector;
	}
	
}
