/////////////////////////////////////////////////////////////////////
//Usage: This is a interface providing a method to normalize a string. 
//		The word "normalize" here means that to replace some special names into a standard form.
//		Such as make the Roman numbers into normal number form etc.
//Author: Liu Jingchen
//Date: 2009/12/21
////////////////////////////////////////////////////////////////////
package bioner.tools.strnormal;

public interface StringNormalizer {
	public abstract String normalizeString(String str);
}
