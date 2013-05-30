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
	
	public MySQLDatabaseBuilder() {
		try {
			//The new Instance() call is a work around for some
			//broken Java implementations
			Class.forName(DatabaseConfig.DATABASE_DRIVER_CLASS).newInstance();
		} 
		catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		DatabaseConfig.ReadConfigFile();
	}

	public void connect() {
		try {
			System.out.println("connecting to:" + DatabaseConfig.DATABASE_NAME + " " + DatabaseConfig.DATABASE_USERNAME);
			System.out.println("will be inserting into:" + DatabaseConfig.DATABASE_GENEINFO_TABLE_NAME);
			conn = DriverManager.getConnection("jdbc:"+DatabaseConfig.DATABASE_DRIVER_NAME+"://"+DatabaseConfig.DATABASE_HOST+"/"+DatabaseConfig.DATABASE_NAME+"?"+
												"user="+DatabaseConfig.DATABASE_USERNAME+"&password="+DatabaseConfig.DATABASE_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void close()
	{
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
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
				System.out.println("INFO: Table doesn't exist! Create new table");
			}
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	public void importData(String filename)
	{

		final boolean batch=true;
		final int batchSize = 1000;

		String tableName = DatabaseConfig.DATABASE_GENEINFO_TABLE_NAME;
		StringBuilder sql = null;
		try {
			BufferedReader freader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			String line;
			int num=0;
			int bogusNum=0;
			Statement stmt = conn.createStatement();
			sql = new StringBuilder("INSERT INTO "+tableName + " VALUES ");
			while ((line=freader.readLine()) != null) {
				if (line.length()<=0 || line.startsWith("#"))  {
					continue;
				}
				num++;
				
				line = line.replaceAll("\\\\", "\\\\\\\\");
				line = line.replaceAll("\\'", "\\\\'");
				String[] parts = line.split("\\t+");
				sql.append("(");


				for (int i=0; i<parts.length; i++) {
					if (i>0) {
						sql.append(",");
					}
					if (!parts[i].equals("-")) {
						//sql.append("'"+parts[i]+"'");
						// http://stackoverflow.com/questions/12316953/insert-varchar-with-single-quotes-in-postgresql
						sql.append("E'"+parts[i]+"'");
					}
					else {
						sql.append("null");
					}
				}


				// BATCHES
				if (num % batchSize==0) {
					System.err.println("Adding #" + num + " Records");
				}	

				try {
					if (batch) {	
						sql.append("),");
						if (num % batchSize==0) {
							if (sql.charAt(sql.length()-1)==',') {
								sql.deleteCharAt(sql.length()-1);
							}
							stmt.execute(sql.toString());
							sql = new StringBuilder("INSERT INTO "+tableName + " VALUES ");
						}
					}
					else {
						sql.append(");");
						stmt.execute(sql.toString());
						sql = new StringBuilder("INSERT INTO "+tableName + " VALUES ");
					}
				}
				catch (Exception x) {
					bogusNum++;
					System.err.println("\nERROR:" + x);
					System.err.println("did not insert:" + bogusNum + "/" +  num + " "  + sql);
					System.err.println("...continuing to process remaineder");
					sql = new StringBuilder("INSERT INTO "+tableName + " VALUES ");
				}
			}
			
	
			if (batch) {
				if (sql.charAt(sql.length()-1)==',') {
					sql.deleteCharAt(sql.length()-1);
				}
				try {
					stmt.execute(sql.toString());
				}
				catch (Exception x) {
					bogusNum++;
					System.err.println("\nERROR:" + x);
					System.err.println("did not insert:" + bogusNum + "/" +  num + " "  + sql);
					System.err.println("...continuing to finish process despite errors.");
				}
			}

			System.out.println("Done Adding #"+num+" Record");
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		catch (SQLException e) {
			System.out.println("\nERROR with STATEMENT: " + sql.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
/***
catch (Exception e) {
			System.out.println("\n ERROR with STATEMENT: " + sql.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}	
***/
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
