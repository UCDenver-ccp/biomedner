package bioner.normalization.feature.builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.normalization.data.BioNERCandidate;

public class NCBIRankFinder {
	private static HashMap<String, String[]> m_rankTable = new HashMap<String, String[]>();
	private String m_cacheFileName = "../../BC2GN/ncbi_rank_cache.txt";
	
	public NCBIRankFinder()
	{
		if (m_rankTable.isEmpty())
			readCacheFile(m_cacheFileName);
	}
	private void readCacheFile(String filename)
	{
		File checkFile = new File(filename);
		if(!checkFile.exists()) return;
		System.out.println("Reading cached NCBI rank....");
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int num = 0;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\|");
				if(parts.length>0)
				{
					num++;
					if(num%1000==0) System.out.println("Reading #"+num+" rank");
					String[] ids = new String[parts.length-1];
					for(int i=1; i<parts.length; i++)
					{
						ids[i-1] = parts[i];
					}
					m_rankTable.put(parts[0], ids);
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
		System.out.println("Finished!");
	}
	public BioNERCandidate[] getCandidates(String geneStr)
	{
		String[] geneIDArray = getRank(geneStr);
		BioNERCandidate[] candidates = new BioNERCandidate[geneIDArray.length];
		for(int i=0; i<geneIDArray.length; i++)
		{
			candidates[i] = new BioNERCandidate();
			candidates[i].setRecordID(geneIDArray[i]);
		}
		return candidates;
	}
	public String[] getRank(String geneStr)
	{
		String[] rankArray = m_rankTable.get(geneStr);
		
		if(rankArray != null) return rankArray;
		
		String encodedGeneStr = URLEncoder.encode(geneStr);
		boolean tryAgain=true;
		while(tryAgain)
		{
		try {
			
			URL url = new URL("http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term="+encodedGeneStr+"&retmax=100");
			StringBuffer sb = new StringBuffer();
			BufferedReader strReader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			if(!strReader.ready()) throw new IOException();
			while((line=strReader.readLine())!=null)
			{
				sb.append(line);
				sb.append("\n");
			}
			String retStr = sb.toString();
			Pattern pattern = Pattern.compile("<Id>[0-9]+</Id>");
			Matcher matcher = pattern.matcher(retStr);
			Vector<String> idVector = new Vector<String>();
			while(matcher.find())
			{
				String idStr = retStr.substring(matcher.start()+4, matcher.end()-5);
				idVector.add(idStr);
			}
			
			int size = idVector.size();
			rankArray = new String[size];
			for(int i=0; i<size; i++)
			{
				rankArray[i] = idVector.elementAt(i);
			}
			m_rankTable.put(geneStr, rankArray);
			
			
			BufferedWriter fwriter = new BufferedWriter(new FileWriter(m_cacheFileName,true));
			fwriter.write(geneStr);
			for(int i=0; i<rankArray.length; i++)
			{
				fwriter.write("|");
				fwriter.write(rankArray[i]);
			}
			fwriter.newLine();
			fwriter.close();
			tryAgain=false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				Thread.sleep(334);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Try again!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				Thread.sleep(334);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Try again!");
		}
		}
		return rankArray;
	}
	
	public static void main(String[] args)
	{
		NCBIRankFinder ncbiRank = new NCBIRankFinder();
		String[] ids = ncbiRank.getRank("RERE");
		for(int i=0; i<ids.length; i++)
		{
			System.out.println(ids[i]);
		}
	}
}
