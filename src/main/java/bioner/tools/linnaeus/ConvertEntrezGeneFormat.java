package bioner.tools.linnaeus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


public class ConvertEntrezGeneFormat {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		BufferedReader freader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0]))));
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(args[1]));
		String line;
		int num=0;
		while((line=freader.readLine())!=null)
		{
			
			if(line.length()<=0 || line.startsWith("#")) continue;
			
			num++;
			if(num%10000==0) System.out.println("Processing #"+num);
			String[] parts = line.split("\\t+");
			if(parts[2].equals("NEWENTRY")) continue;
			String id = parts[1];
			Vector<String> nameVector = getNameFeilds(line);
			fwriter.write("gene:ncbi:"+id);
			fwriter.write("\t");
			for(int i=0; i<nameVector.size(); i++)
			{
				if(i!=0) fwriter.write("|");
				fwriter.write(nameVector.elementAt(i));
			}
			fwriter.newLine();
		}
		fwriter.close();
		freader.close();
	}
	public static Vector<String> getNameFeilds(String line)
	{
		Vector<String> nameVector = new Vector<String>();
		String[] parts = line.split("\\t+");
		
		//Symbol
		nameVector.add(parts[2]);
		
		//LocusTag
		if(!parts[3].equals("-"))
			nameVector.add(parts[3]);
		
		//Synonyms
		if(!parts[4].equals("-"))
		{
			for(String names : parts[4].split("\\|"))
			{
				if(!nameVector.contains(names))
					nameVector.add(names);
			}
		}
		
		//description
		/*if(!parts[8].equals("-"))
		{
			for(String names : parts[8].split("\\|"))
			{
				if(!nameVector.contains(names))
					nameVector.add(names);
			}
		}*/
		
		//Symbol_from_nomenclature_authority
		if(!parts[10].equals("-"))
		{
			for(String names : parts[10].split("\\|"))
			{
				if(!nameVector.contains(names))
					nameVector.add(names);
			}
		}
		
		//Full_name_from_nomenclature_authority
		if(!parts[11].equals("-"))
		{
			for(String names : parts[11].split("\\|"))
			{
				if(!nameVector.contains(names))
					nameVector.add(names);
			}
		}
		
		//Other_designations
		if(!parts[13].equals("-"))
		{
			for(String names : parts[13].split("\\|"))
			{
				if(!nameVector.contains(names))
					nameVector.add(names);
			}
		}
		return nameVector;
	}
}
