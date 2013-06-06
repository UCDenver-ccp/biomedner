package bioner.tools;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;

/**
 * This is the interface for NER.
 * @author Liu Jingchen
 *
 */
public interface NERProcessor {
	public abstract BioNEREntity[] recognizeSentence(BioNERSentence sentence); 
}
