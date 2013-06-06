package bioner.normalization.data.database;

public class DatabaseReaderFactory {
	public static DatabaseReader createDatabaseReader()
	{
		return new MySQLDatabaseReader();
	}
}
