package bioner.application.webtool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import bioner.normalization.data.BioNERRecord;
import bioner.normalization.data.database.DatabaseConfig;
import bioner.normalization.data.database.MySQLDatabaseReader;

public class DatabaseJSONTableReader{

	private Connection conn=null;
	private Statement stmt = null;
	private String m_tableName = null;
	public DatabaseJSONTableReader()
	{
		try{
			//The new Instance() call is a work around for some
			//broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}catch(Exception ex){
			//handle the error
			ex.printStackTrace();
		}
		DatabaseConfig.ReadConfigFile();
		m_tableName = DatabaseConfig.DATABASE_JSON_TABLE_NAME;
	}
	public void createTable()
	{
		String tableName = m_tableName;
		String sql = "CREATE TABLE "+tableName+" ( "+
			"PMCID VARCHAR(20), "+
			"JSON TEXT, "+
			"Primary key (PMCID))";
		try {
			Statement stmt = conn.createStatement();
			try{
			stmt.execute("drop table "+tableName);
			}catch(SQLException e)
			{
				System.out.println("Table doesn't exist! Create new table");
			}
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void connect()
	{
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+DatabaseConfig.DATABASE_HOST+"/"+DatabaseConfig.DATABASE_NAME+"?"+
					"user="+DatabaseConfig.DATABASE_USERNAME+"&password="+DatabaseConfig.DATABASE_PASSWORD);
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void close()
	{
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String[] getAllPMCID()
	{
		StringBuffer queryBuffer = new StringBuffer("SELECT PMCID FROM ");
		queryBuffer.append(m_tableName);
		Vector<String> idVector = new Vector<String>();
		try {
			ResultSet rs = stmt.executeQuery(queryBuffer.toString());
			while(rs.next())
			{
				String id = rs.getString(1);
				idVector.add(id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] array = new String[idVector.size()];
		for(int i=0; i<array.length; i++)
		{
			array[i] = idVector.elementAt(i);
		}
		return array;
	}
	public String serachRecord(String pmcid)
	{
		StringBuffer queryBuffer = new StringBuffer("SELECT * FROM ");
		queryBuffer.append(m_tableName);
		queryBuffer.append(" WHERE ");
		queryBuffer.append("PMCID='"+pmcid+"'");
		
		try {
			ResultSet rs = stmt.executeQuery(queryBuffer.toString());
			if(rs.next())
			{
				String jsonStr = rs.getString(2);
				return jsonStr;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void insertReader(String pmcid, String jsonStr)
	{
		jsonStr = jsonStr.replaceAll("'", "\\\\'");
		StringBuffer sql = new StringBuffer("INSERT INTO "+m_tableName + " VALUE ");
		//jsonStr = jsonStr.replaceAll("\\\"", "\\\\\"");
		sql.append("('"+pmcid+"','"+jsonStr+"')");
		try {
			stmt.execute(sql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateRecord(String pmcid, String jsonStr)
	{
		jsonStr = jsonStr.replaceAll("'", "\\\\'");
		StringBuffer sql = new StringBuffer("UPDATE "+m_tableName + " SET JSON='"+jsonStr+"'");
		
		sql.append(" WHERE PMCID='"+pmcid+"'");
		try {
			stmt.execute(sql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public HashMap<String, BioNERRecord> searchRecords(String[] idArray) {
		// TODO Auto-generated method stub
		if(idArray.length==0) return null;
		StringBuffer queryBuffer = new StringBuffer("SELECT * FROM ");
		queryBuffer.append(m_tableName);
		queryBuffer.append(" WHERE ");
		for(int i=0; i<idArray.length; i++)
		{
			if(i>0)
			{
				queryBuffer.append("OR ");
			}
			queryBuffer.append("GeneID='"+idArray[i]+"' ");
		}
		
		HashMap<String, BioNERRecord> recordTable = new HashMap<String, BioNERRecord>();
		try {
			ResultSet rs = stmt.executeQuery(queryBuffer.toString());
			while(rs.next())
			{
				BioNERRecord record = new BioNERRecord();
				record.setID(rs.getString(2));
				record.setSpeciesID(rs.getString(1));
				record.setSymbol(rs.getString(3));
				
				record.setAttribute("dbXrefs", rs.getString(6));
				record.setAttribute("chromosome", rs.getString(7));
				record.setAttribute("map_location", rs.getString(8));
				record.setAttribute("description", rs.getString(9));
				record.setAttribute("type_of_gene", rs.getString(10));
				record.setAttribute("Nomenclature_status", rs.getString(13));
				record.setAttribute("Other_designations", rs.getString(14));
				
				Vector<String> synonymVector = new Vector<String>();
				addSynonyms(rs.getString(3), synonymVector);
				addSynonyms(rs.getString(4), synonymVector);
				addSynonyms(rs.getString(5), synonymVector);
				addSynonyms(rs.getString(11), synonymVector);
				addSynonyms(rs.getString(12), synonymVector);
				addSynonyms(rs.getString(14), synonymVector);
				record.setSynonyms(synonymVector);
				
				recordTable.put(record.getID(), record);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recordTable;
	}
	private void addSynonyms(String dataStr, Vector<String> synonymVector)
	{
		if(dataStr==null) return;
		String[] parts = dataStr.split("\\|");
		for(int i=0; i<parts.length; i++)
		{
			if(parts[i].length()>0 && !synonymVector.contains(parts[i]))
			{
				synonymVector.add(parts[i]);
			}
		}
	}

	public static void main(String[] args)
	{
		MySQLDatabaseReader reader = new MySQLDatabaseReader();
		reader.connect();
		String[] ids = new String[]{"6382184","1246500","5961934"};
		HashMap<String, BioNERRecord> recordTable = reader.searchRecords(ids);
		reader.close();
		for(BioNERRecord record : recordTable.values())
		{
			System.out.print(record.getID()+" "+record.getSymbol()+" ");
			if(record.getSynonyms() != null)
			{
				for(String synonym : record.getSynonyms())
				{
					System.out.print(synonym+" ");
				}
			}
			System.out.println();
		}
	}
}
