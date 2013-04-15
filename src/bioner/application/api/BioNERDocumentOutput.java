package bioner.application.api;

import bioner.data.document.BioNERDocument;

public interface BioNERDocumentOutput {
	public abstract void init();
	public abstract void close();
	public abstract void outputDocument(BioNERDocument document);
}
