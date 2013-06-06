package bioner.application.geneterm;

import bioner.application.api.BioNERProcessFactory;
import bioner.process.BioNERProcess;
import bioner.process.common.ProcessImpEntityFilter;
import bioner.process.preprocess.ProcessImpPreprocess;

public class GTProcessFactory implements BioNERProcessFactory {

	@Override
	public BioNERProcess[] buildProcessPipeline() {
		// TODO Auto-generated method stub
		BioNERProcess[] pipeline = new BioNERProcess[4];
		pipeline[0] = new ProcessImpPreprocess();
		
		pipeline[1] = new ProcessImpHumanGeneDictNER();
		pipeline[2] = new ProcessImpTermNER();
		pipeline[3] = new ProcessImpEntityFilter();
		return pipeline;
	}

}
