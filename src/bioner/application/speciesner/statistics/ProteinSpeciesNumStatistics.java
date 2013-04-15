package bioner.application.speciesner.statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import bioner.process.orgnismnormal.ProteinOrgnismTableBuilder;
import bioner.process.proteinner.ProteinDictionaryBuilder;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictionary.BioNERTerm;

public class ProteinSpeciesNumStatistics {
	private BioNERDictionary m_proteinDict = ProteinDictionaryBuilder.getProteinDictionary();
	private Hashtable<String,String> ac_orgTable = ProteinOrgnismTableBuilder.getTable();
	private Hashtable<String, Vector<String>> proteinSpeciesTable = new Hashtable<String, Vector<String>>();
	private Hashtable<String, Vector<String>> proteinACTable = new Hashtable<String, Vector<String>>();
	
	public Hashtable<String, Vector<String>> getProteinSpeceisTable()
	{
		return this.proteinSpeciesTable;
	}
	
	public void buildProteinSpeciesTable()
	{
		proteinSpeciesTable.clear();
		for(BioNERTerm term : m_proteinDict.getAllTerms())
		{
			String acID = term.getId();
			String speciesID = ac_orgTable.get(acID);
			for(String proteinName : term.getDescribe())
			{
				Vector<String> speciesVector = proteinSpeciesTable.get(proteinName);
				if(speciesVector==null)
				{
					speciesVector = new Vector<String>();
					proteinSpeciesTable.put(proteinName, speciesVector);
				}
				if(!speciesVector.contains(speciesID))
				{
					speciesVector.add(speciesID);
				}
				Vector<String> acVector = proteinACTable.get(proteinName);
				if(acVector==null)
				{
					acVector = new Vector<String>();
					proteinACTable.put(proteinName, acVector);
				}
				if(!acVector.contains(acID))
				{
					acVector.add(acID);
				}
			}
		}
	}
	public void writeTableToFile(String filename)
	{
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(filename));
			for(String proteinName : proteinSpeciesTable.keySet())
			{
				Vector<String> speciesVector = proteinSpeciesTable.get(proteinName);
				String line = proteinName;
				for(String speciesID : speciesVector)
				{
					line += "|"+speciesID;
				}
				fwriter.write(line);
				fwriter.newLine();
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeUniqProteinToFile(String filename)
	{
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(filename));
			for(String proteinName : proteinSpeciesTable.keySet())
			{
				Vector<String> speciesVector = proteinSpeciesTable.get(proteinName);
				String line = proteinName.replaceAll("\\_", " ");
				if(speciesVector.size()==1)
				{
					fwriter.write(line);
					fwriter.newLine();
				}
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void countUniqueProtein()
	{
		int uniqueNum = 0;
		int mulitNum = 0;
		
		int oneAC = 0;
		int moreAC = 0;
		int oneSponeToken = 0;
		
		int sp10Num = 0;
		int sp20Num = 0;
		int sp30Num = 0;
		int sp40Num = 0;
		int sp50Num = 0;
		int spMoreNum = 0;
		int maxNum = 0;
		for(String proteinName : proteinSpeciesTable.keySet())
		{
			Vector<String> speciesVector = proteinSpeciesTable.get(proteinName);
			String line = proteinName;
			if(speciesVector.size()==1)
			{
				uniqueNum++;
				Vector<String> acVector = proteinACTable.get(proteinName);
				if(acVector.size()==1)
				{
					oneAC++;
				}
				else
				{
					moreAC++;
				}
				
			}
			else
			{
				
				mulitNum++;
				int size = speciesVector.size();
				if(size>maxNum) maxNum=size;
				if(size>1&&size<=10)
				{
					sp10Num++;
				}
				else if(size<=20)
				{
					sp20Num++;
				}
				else if(size<=30)
				{
					sp30Num++;
				}
				else if(size<=40)
				{
					sp40Num++;
				}else if(size<=50)
				{
					sp50Num++;
				}else
				{
					spMoreNum++;
				}
			}
			
			String[] parts = proteinName.split("\\W+");
			if(parts.length==1)
			{
				oneSponeToken++;
			}
		}
		System.out.println("One Speces:"+uniqueNum+" More Species:"+mulitNum);
		System.out.println("One AC:"+oneAC+" More AC:"+moreAC);
		System.out.println("One tokens in One Species:"+oneSponeToken);
		System.out.println("10:"+sp10Num+" 20:"+sp20Num+" 30:"+sp30Num+" 40:"+sp40Num+" 50:"+sp50Num+" more:"+spMoreNum+" max:"+maxNum);
	}
}
