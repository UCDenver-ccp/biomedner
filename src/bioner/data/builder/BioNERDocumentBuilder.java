package bioner.data.builder;

import bioner.data.document.BioNERDocument;

public interface BioNERDocumentBuilder {
	public abstract BioNERDocument[] buildDocuments(String filepath);
}
