package bioner.process.orgnismnormal;

import bioner.data.document.BioNERDocument;

public interface OrgnismBasedNormalizer {
	public abstract void normalizeNEREntity(BioNERDocument doc, OrgnismEntity[] orgnismEntities);
}
