package bioner.tools.dictionary;

public class TreeNode<F> {
	private TreeNode<F>[] m_childNodes = new TreeNode[128];
	private F m_value = null;
	public TreeNode()
	{
		for(int i=0; i<m_childNodes.length; i++)
		{
			m_childNodes[i] = null;
		}
	}
	public void addChildNode(char nextChar)
	{
		m_childNodes[nextChar] = new TreeNode<F>();
	}
	public TreeNode<F> getChildNode(char nextChar)
	{
		return m_childNodes[nextChar];
	}
	public void setValue(F value) {
		this.m_value = value;
	}
	public F getValue() {
		return m_value;
	}
	
}
