/////////////////////////////////////////////////////////////
//Usage: This is a interface to build one feature for one token. The input is the whole token array and a int to indicate which token to build feature for.
//		This is because some kind of feature may need the whole sentence information.
//Author: Liu Jingchen
//Date: 2009/12/14
/////////////////////////////////////////////////////////////
package crf.featurebuild;

import bioner.data.document.BioNERSentence;

public interface TokenFeatureBuilder {
	public abstract String buildFeature(BioNERSentence sentence, int index);
}
