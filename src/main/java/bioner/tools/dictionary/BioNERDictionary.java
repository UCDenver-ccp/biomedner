//////////////////////////////////////////////////////////
//Usage: This class is a dictionary. It can be seen as a set of BioNERTerm, providing two kinds of index for looking up.
//		One is by the id of the term, the other is by a string type index. The second type of indexing should be added manually, using the method:getTermsByIndex()
//Author: Liu Jingchen
//Date: 2009/12/7
//////////////////////////////////////////////////////////
package bioner.tools.dictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class BioNERDictionary {
	private Vector<BioNERTerm> m_termVector = new Vector<BioNERTerm>();
	private Hashtable<String,BioNERTerm> m_IDTable = new Hashtable<String, BioNERTerm>();
	private Hashtable<String,Vector<BioNERTerm>> m_IndexTable = new Hashtable<String, Vector<BioNERTerm>>();
	//private TreeTable<BioNERTerm> m_IDTable = new TreeTable<BioNERTerm>();
	//private TreeTable<Vector<BioNERTerm>> m_IndexTable = new TreeTable<Vector<BioNERTerm>>();
	
	private String m_type = "NoType";
	
	public void addTerm(BioNERTerm term)
	{
		if(m_termVector.contains(term)) return;
		m_termVector.add(term);
		m_IDTable.put(term.getId(), term);
	}
	public void setTermArray(BioNERTerm[] terms)
	{
		m_termVector.clear();
		m_termVector.setSize(terms.length);
		for(int i=0; i<terms.length; i++)
		{
			BioNERTerm term = terms[i];
			m_termVector.set(i, term);
			m_IDTable.put(term.getId(), term);
		}
	}
	public void addIndexPair(String index, BioNERTerm term)
	{
		addTerm(term);
		Vector<BioNERTerm> termVector = m_IndexTable.get(index);
		if(termVector==null)
		{
			termVector = new Vector<BioNERTerm>();
			m_IndexTable.put(index, termVector);
		}
		if(!termVector.contains(term))
		{
			termVector.add(term);
		}
	}
	public BioNERTerm getTermByID(String id)
	{
		BioNERTerm term = m_IDTable.get(id);
		return term;
	}
	public BioNERTerm[] getTermsByIndex(String index)
	{
		Vector<BioNERTerm> termVector = m_IndexTable.get(index);
		if(termVector==null) return null;
		int size = termVector.size();
		BioNERTerm[] terms = new BioNERTerm[size];
		for(int i=0; i<size; i++)
		{
			terms[i] = termVector.elementAt(i);
		}
		return terms;
	}
	public BioNERTerm[] getAllTerms()
	{
		int size = this.m_termVector.size();
		BioNERTerm[] terms = new  BioNERTerm[size];
		for(int i=0; i<size; i++)
		{
			terms[i] = this.m_termVector.elementAt(i);
		}
		return terms;
	}
	
	
	//This method can save the index table into a local file. The dictionary can read the file to build the index table.
	//This might be useful when the dictionary is too large.
	//Format:
	//key|id_1|id_2|id_3....
	public void writeIndexTableToFile(String filename)
	{
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(filename));
			for(String key : m_IndexTable.keySet())
			{
				Vector<BioNERTerm> termVector = m_IndexTable.get(key);
				String line = key;
				for(BioNERTerm term : termVector)
				{
					line += "|"+term.getId();
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
	public void readIndexTableFromFile(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int num=0;
			while((line=freader.readLine()) != null)
			{
				num++;
				if(num%1000==0)System.out.println("Reading index file line #"+num);
				if(line.contains("|"))
				{
					String[] parts = line.split("\\|");
					String index = parts[0];//First part is the index.
					
					//Get the Vector for this index.
					Vector<BioNERTerm> termVector = m_IndexTable.get(index);;
					
					if(termVector==null)
					{
						termVector = new Vector<BioNERTerm>();
						m_IndexTable.put(index, termVector);
					}
					
					termVector.clear();
					termVector.setSize(parts.length-1);
					//Add terms into this vector.
					for(int i=1;i<parts.length; i++)
					{
						BioNERTerm term = getTermByID(parts[i]);
						if(term!=null)
						{
							termVector.set(i-1, term);
						}
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
	}
	
	public void WriterNormalizedDictionaryToFile(String filename)
	{
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(filename));
			for(BioNERTerm term : this.m_termVector)
			{
				String line = term.getId()+"|";
				for(String describ : term.getDescribe())
				{
					line += describ+"~";
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
	public void ReadNormalizedDictionaryFromFile(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int size = 0;
			while((line=freader.readLine()) != null)
			{
				if(line.contains("|"))
				{
					size++;
				}
			}
			freader.close();
			BioNERTerm[] terms = new BioNERTerm[size];
			freader = new BufferedReader(new FileReader(filename));
			int num=0;
			int i=0;
			while((line=freader.readLine()) != null)
			{
				num++;
				if(num%1000==0)System.out.println("Reading normalized dictionary line #"+num);
				if(line.contains("|"))
				{
					String[] parts = line.split("\\|");
					BioNERTerm term = new BioNERTerm();
					term.setId(parts[0]);
					term.setDescribe(parts[1]);
					terms[i] = term;
				}
				i++;
			}
			freader.close();
			setTermArray(terms);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void setType(String m_type) {
		this.m_type = m_type;
		for(BioNERTerm term: this.m_termVector)
		{
			term.setType(m_type);
		}
	}
	public String getType() {
		return m_type;
	}
}
