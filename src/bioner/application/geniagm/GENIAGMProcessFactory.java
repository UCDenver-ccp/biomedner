package bioner.application.geniagm;

import bioner.application.api.BioNERProcessFactory;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpGRMMLineCRF;
import bioner.process.common.ProcessImpEntityFilter;
import bioner.process.common.ProcessImpGetEntityFromLabel;
import bioner.process.knowledgebase.ProcessImpDiseaseNER;
import bioner.process.knowledgebase.ProcessImpDrugNER;
import bioner.process.organismner.ProcessImpOrgnismNER;
import bioner.process.postprocess.ProcessImpPostProcessEntityFilter;
import bioner.process.postprocess.ProcessImpRightBoundAdjust;
import bioner.process.preprocess.ProcessImpPreprocess;
import bioner.process.proteinner.ProcessImpProteinNER;
import bioner.process.common.ProcessImpSetLable;

public class GENIAGMProcessFactory implements BioNERProcessFactory{

	@Override
	public BioNERProcess[] buildProcessPipeline() {
		// TODO Auto-generated method stub
		
		BioNERProcess[] pipeline = new BioNERProcess[11];
		pipeline[0] = new ProcessImpPreprocess();
		pipeline[1] = new ProcessImpProteinNER();
		pipeline[2] = new ProcessImpDrugNER();
		pipeline[3] = new ProcessImpDiseaseNER();
		pipeline[4] = new ProcessImpEntityFilter();
		pipeline[5] = new ProcessImpOrgnismNER();
		pipeline[6] = new ProcessImpSetLable();
		//pipeline[7] = new ProcessImpSetLabelByCRF();
		pipeline[7] = new ProcessImpGRMMLineCRF();
		pipeline[8] = new ProcessImpRightBoundAdjust();
		pipeline[9] = new ProcessImpGetEntityFromLabel();
		pipeline[10] = new ProcessImpPostProcessEntityFilter();
		return pipeline;
	}
}
