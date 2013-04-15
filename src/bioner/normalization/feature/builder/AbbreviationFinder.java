package bioner.normalization.feature.builder;

/**
 * This code is originally from a paper: A Simple Algorithm for Identifying Abbreviation Definitions in Biomedical Text
 * But modified by me.
 * @author Liu Jingchen
 *
 */
public class AbbreviationFinder {
	
	public static String findFuzzyLongForm(String shortForm, String longForm)
	{
		int i = shortForm.length();
		for(; i>=2; i--)
		{
			String subShortForm = shortForm.substring(0,i);
			String resultStr = findBestLongForm(subShortForm, longForm);
			if(resultStr != null) return resultStr;
		}
		return null;
	}
	
	/** 
	* Method findBestLongForm takes as input a short-form and a long-  
	* form candidate (a list of words) and returns the best long-form 
	* that matches the short-form, or null if no match is found. 
	**/   
	public static String findBestLongForm(String shortForm, String longFormOri) { 
		
		
		int longFormEndPos = longFormOri.length();
		
		while(longFormEndPos > 0)
		{
			  String longForm = longFormOri.substring(0, longFormEndPos);
			  int sIndex;     // The index on the short form 
			  int lIndex;     // The index on the long form   
			  char currChar;  // The current character to match 
			 
			  boolean isLastWord = true;
			  int lastWordPos = -1;
			  sIndex = shortForm.length() - 1;  // Set sIndex at the end of the 
			                                // short form 
			  lIndex = longForm.length() - 1;   // Set lIndex at the end of the 
			                                // long form 
			  for ( ; sIndex >= 0; sIndex--) {  // Scan the short form starting  
			// from end to start 
			    // Store the next character to match. Ignore case 
			    currChar = Character.toLowerCase(shortForm.charAt(sIndex)); 
			    // ignore non alphanumeric characters 
			    if (!Character.isLetterOrDigit(currChar)) 
			      continue; 
			    // Decrease lIndex while current character in the long form 
			    // does not match the current character in the short form. 
			    // If the current character is the first character in the 
			    // short form, decrement lIndex until a matching character  
			    // is found at the beginning of a word in the long form. 
			    while ( 
					((lIndex >= 0) && 
					(Character.toLowerCase(longForm.charAt(lIndex)) != currChar)) 
					|| 
					      ((sIndex == 0) && (lIndex > 0) && 
					(Character.isLetterOrDigit(longForm.charAt(lIndex - 1))))) 
					          lIndex--; 
					    // If no match was found in the long form for the current 
					// character, return null (no match). 
					    if (lIndex < 0) 
					      return null; 
					    // A match was found for the current character. Move to the 
					    // next character in the long form. 
					    
					    if(isLastWord)
					    {
					    	isLastWord = false;
					    	lastWordPos = lIndex;
					    }
					    lIndex--;
					  } 
					  // Find the beginning of the first word (in case the first  
					  // character matches the beginning of a hyphenated word).  
					  lIndex = longForm.lastIndexOf(" ", lIndex) + 1; 
					  // Return the best long form, the substring of the original 
					// long form, starting from lIndex up to the end of the original  
					// long form. 
					  if(lastWordPos>=0)
					  {
						  int end = lastWordPos;
						  for(; end < longForm.length(); end++)
						  {
							  if(longForm.charAt(end)==' ') break;
						  }
						  String resultStr = longForm.substring(lIndex,end);
						  if(!resultStr.contains(shortForm) && !shortForm.contains(resultStr))
							 return resultStr; 
					  }
					  longFormEndPos = lIndex - 1;
					  
			}
		return null;
	}
	
	public static void main(String[] args)
	{
		String shortStr = "hspry4";
		String longStr = "which we designated human sprouty 4  based";
		String resultStr = findFuzzyLongForm(shortStr, longStr);
		System.out.println(resultStr);
	}
}
