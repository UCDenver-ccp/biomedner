package bioner.application.speciesner.statistics;

public class ProteinSpeciesStatisticsRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ProteinSpeciesNumStatistics statistics = new ProteinSpeciesNumStatistics();
		statistics.buildProteinSpeciesTable();
		statistics.writeUniqProteinToFile("../../Species_NER/unique_protein.txt");
		statistics.countUniqueProtein();
	}

}
