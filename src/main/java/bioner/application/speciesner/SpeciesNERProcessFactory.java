package bioner.application.speciesner;

import bioner.application.api.BioNERProcessFactory;
import bioner.process.BioNERProcess;
import bioner.process.common.ProcessImpEntityFilter;
import bioner.process.common.ProcessImpGetEntityFromLabel;
import bioner.process.organismner.ProcessImpOrgnismNER;
import bioner.process.postprocess.ProcessImpPostProcessEntityFilter;
import bioner.process.postprocess.ProcessImpRightBoundAdjust;
import bioner.process.preprocess.ProcessImpPreprocess;
import bioner.process.common.ProcessImpSetLable;

public class SpeciesNERProcessFactory implements BioNERProcessFactory {

	@Override
	public BioNERProcess[] buildProcessPipeline() {
		// TODO Auto-generated method stub
		BioNERProcess[] pipeline = new BioNERProcess[3];
		pipeline[0] = new ProcessImpPreprocess();
		pipeline[1] = new ProcessImpOrgnismNER();
		pipeline[2] = new ProcessImpAddSpeciesInProteinName();
		
		return pipeline;
	}

}
