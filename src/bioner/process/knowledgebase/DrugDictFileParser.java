package bioner.process.knowledgebase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bioner.tools.dictionary.BioNERTerm;
import bioner.tools.dictionary.DictionaryFileParser;

public class DrugDictFileParser implements DictionaryFileParser {

	@Override
	public BioNERTerm[] createTerms(String filename) {
		// TODO Auto-generated method stub
		BioNERTerm[] terms = null;
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int size=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					size++;
				}
			}
			freader.close();
			freader = new BufferedReader(new FileReader(filename));
			terms = new BioNERTerm[size];
			int i=0;
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					terms[i] = new BioNERTerm();
					terms[i].setId(""+i);
					int pos = line.indexOf("\\b", 2);
					String describe = line.substring(2,pos);
					terms[i].addDescribe(describe.toLowerCase().trim());
					i++;
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
		
		return terms;
	}

}
