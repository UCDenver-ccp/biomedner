package bioner.tools.enju;

/**
 * Store one word in the Enju parse results.
 * @author Jingchen Liu
 *
 */
public class EnjuWord {
	private String m_text;
	private String m_postag;
	private int m_index;
	public EnjuWord(String text, String postag, int index)
	{
		m_text = text;
		m_postag = postag;
		m_index = index;
	}
	
	public void setText(String m_text) {
		this.m_text = m_text;
	}
	public String getText() {
		return m_text;
	}
	public void setPostag(String m_postag) {
		this.m_postag = m_postag;
	}
	public String getPostag() {
		return m_postag;
	}
	public void setIndex(int m_index) {
		this.m_index = m_index;
	}
	public int getIndex() {
		return m_index;
	}
	
	public String toString()
	{
		return m_text+"\t"+m_postag+"\t"+m_index;
	}
	public boolean equals(EnjuWord otherWord)
	{
		if(m_text.equals(otherWord.getText()) && m_postag.equals(otherWord.getPostag()) && m_index==otherWord.getIndex())
			return true;
		return false;
	}
	public boolean equals(String text, int index)
	{
		if((m_text.equals(text) || m_text.contains(text) || text.contains(m_text))
				&& Math.abs(m_index-index)<=5) return true;
		return false;
	}
}
