package bioner.normalization.data.database;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.GZIPInputStream;

public class MySQLDatabaseBuilder {
	
	private Connection conn=null;
	
	public MySQLDatabaseBuilder()
	{
		try{
			//The new Instance() call is a work around for some
			//broken Java implementations
			Class.forName(DatabaseConfig.DATABASE_DRIVER_CLASS).newInstance();
		}catch(Exception ex){
			//handle the error
			ex.printStackTrace();
		}
		DatabaseConfig.ReadConfigFile();
	}
	public void connect()
	{
		try {
			conn = DriverManager.getConnection("jdbc:"+DatabaseConfig.DATABASE_DRIVER_NAME+"://"+DatabaseConfig.DATABASE_HOST+"/"+DatabaseConfig.DATABASE_NAME+"?"+
												"user="+DatabaseConfig.DATABASE_USERNAME+"&password="+DatabaseConfig.DATABASE_PASSWORD);
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
	
	public void createTable()
	{
		String tableName = DatabaseConfig.DATABASE_GENEINFO_TABLE_NAME;
		String sql = "CREATE TABLE "+tableName+" ( "+
			"tax_id VARCHAR(20), "+
			"GeneID VARCHAR(20), "+
			"Symbol TEXT, "+
			"LocusTag TEXT, "+
			"Synonyms TEXT, "+
			"dbXrefs TEXT, "+
			"chromosome TEXT, "+
			"map_location TEXT, "+
			"description TEXT, "+
			"type_of_gene TEXT, "+
			"Symbol_from_nomenclature_authority TEXT, "+
			"Full_name_from_nomenclature_authority TEXT, "+
			"Nomenclature_status TEXT, "+
			"Other_designations TEXT, "+
			"Modification_date VARCHAR(20), "+
			"Primary key (GeneID))";
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
	public void importData(String filename)
	{
		String tableName = DatabaseConfig.DATABASE_GENEINFO_TABLE_NAME;
		try {
			BufferedReader freader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			String line;
			int num=0;
			Statement stmt = conn.createStatement();
			StringBuffer sql = new StringBuffer("INSERT INTO "+tableName + " VALUES ");
			while((line=freader.readLine()) != null)
			{
				if(line.length()<=0 || line.startsWith("#")) continue;
				num++;
				
				line = line.replaceAll("\\\\", "\\\\\\\\");
				line = line.replaceAll("\\'", "\\\\'");
				String[] parts = line.split("\\t+");
				sql.append("(");
				for(int i=0; i<parts.length; i++)
				{
					if(i>0) sql.append(",");
					if(!parts[i].equals("-"))
					{
						sql.append("'"+parts[i]+"'");
					}
					else
					{
						sql.append("null");
					}
				}
				sql.append("),");
				if(num%1000==0) 
				{
					System.out.println("Adding #"+num+" Record");
					
					if(sql.charAt(sql.length()-1)==',')
					{
						sql.deleteCharAt(sql.length()-1);
					}
					stmt.execute(sql.toString());
					sql = new StringBuffer("INSERT INTO "+tableName + " VALUES ");
				}
				
			}
			System.out.println("Adding #"+num+" Record");
			
			if(sql.charAt(sql.length()-1)==',')
			{
				sql.deleteCharAt(sql.length()-1);
			}
			stmt.execute(sql.toString());
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MySQLDatabaseBuilder builder = new MySQLDatabaseBuilder();
		builder.connect();
		builder.createTable();
		builder.importData(args[0]);
		builder.close();
	}
}
