package bioner.application.geneterm;

import bioner.application.api.BioNERApplication;
import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERDocumentOutput;
import bioner.application.api.BioNERProcessFactory;

import bioner.global.GlobalConfig;

public class GTRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		BioNERDocumentBuilder docBuilder = new GTDocumentBuilder();
		BioNERProcessFactory processFacotry = new GTProcessFactory();
		BioNERDocumentOutput output = new GTOutput();
		BioNERApplication application = new BioNERApplication(docBuilder, output, processFacotry);
		application.run();
	}

}
