package bioner.process.orgnismnormal;

import bioner.data.document.BioNERDocument;
import bioner.process.BioNERProcess;

public class ProcessImpOrgnismNormalization implements BioNERProcess {

	OrgnismRecognizer m_orgnismRecognizer = OrgnismNormalProcessFactory.createOrgnismRecognizer();
	OrgnismBasedNormalizer m_orgnismBasedNormalizer = OrgnismNormalProcessFactory.createOrgnismBasedNormalizer();
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		OrgnismEntity[] orgEntities = m_orgnismRecognizer.recognizeOrgnisms(document);
		m_orgnismBasedNormalizer.normalizeNEREntity(document, orgEntities);
	}

}
