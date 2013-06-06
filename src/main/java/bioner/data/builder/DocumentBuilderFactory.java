////////////////////////////////////////////////////
//Usage: Factory class for all kinds of DocumentBuilders
//Author: Liu Jingchen
//Date: 2009/12/2
////////////////////////////////////////////////////
package bioner.data.builder;

public class DocumentBuilderFactory {
	
	static public BioNERDocumentBuilder createDocumentBuilder()
	{
		//return new GENIADocumentBuilder();
		return new BC2GeneMentionDocumentBuillder();
	}
}
