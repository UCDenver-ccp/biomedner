package bioner.normalization.data.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class PickUsefulSpeciesIDFromNCBI {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		boolean[] usefulArray = getUnusefulGeneID("../../EntrezGene/species/nodes.dmp");
		BufferedReader freader = new BufferedReader(new FileReader("../../EntrezGene/species/names.dmp"));
		BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../EntrezGene/species/names_refined.dmp"));
		String line;
		int num=0;
		while((line=freader.readLine())!=null)
		{
			num++;
			if(num%1000==0) System.out.println("#"+num);
			String[] parts = line.split("\\|+");
			String id = parts[0].trim();
			int idNum = Integer.parseInt(id);
			if(usefulArray[idNum])
			{
				fwriter.write(line);
				fwriter.newLine();
			}
		}
		fwriter.close();
		freader.close();
	}
	private static boolean[] getUnusefulGeneID(String filename) throws IOException
	{
		boolean[] array = new boolean[10000000];
		for(int i=0; i<array.length; i++)
		{
			array[i] = true;
		}
		BufferedReader freader = new BufferedReader(new FileReader(filename));
		String line;
		while((line=freader.readLine())!=null)
		{
			String[] parts = line.split("\\|+");
			if(parts.length<3) continue;
			String id = parts[0].trim();
			String rank = parts[2].trim();
			if(!(rank.equals("species") || rank.equals("genus") || rank.equals("subgenus") || rank.equals("subspecies")))
			{
				int idNum = Integer.parseInt(id);
				array[idNum] = false;
			}
		}
		return array;
	}
}
