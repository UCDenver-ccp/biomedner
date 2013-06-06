package bioner.application.webtool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bioner.normalization.data.database.DatabaseConfig;

public class SpeciesDatabaseReader {
	private Connection conn=null;
	private Statement stmt = null;
	private String m_tableName = null;
	public SpeciesDatabaseReader()
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
		m_tableName = DatabaseConfig.DATABASE_SPECIES_TABLE_NAME;
	}
	
	public void createTable()
	{
		String tableName = m_tableName;
		String sql = "CREATE TABLE "+tableName+" ( "+
			"ID VARCHAR(20), "+
			"SCIENTIFIC_NAME TEXT, "+
			"Primary key (ID))";
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
	public String getScientificName(String id)
	{
		StringBuffer queryBuffer = new StringBuffer("SELECT * FROM ");
		queryBuffer.append(m_tableName);
		queryBuffer.append(" WHERE ");
		queryBuffer.append("ID='"+id+"'");
		
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
	public void insertSpecies(String id, String scientificName)
	{
		scientificName = scientificName.replaceAll("\\'", "\\\\'");
		StringBuffer sql = new StringBuffer("INSERT INTO "+m_tableName + " VALUE ");
		//jsonStr = jsonStr.replaceAll("\\\"", "\\\\\"");
		sql.append("('"+id+"','"+scientificName+"')");
		try {
			stmt.execute(sql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void readSpeciesFile(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			int lineNum=0;
			while((line=freader.readLine())!=null)
			{
				lineNum++;
				if(lineNum%1000==0) System.out.println("Processing line "+lineNum);
				if(line.contains("scientific name"))
				{
					String[] parts = line.split("[\\t\\|]+");
					insertSpecies(parts[0], parts[1]);
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
	}
	public static void main(String[] args)
	{
		SpeciesDatabaseReader reader = new SpeciesDatabaseReader();
		reader.connect();
		reader.createTable();
		reader.readSpeciesFile("/home/ljc/EntrezGene/species/names.dmp");
		String name = reader.getScientificName("9606");
		System.out.println(name);
		reader.close();
	}
}
