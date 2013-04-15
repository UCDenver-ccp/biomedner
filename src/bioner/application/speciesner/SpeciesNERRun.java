package bioner.application.speciesner;

import bioner.application.api.BioNERApplication;
import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERDocumentOutput;
import bioner.application.api.BioNERProcessFactory;

public class SpeciesNERRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BioNERDocumentBuilder docBuilder = new SpeciesNERDocumentBuilder();
		BioNERDocumentOutput output = new SpeciesNEROutput();
		BioNERProcessFactory processFactory = new SpeciesNERProcessFactory();
		BioNERApplication application = new BioNERApplication(docBuilder, output, processFactory);
		application.run();
	}

}
