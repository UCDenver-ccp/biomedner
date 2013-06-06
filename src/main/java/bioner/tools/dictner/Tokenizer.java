///////////////////////////////////////////////////
//Usage: Interface to tokenize. Input:One BioNERSentence object. Output: a array of BioNERToken objects.
//Author: Liu Jingchen
//Date:2009/12/2
///////////////////////////////////////////////////
package bioner.tools.dictner;

import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

public interface Tokenizer {
	public abstract BioNERToken[] Tokenize(BioNERSentence sentence);
}
