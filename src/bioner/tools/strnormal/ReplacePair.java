//////////////////////////////////////////////////
//Usage: This is a class to store one replace rule in token normalization.
//		It store one string to be replace and another string to be used instead.
//Author: Liu Jingchen
//Date: 2009/12/7
//////////////////////////////////////////////////
package bioner.tools.strnormal;

public class ReplacePair {
	private String m_oriStr;
	private String m_newStr;
	
	public ReplacePair()
	{
		m_oriStr = "";
		m_newStr = "";
	}
	public ReplacePair(String oriStr, String newStr)
	{
		m_oriStr = oriStr;
		m_newStr = newStr;
		//checkOriStr();
	}
	public void ReadStrRule(String rule)
	{
		rule = rule.trim();
		String[] parts = rule.split("\\|");
		if(parts.length!=2)
		{
			Exception e = new Exception();
			e.printStackTrace();
			return;
		}
		m_oriStr = parts[0].toLowerCase().trim();
		m_newStr = parts[1].toLowerCase().trim();
		if(m_newStr.equals("<space>")) m_newStr = " ";
		//checkOriStr();
	}
	
	//To make sure the regular expression is like "\b....\b" 
	private void checkOriStr()
	{
		if(!m_oriStr.startsWith("\\b"))
		{
			m_oriStr = "\\b"+m_oriStr;
		}
		if(!m_oriStr.endsWith("\\b"))
		{
			m_oriStr = m_oriStr + "\\b";
		}
	}
	public void setOrignalriStr(String m_oriStr) {
		this.m_oriStr = m_oriStr;
	}
	public String getOrignalStr() {
		return m_oriStr;
	}
	public void setNewStr(String m_newStr) {
		this.m_newStr = m_newStr;
	}
	public String getNewStr() {
		return m_newStr;
	}
	
}
