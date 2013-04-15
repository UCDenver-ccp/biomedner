/////////////////////////////////////////////////////////////////////////
//Usage: A implement for DictionaryFile Parser to parse the protein dictionary file.
//Author: Liu Jingchen
//Date: 2009/12/21
/////////////////////////////////////////////////////////////////////////
package bioner.process.proteinner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.tools.dictionary.BioNERTerm;
import bioner.tools.dictionary.DictionaryFileParser;


public class ProteinDictionaryFileParser implements DictionaryFileParser {

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
				if(line.startsWith("####"))
				{
					term = new BioNERTerm();
					termVector.add(term);
					num++;
					if(num%1000==0) System.out.println("Processing "+num+" term");
				}
				else if(line.startsWith("ID:"))
				{
					int pos = line.indexOf('_');
					String subStr;
					if(pos<0) subStr = line.substring(3);
					else subStr = line.substring(3,pos);
					term.addDescribe(subStr);
				}
				else if(line.startsWith("AC:"))
				{
					int pos = line.indexOf('~');
					String subStr;
					if(pos<0) subStr = line.substring(3);
					else subStr = line.substring(3,pos);
					term.setId(subStr);
				}
				else if(line.startsWith("DE:"))
				{
					String subStr;
					subStr = line.substring(3);
					String[] parts = subStr.split("\\~");
					for(int i=0; i<parts.length; i++)
					{
						if(!parts[i].contains("EC@EC"))
						{
							term.addDescribe(parts[i]);
						}
					}
					
				}
				else if(line.startsWith("GN:"))
				{
					String subStr;
					subStr = line.substring(3);
					String[] parts = subStr.split("\\~");
					String newStr = "";
					for(String part : parts)
					{
						int pos = part.indexOf('@');
						String subPart;
						subPart = part.substring(pos+1);
						newStr += "~"+subPart;
					}
					term.addDescribe(newStr);
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
