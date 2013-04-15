package bioner.application.api;

import bioner.data.document.BioNERDocument;

public interface BioNERDocumentBuilder {
	public abstract BioNERDocument[] buildDocuments();
}
