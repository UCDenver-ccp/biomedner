package bioner.application.speciesner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class SpeciesNEREval {

	public static String RESULT_FILE_PATH = "../../Species_NER/sp_ner_result.txt";
	public static String GOLD_FILE_PATH = "../../Species_NER/gold.txt";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Hashtable<String, Vector<String>> resultTable = getResultTable();
		Hashtable<String, Vector<String>> goldTable = getGoldTable();
		double precision = getPrecision(resultTable, goldTable);
		double recall = getRecall(resultTable, goldTable);
		double f = 2*precision*recall/(precision+recall);
		System.out.println("Precision:"+precision+" Recall:"+recall+" F:"+f);

	}
	public static Hashtable<String, Vector<String>> getResultTable()
	{
		Hashtable<String, Vector<String>> table = new Hashtable<String, Vector<String>>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(RESULT_FILE_PATH));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\t");
				if(parts.length==3)
				{
					String docID = parts[0];
					if(docID.equals("fly_00366_devtest.txt"))
					{
						int i=0;
						i++;
					}
					String speciesID = parts[1].substring(5);
					if(!table.keySet().contains(docID))
					{
						table.put(docID, new Vector<String>());
					}
					Vector<String> speciesVector = table.get(docID);
					if(!speciesVector.contains(speciesID))
					{
						speciesVector.add(speciesID);
					}
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
	public static Hashtable<String, Vector<String>> getGoldTable()
	{
		Hashtable<String, Vector<String>> table = new Hashtable<String, Vector<String>>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(GOLD_FILE_PATH));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\t");
				if(parts.length==5)
				{
					String docID = parts[0];
					
					String speciesID = parts[4].substring(10);
					if(!table.keySet().contains(docID) )
					{
						table.put(docID, new Vector<String>());
					}
					Vector<String> speciesVector = table.get(docID);
					if(!speciesVector.contains(speciesID)&& !speciesID.equals("0") && !speciesID.equals("-1"))
					{
						speciesVector.add(speciesID);
					}
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
	public static double getPrecision(Hashtable<String, Vector<String>> resultTable, Hashtable<String, Vector<String>> goldTable)
	{
		int totalNum = 0;
		int correctNum = 0;
		for(String docID : resultTable.keySet())
		{
			Vector<String> resultVector = resultTable.get(docID);
			totalNum += resultVector.size();
			Vector<String> goldVector = goldTable.get(docID);
			if(goldVector!=null)
			{
				for(String speciesID : resultVector)
				{
					if(goldVector.contains(speciesID))
					{
						correctNum++;
					}
					else
					{
						System.out.println("FP:"+docID+"\t"+speciesID);
					}
				}
			}
		}
		return (double)correctNum / (double)totalNum;
	}
	public static double getRecall(Hashtable<String, Vector<String>> resultTable, Hashtable<String, Vector<String>> goldTable)
	{
		int totalNum = 0;
		int correctNum = 0;
		for(String docID : goldTable.keySet())
		{
			
			Vector<String> goldVector = goldTable.get(docID);
			totalNum += goldVector.size();
			Vector<String> resultVector = resultTable.get(docID);
			if(resultVector!=null)
			{
				for(String speciesID : goldVector)
				{
					if(resultVector.contains(speciesID))
					{
						correctNum++;
					}
					else
					{
						System.out.println("FN:"+docID+"\t"+speciesID);
					}
				}
			}
		}
		return (double)correctNum / (double)totalNum;
	}
}
