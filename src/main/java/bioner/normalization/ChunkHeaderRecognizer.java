package bioner.normalization;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

public class ChunkHeaderRecognizer {
	
	private HashMap<String,Vector<TypedDependency>> tdMap = new HashMap<String, Vector<TypedDependency>>();
	
	
	private static ChunkHeaderRecognizer recognizer = new ChunkHeaderRecognizer("../../BC2GN/parsed_data.txt");
	public static ChunkHeaderRecognizer getRecognizer()
	{
		return recognizer;
	}
	public ChunkHeaderRecognizer(String cachedParsedFile)
	{
		readCachedTypeDependencyFile(cachedParsedFile);
	}
	
	private void readCachedTypeDependencyFile(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine())!=null)
			{
				String[] parts = line.split("\\|");
				if(parts.length<1) continue;
				Vector<TypedDependency> tdVector = new Vector<TypedDependency>();
				for(int i=1; i<parts.length; i++)
				{
					String[] elements = parts[i].split("\\s+");
					if(elements.length!=3) continue;
					String type = elements[0];
					int pos = elements[1].indexOf('-');
					String govIndexStr = elements[1].substring(pos+1);
					pos = elements[2].indexOf('-');
					String depIndexStr = elements[2].substring(pos+1);
					int govIndex = Integer.parseInt(govIndexStr)-1;
					int depIndex = Integer.parseInt(depIndexStr)-1;
					TypedDependency td = new TypedDependency(type, govIndex, depIndex);
					tdVector.add(td);
				}
				tdMap.put(parts[0], tdVector);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Vector<Integer> getChunkHeaders(BioNERSentence sentence, int begin, int end)
	{
		Vector<Integer> headerIndexVector = new Vector<Integer>();
		
		boolean[] labelArray = new boolean[end-begin+1];
		for(int i=0; i<labelArray.length; i++)
		{
			labelArray[i] = false;
		}
		BioNERToken[] tokens = sentence.getTokens();
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<tokens.length; i++)
		{
			sb.append(tokens[i].getText());
			sb.append(" ");
		}
		String key = sb.toString();
		Vector<TypedDependency> tdVector = tdMap.get(key);
		if(tdVector==null)
		{
			System.out.println("cache not found!:"+key);
			System.out.println("Document ID:"+sentence.getDocument().getID());
		}
		for(TypedDependency td : tdVector)
		{
			if(!td.geType().equals("nn")) continue;
			if(td.getGovIndex()>=begin && td.getGovIndex()<=end
					&& td.getDepIndex()>=begin && td.getDepIndex()<=end)
			{
				labelArray[td.getGovIndex()-begin] = true;
			}
		}
		for(TypedDependency td : tdVector)
		{
			if(!td.geType().equals("nn")) continue;
			if(td.getGovIndex()>=begin && td.getGovIndex()<=end
					&& td.getDepIndex()>=begin && td.getDepIndex()<=end)
			{
				labelArray[td.getDepIndex()-begin] = false;
			}
		}
		for(int i=0; i<labelArray.length; i++)
		{
			if(labelArray[i])
				headerIndexVector.add(i+begin);
		}
		if(headerIndexVector.isEmpty()) headerIndexVector.add(end);
		return headerIndexVector;
	}
	
	public static void main(String[] args)
	{
		String text = "Here, we report the identification of Smurf2, a new member of the Hect family of E3 ubiquitin ligases.";
		BioNERSentence sentence = new BioNERSentence(text, 0);
		ChunkHeaderRecognizer recognizer = new ChunkHeaderRecognizer("../../BC2GN/parsed_data.txt");
		Vector<Integer> vector = recognizer.getChunkHeaders(sentence,17, 19);
		BioNERToken[] tokens = sentence.getTokens();
		for(Integer index : vector)
		{
			System.out.println(tokens[index].getText());
		}
	}
}

class TypedDependency
{
	private String m_type;
	private int m_govIndex;
	private int m_depIndex;
	public TypedDependency(String type, int govIndex, int depIndex)
	{
		setType(type);
		setGovIndex(govIndex);
		setDepIndex(depIndex);
	}
	public void setType(String m_type) {
		this.m_type = m_type;
	}
	public String geType() {
		return m_type;
	}
	public void setGovIndex(int m_govIndex) {
		this.m_govIndex = m_govIndex;
	}
	public int getGovIndex() {
		return m_govIndex;
	}
	public void setDepIndex(int m_depIndex) {
		this.m_depIndex = m_depIndex;
	}
	public int getDepIndex() {
		return m_depIndex;
	}
	
}