package bioner.process.orgnismnormal;

import bioner.data.document.BioNERDocument;

public interface OrgnismRecognizer {
	public abstract OrgnismEntity[] recognizeOrgnisms(BioNERDocument doc);
}
