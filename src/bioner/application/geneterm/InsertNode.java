package bioner.application.geneterm;

public class InsertNode {
	private int m_pos = -1;
	private String m_text = null;
	public InsertNode()
	{
		
	}
	public InsertNode(int pos, String text)
	{
		m_pos = pos;
		m_text = text;
	}
	public void setPos(int m_pos) {
		this.m_pos = m_pos;
	}
	public int getPos() {
		return m_pos;
	}
	public void setText(String text) {
		this.m_text = text;
	}
	public String getText() {
		return m_text;
	}
	
}
