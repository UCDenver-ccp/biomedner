//////////////////////////////////////////////
//Usage: This is a interface to normalize one token, including make the token lower case, convert Greek letter etc.
//		The result is stored in the BioNERToken object.
//Author: Liu Jingchen
//Date: 2009/12/2
//////////////////////////////////////////////
package bioner.tools.dictner;

import bioner.data.document.BioNERToken;

public interface TokenNormalize {
	public abstract void NormalizeToken(BioNERToken[] tokens);
}
