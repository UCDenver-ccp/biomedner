package bioner.application.geniagm;

import bioner.application.api.BioNERApplication;
import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERDocumentOutput;
import bioner.application.api.BioNERProcessFactory;
import bioner.global.GlobalConfig;

public class GENIAGMTaskRun {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		BioNERDocumentBuilder docBuilder = new GENIAGMDocumentBuillder();
		BioNERProcessFactory processFacotry = new GENIAGMProcessFactory();
		BioNERDocumentOutput output = new GENIAGMDocumentOutput();
		BioNERApplication application = new BioNERApplication(docBuilder, output, processFacotry);
		application.run();
	}
	

}
