package bioner.normalization.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import bioner.normalization.data.BioNERRecord;

public class MySQLDatabaseReader implements DatabaseReader {

	private Connection conn=null;
	private Statement stmt = null;
	private String m_tableName = null;

	public MySQLDatabaseReader()
	{
		try{
			//The new Instance() call is a work around for some
			//broken Java implementations
			Class.forName(DatabaseConfig.DATABASE_DRIVER_CLASS).newInstance();
		}catch(Exception ex){
			//handle the error
			ex.printStackTrace();
			System.err.println("MySQLDatabaseReader: ERROR, could not get the driver class. " + ex);
			throw new RuntimeException(ex);	
		}
		DatabaseConfig.ReadConfigFile();
		m_tableName = DatabaseConfig.DATABASE_GENEINFO_TABLE_NAME;
		
	}
	
	@Override
	public void connect()
	{
		try {
            System.err.println("attempting to connect to " 
                    + DatabaseConfig.DATABASE_HOST + " "
                    + DatabaseConfig.DATABASE_NAME + " "
                    + DatabaseConfig.DATABASE_USERNAME );
			conn = DriverManager.getConnection("jdbc:"+DatabaseConfig.DATABASE_DRIVER_NAME+"://"+DatabaseConfig.DATABASE_HOST+"/"+DatabaseConfig.DATABASE_NAME+"?"+
					"user="+DatabaseConfig.DATABASE_USERNAME+"&password="+DatabaseConfig.DATABASE_PASSWORD);
			stmt = conn.createStatement();
		} catch (SQLException e) {
			System.err.println("MySQLDatabaseReader: ERROR, could not connect to database. " + e);
			throw new RuntimeException(e);	
		}
	}
	
	@Override
	public void close()
	{
		try {
			conn.close();
		} catch (SQLException e) {
			System.err.println("MySQLDatabaseReader: ERROR, could not connect to database. " + e);
			throw new RuntimeException(e);	
		}
	}

	@Override
	public HashMap<String, BioNERRecord> searchRecords(String[] idArray) {
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
			//int i=0;
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
            System.err.println("MySQLDatabaseReader.searchRecords: ERROR " + e);
			e.printStackTrace();
            throw new RuntimeException(e);
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
