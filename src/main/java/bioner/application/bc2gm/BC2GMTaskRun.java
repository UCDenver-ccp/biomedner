package bioner.application.bc2gm;

import bioner.application.api.BioNERApplication;
import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.api.BioNERDocumentOutput;
import bioner.application.api.BioNERProcessFactory;
import bioner.global.GlobalConfig;

public class BC2GMTaskRun {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		BioNERDocumentBuilder docBuilder = new BC2GMDocumentBuillder();
		BioNERProcessFactory processFacotry = new BC2GMProcessFactory();
		BioNERDocumentOutput output = new BC2GMDocumentOutput();
		BioNERApplication application = new BioNERApplication(docBuilder, output, processFacotry);
		application.run();
	}
	

}
