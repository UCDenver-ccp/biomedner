package bioner.application.webtool;

public class CreateDatabaseJSONTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DatabaseJSONTableReader reader = new DatabaseJSONTableReader();
		reader.connect();
		reader.createTable();
		reader.close();
	}

}
