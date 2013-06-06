/////////////////////////////////////////////////////////////////////////
//Usage: A implement for DictionaryFile Parser to parse the protein dictionary file.
//Author: Liu Jingchen
//Date: 2009/12/21
/////////////////////////////////////////////////////////////////////////
package bioner.application.geneterm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.tools.dictionary.BioNERTerm;
import bioner.tools.dictionary.DictionaryFileParser;


public class HumanGeneDictionaryFileParser implements DictionaryFileParser {

	@Override
	public BioNERTerm[] createTerms(String filename) {
		// TODO Auto-generated method stub
		Vector<BioNERTerm> termVector = new Vector<BioNERTerm>();
		
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line = freader.readLine();
			BioNERTerm term=null;
			System.out.println("Begin to read terms:");
			int num=0;
			while((line=freader.readLine()) != null)
			{
				num++;
				if(num%1000==0) System.out.println("Processing "+num+" term");
				String[] parts = line.split("\\s+");
				if(parts.length>2)
				{
					term = new BioNERTerm();
					term.setId(parts[1]);
					term.addDescribe(parts[2]);
					if(!parts[3].equals("-"))
					{
						String[] names = parts[3].split("\\|");
						
						for(String name : names)
						{
							if(name.equals("maps"))
							{
								int u=0;
								u++;
							}
							term.addDescribe(name);
						}
					}
					termVector.add(term);
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
