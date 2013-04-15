package bioner.normalization.data.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import bioner.normalization.GeneMentionTokenizer;

public class LuceneSpeciesIndexBuilder {
	private static Vector<String> m_commonSpeciesVector = readCommonSpeciesList(IndexConfig.COMMON_SPEICIES_FILENAME);
	public static Vector<String> readCommonSpeciesList(String filename)
	{
		Vector<String> vector = new Vector<String>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0 && !vector.contains(line))
				{
					vector.add(line);
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
		
		return vector;
	}
	
	public static void indexKnowledgeBase(String filename)
	{
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		
		String indexFilename = IndexConfig.SPECIES_INDEX_DIRECTORY;

		

		try {
			Directory directory = FSDirectory.open(new File(indexFilename));
			IndexWriter iwriter = new IndexWriter(directory, analyzer, true,
	                new IndexWriter.MaxFieldLength(25000));
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int num=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()<=0 || line.startsWith("#")) continue;
				
				num++;
				if(num%10000==0) System.out.println("Processing #"+num);
				String[] parts = line.split("[\\@\\:]+");
				
				String id = parts[1];
				Vector<String> nameVector = getNameFeilds(parts[2].trim());
				/*
				String[] parts = line.split("\\|+");
				String id=parts[0].trim();
				String type=parts[3].trim();
				String name = parts[1].trim();
				if(!type.equals("scientific name") && !type.contains("common name")) continue;
				if(name.contains("(")) continue;
				Vector<String> nameVector = getNameFeilds(parts[1].trim());*/
				
				//If it is a common species, add its first word as its name. Like the Arabidopsis for Arabidopsis thaliana.
				if(m_commonSpeciesVector.contains(id))
				{
					addFirstWordName(nameVector);
				}
				
				Vector<String> textVector = new Vector<String>();
				for(String nameStr : nameVector)
				{
					Vector<String> tokenVector = GeneMentionTokenizer.getTokens(nameStr);
					StringBuffer textBuffer = new StringBuffer();
					for(String token : tokenVector)
					{
						textBuffer.append(token);
						textBuffer.append(" ");
					}
					String text = textBuffer.toString().toLowerCase();
					if(!textVector.contains(text))
					{
						textVector.add(text);
						Document doc = new Document();
						doc.add(new Field("names", text, Field.Store.YES, Field.Index.ANALYZED));
						doc.add(new Field("id", id, Field.Store.YES, Field.Index.NO));
						iwriter.addDocument(doc);
					}
				}
			}
			iwriter.close();
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static Vector<String> getNameFeilds(String line)
	{
		Vector<String> nameVector = new Vector<String>();
		String[] parts = line.split("\\~|\\,");
		for(String part : parts)
		{
			if(part.length()>0)
			{
				nameVector.add(part);
				nameVector.add(getPlural(part));
				if(part.endsWith("s"))
				{
					nameVector.add(part+"es");
				}
				else if(part.endsWith("y"))
				{
					nameVector.add(part.substring(0, part.length()-1)+"ies");
				}
				else
				{
					nameVector.add(part+"s");
				}
			}
		}
		
		//Convert names having two words.
		//Drosophila melanogaster--> D melanogaster; D mel; Drosophila mel; Drosophila melanogaster
		int size = nameVector.size();
		for(int i=0; i<size; i++)
		{
			String name = nameVector.elementAt(i);
			parts = name.split("\\s+");
			if(parts.length==2)
			{
				String[] firstWords;
				if(parts[0].matches("\\w+"))
				{
					firstWords = new String[2];
					firstWords[0] = parts[0].substring(0, 1);
					firstWords[1] = parts[0];
				}
				else
				{
					firstWords = new String[1];
					firstWords[0] = parts[0];
				}
				String[] secondWords;
				if(parts[1].matches("\\w{3,}.*"))
				{
					secondWords = new String[2];
					secondWords[0] = parts[1].substring(0, 3);
					secondWords[1] = parts[1];
				}
				else
				{
					secondWords = new String[1];
					secondWords[0] = parts[1];
				}
				for(String firstWord : firstWords)
				{
					for(String secondWord : secondWords)
					{
						String newName = firstWord + " " + secondWord;
						if(!nameVector.contains(newName))
						{
							nameVector.add(newName);
						}
					}
				}
			}
		}
		return nameVector;
	}
	
	public static String getPlural(String str)
	{
		if(str.endsWith("s"))
		{
			return str+"es";
		}
		else if(str.endsWith("y"))
		{
			return str.substring(0, str.length()-1)+"ies";
		}
		else
		{
			return str+"s";
		}
	}
	public static void addFirstWordName(Vector<String> nameVector)
	{
		int size = nameVector.size();
		for(int i=0; i<size; i++)
		{
			String name = nameVector.elementAt(i);
			String[] words = name.split("\\W+");
			if(words.length>1 && words[0].matches("[A-Z]\\w+") && !nameVector.contains(words[0]))
			{
				nameVector.add(words[0]);
			}
		}
	}
	public static void main(String[] args)
	{
		IndexConfig.ReadConfigFile();
		System.out.println("Indexing Species");
		indexKnowledgeBase(args[0]);
		System.out.println("Done!");
	}
}
