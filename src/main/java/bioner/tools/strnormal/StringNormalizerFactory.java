//////////////////////////////////////////////////////////////
//Usage: A factory class for StringNormalizer.
//Author: Liu Jingchen
//Data: 2009/12/21
//////////////////////////////////////////////////////////////
package bioner.tools.strnormal;

public class StringNormalizerFactory {
	static private StringNormalizer m_normalizer = null;
	static public StringNormalizer getStringNormalizer()
	{
		//Singleton pattern should be OK.
		if(m_normalizer==null)
		{
			m_normalizer = createStringNormalizer();
		}
		return m_normalizer;
	}
	static private StringNormalizer createStringNormalizer()
	{
		return new GeneralStringNormalizer();
	}
}
