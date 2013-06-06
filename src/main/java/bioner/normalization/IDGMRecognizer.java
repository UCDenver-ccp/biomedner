package bioner.normalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.application.bc3gn.BC3GNDataFileReader;
import bioner.data.document.BioNERDocument;
import bioner.global.GlobalConfig;
import bioner.normalization.feature.builder.NCBIRankFinder;

public class IDGMRecognizer {
	private static BioNERDocument m_currentDocument=null;
	private static Vector<String> m_currentIDVector = null;
	private static Pattern m_pattern = Pattern.compile("\\b([A-Z]{2,}\\_?[0-9]{3,})|([PQ][0-9A-Z]{5,5})\\b");
	private static NCBIRankFinder m_ncbiRank = new NCBIRankFinder();
	public static Vector<String> getIDVector(BioNERDocument document)
	{
		if(m_currentDocument==document)
		{
			return m_currentIDVector;
		}
		m_currentDocument = document;
		m_currentIDVector = new Vector<String>();
		String dataStr = getNXMLStr(document.getID());
		Matcher matcher = m_pattern.matcher(dataStr);
		Vector<String> gmVector = new Vector<String>();
		while(matcher.find())
		{
			int begin = matcher.start();
			int end = matcher.end();
			String gmText = dataStr.substring(begin, end);
			if(gmVector.contains(gmText)) continue;
			gmVector.add(gmText);
			String[] ids = m_ncbiRank.getRank(gmText);
			if(ids.length==1)
			{
				if(!m_currentIDVector.contains(ids[0]))
				{
					m_currentIDVector.add(ids[0]);
				}
			}
		}
		
		return m_currentIDVector;
	}
	
	private static String getNXMLStr(String pmcid)
	{
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(GlobalConfig.BC3GN_DATADIR+pmcid+".nxml"));
			String line;
			while((line=freader.readLine())!=null)
			{
				sb.append(line);
				sb.append(" ");
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
		GlobalConfig.BC3GN_DATADIR = "../../BC3GN/50_data/";
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader(GlobalConfig.BC3GN_DATADIR);
		File[] files = (new File(GlobalConfig.BC3GN_DATADIR)).listFiles();
		for(File file : files)
		{
			System.out.println("PMCID:"+file.getName());
			BioNERDocument document = docBuilder.getOneDocument(file);
			Vector<String> idVector = getIDVector(document);
			for(String id : idVector)
			{
				System.out.println(id);
			}
		}
	}
}
