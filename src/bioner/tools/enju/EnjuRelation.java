package bioner.tools.enju;

import java.util.Vector;

public class EnjuRelation {
	private EnjuWord m_centralWord = null;
	private Vector<EnjuWord> m_argWordVector = new Vector<EnjuWord>();
	
	public void addLine(String line)
	{
		String[] parts = line.split("\\t+");
		if(m_centralWord==null)
		{
			m_centralWord = new EnjuWord(parts[0], parts[2], Integer.parseInt(parts[4]));
		}
		EnjuWord argWord = new EnjuWord(parts[7], parts[9], Integer.parseInt(parts[11]));
		m_argWordVector.add(argWord);
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if(m_centralWord!=null)
		{
			sb.append(m_centralWord.toString());
			for(int i=0; i<m_argWordVector.size(); i++)
			{
				sb.append("|");
				sb.append(m_argWordVector.elementAt(i).toString());
			}
		}
		return sb.toString();
	}
	
	public EnjuWord getCentralWord()
	{
		return m_centralWord;
	}
	public int getArgWordNum()
	{
		return m_argWordVector.size();
	}
	public Vector<EnjuWord> getAllArgWords()
	{
		return m_argWordVector;
	}
}
