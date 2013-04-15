package bioner.application.api;

import bioner.process.BioNERProcess;

public interface BioNERProcessFactory {
	public abstract BioNERProcess[] buildProcessPipeline();
}
