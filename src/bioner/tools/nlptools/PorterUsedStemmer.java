package bioner.tools.nlptools;

public class PorterUsedStemmer implements Stemmer {
	private PorterStemmer m_porterStemmer = new PorterStemmer();
	@Override
	public String stem(String word) {
		// TODO Auto-generated method stub
		for(int i=0; i<word.length(); i++)
		{
			m_porterStemmer.add(word.charAt(i));
		}
		m_porterStemmer.stem();
		String stem = m_porterStemmer.toString();
		
		
		return stem;
	}

}
