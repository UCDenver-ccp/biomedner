/////////////////////////////////////////////////////
//Usage: This interface describes one process in the main work flow. 
//		Every process step should implement this interface, such as the NER step, the normalization, etc.
//		The input is one BioNERDocument object. Its data inside should be changed during the Process method.
//Author:Liu Jingchen
//Date:2009/12/2
//////////////////////////////////////////////////////

package bioner.process;

import bioner.data.document.BioNERDocument;

public interface BioNERProcess {
	
	public abstract void Process(BioNERDocument document);
}
