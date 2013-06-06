package bioner.normalization.data.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import bioner.normalization.GeneMentionTokenizer;

public class LuceneGeneIndexBuilder {

	public static void indexKnowledgeBase(String filename) {
		String indexFilename = IndexConfig.GENE_INDEX_DIRECTORY;
		indexKnowledgeBase(filename, indexFilename);
	}

	public static void indexKnowledgeBase(String filename, String indexFilename)
	{
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		

		

		try {
			Directory directory = FSDirectory.open(new File(indexFilename));
			IndexWriter iwriter = new IndexWriter(directory, analyzer, true,
	                new IndexWriter.MaxFieldLength(25000));
			BufferedReader freader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			String line;
			int num=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()<=0 || line.startsWith("#")) continue;
				
				num++;
				if(num%10000==0) System.out.println("Processing #"+num);
				String[] parts = line.split("\\t+");
				if(parts[2].equals("NEWENTRY")) continue;
				//if(!parts[0].equals("9606")) continue;
				Vector<String> nameVector = getNameFeilds(line);
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
					String text = textBuffer.toString();
					if(!textVector.contains(text))
					{
						textVector.add(text);
						Document doc = new Document();
						doc.add(new Field("names", text, Field.Store.YES, Field.Index.ANALYZED));
						doc.add(new Field("id", parts[1], Field.Store.YES, Field.Index.NO));
						iwriter.addDocument(doc);
					}
					nameStr = nameStr.toLowerCase();
					if(!textVector.contains(nameStr))
					{
						textVector.add(nameStr);
						Document doc = new Document();
						doc.add(new Field("names", nameStr, Field.Store.YES, Field.Index.ANALYZED));
						doc.add(new Field("id", parts[1], Field.Store.YES, Field.Index.NO));
						iwriter.addDocument(doc);
					}
				}
			}
			iwriter.close();
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		}
	}
	
	public static void indexKnowledgeBaseForIDF(String filename)
	{
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		
		String indexFilename = IndexConfig.GENE_INDEX_DIRECTORY+"_idf";

		

		try {
			Directory directory = FSDirectory.open(new File(indexFilename));
			IndexWriter iwriter = new IndexWriter(directory, analyzer, true,
	                new IndexWriter.MaxFieldLength(25000));
			BufferedReader freader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			String line;
			int num=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()<=0 || line.startsWith("#")) continue;
				
				num++;
				if(num%10000==0) System.out.println("Processing #"+num);
				String[] parts = line.split("\\t+");
				if(parts[2].equals("NEWENTRY")) continue;
				if(!parts[0].equals("9606")) continue;
				Vector<String> nameVector = getNameFeilds(line);
				StringBuffer textBuffer = new StringBuffer();
				for(String nameStr : nameVector)
				{
					Vector<String> tokenVector = GeneMentionTokenizer.getTokens(nameStr);
					
					for(String token : tokenVector)
					{
						textBuffer.append(token);
						textBuffer.append(" ");
					}
				}
				String text = textBuffer.toString();
				
				Document doc = new Document();
				doc.add(new Field("names", text, Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("id", parts[1], Field.Store.YES, Field.Index.NO));
				iwriter.addDocument(doc);
				
			}
			iwriter.close();
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		}
	}
	
	public static void indexKnowledgeBaseForSemantic(String filename)
	{
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		
		String indexFilename = IndexConfig.GENE_INDEX_DIRECTORY+"_semantic";

		

		try {
			Directory directory = FSDirectory.open(new File(indexFilename));
			IndexWriter iwriter = new IndexWriter(directory, analyzer, true,
	                new IndexWriter.MaxFieldLength(25000));
			BufferedReader freader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			String line;
			int num=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()<=0 || line.startsWith("#")) continue;
				
				num++;
				if(num%10000==0) System.out.println("Processing #"+num);
				String[] parts = line.split("\\t+");
				if(parts[2].equals("NEWENTRY")) continue;
				if(!parts[0].equals("9606")) continue;
				Vector<String> nameVector = getNameFeilds(line);
				StringBuffer textBuffer = new StringBuffer();
				Vector<String> tokenContainVector = new Vector<String>();
				for(String nameStr : nameVector)
				{
					if(nameStr.matches(".*\\b[a-z]+\\b.*"))
					{
						String[] tokenVector = nameStr.split("\\W+");
						
						for(String token : tokenVector)
						{
							if(!tokenContainVector.contains(token) || token.matches("[a-z]{3,}"))
							{
								tokenContainVector.add(token);
								textBuffer.append(token);
								textBuffer.append(" ");
							}
						}
					}
				}
				String text = textBuffer.toString();
				
				if(text.length()>0)
				{
					Document doc = new Document();
					doc.add(new Field("names", text, Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("id", parts[1], Field.Store.YES, Field.Index.NO));
					iwriter.addDocument(doc);
				}
				
			}
			iwriter.close();
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		}
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
	
	public static void main(String[] args)
	{
		IndexConfig.ReadConfigFile();
		System.out.println("Indexing Gene");
		if (args.length < 2) {
			indexKnowledgeBase(args[0]);
		}
		else {
			indexKnowledgeBase(args[0], args[1]);
		}
	
		System.out.println("Done!");
	}
}
