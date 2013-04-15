package bioner.process.organismner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.tools.dictionary.BioNERTerm;
import bioner.tools.dictionary.DictionaryFileParser;

public class OrganismDictionaryFileParser implements DictionaryFileParser {

	@Override
	public BioNERTerm[] createTerms(String filename) {
		// TODO Auto-generated method stub
		Vector<BioNERTerm> termVector = new Vector<BioNERTerm>();
		
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			BioNERTerm term=null;
			System.out.println("Begin to read terms:");
			int num=0;
			while((line=freader.readLine()) != null)
			{
				if(line.startsWith("NCBI@"))
				{
					term = new BioNERTerm();
					termVector.add(term);
					num++;
					if(num%1000==0) System.out.println("Processing "+num+" term");
					String parts[] = line.split("\\:");
					term.setId(parts[0]);
					term.setDescribe(parts[1]);
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
		int size = termVector.size();
		BioNERTerm[] terms = new BioNERTerm[size];
		for(int i=0; i<size; i++)
		{
			terms[i] = termVector.elementAt(i);
		}
		return terms;
	}

}
