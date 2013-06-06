/////////////////////////////////////////////////
//Usage: This is a interface for sentence based NER. Input: A array of BioNERToken  Output: A array of BioNEREntity
//Author: Liu Jingchen
//Date:2009/12/2
/////////////////////////////////////////////////
package bioner.tools.dictner;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

public interface SentenceNER {
	public abstract BioNEREntity[] SentenceBasedNER(BioNERSentence sentence);
}
