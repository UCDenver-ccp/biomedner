package bioner.application.webtool;

import java.util.Vector;
import java.util.regex.Pattern;

import bioner.normalization.data.BioNERRecord;
import bioner.normalization.data.database.MySQLDatabaseReader;

public class JSONItem {
	private String m_pmcid;
	private Vector<String> m_elementVector = new Vector<String>();
	private SpeciesDatabaseReader m_speciesReader;
	private MySQLDatabaseReader m_geneReader;
	public JSONItem(String pmcid, SpeciesDatabaseReader speciesReader, MySQLDatabaseReader geneReader)
	{
		m_pmcid = pmcid;
		m_speciesReader = speciesReader;
		m_geneReader = geneReader;
	}
	
	private static Pattern pattern = Pattern.compile("\\{.*\\}");
	public void readJSONString(String str)
	{
		int pos = str.indexOf('{');
		while(pos>=0)
		{
			int end = str.indexOf("\"score\":", pos);
			end = str.indexOf('}', end);
			end++;
			String oneElementStr = str.substring(pos, end);
			m_elementVector.add(oneElementStr);
			pos = str.indexOf('{', end);
		}
	}
	public String toString()
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i=0; i<m_elementVector.size(); i++)
		{
			String elementStr = m_elementVector.elementAt(i);
			if(i!=0) sb.append(",");
			sb.append(elementStr);
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String getGeneID(String elementStr)
	{
		int begin = elementStr.indexOf("annotation_id")+"annotation_id\":\"".length();
		int end = elementStr.indexOf('"',begin);
		String id = elementStr.substring(begin, end);
		return id;
	}
	private String getSpeciesID(String geneID)
	{
		BioNERRecord record = m_geneReader.searchRecords(new String[]{geneID}).get(geneID);
		return record.getSpeciesID();
	}
	
	public void addSpecies()
	{
		for(int i=0; i<m_elementVector.size(); i++)
		{
			String elementStr = m_elementVector.elementAt(i);
			if(elementStr.contains("\"species_name\"")) continue;
			String geneID = getGeneID(elementStr);
			String speciesID = getSpeciesID(geneID);
			String speciesName = m_speciesReader.getScientificName(speciesID);
			String insertStr = ",\"species_id\":\""+speciesID+"\",\"species_name\":\""+speciesName+"\"";
			StringBuffer sb = new StringBuffer(elementStr);
			int pos = sb.indexOf(",\"score\":");
			sb.insert(pos, insertStr);
			m_elementVector.set(i, sb.toString());
		}
	}
	
	public static void main(String[] args)
	{
		SpeciesDatabaseReader speciesReader = new SpeciesDatabaseReader();
		MySQLDatabaseReader geneReader = new MySQLDatabaseReader();
		speciesReader.connect();
		geneReader.connect();
		
		JSONItem item = new JSONItem("0001", speciesReader, geneReader);
		item.readJSONString("[{\"annotation_type_cd\":\"gene\",\"annotation_id\":\"217369\",\"annotation_data\":\"UTR\",\"annotation_text\":\"Uts2r\",\"score\":\"0.4093215700034886\"},{\"annotation_type_cd\":\"gene\",\"annotation_id\":\"217369\",\"annotation_data\":\"UTR\",\"annotation_text\":\"Uts2r\",\"score\":\"0.4093215700034886\"}]");
		item.addSpecies();
		System.out.println(item.toString());
		
		
		speciesReader.close();
		geneReader.close();
	}
	
}
