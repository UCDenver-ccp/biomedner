package bioner.tools.dictionary;

import java.util.Vector;

public class TreeTable<F> {
	private Vector<String> m_keySet = new Vector<String>();
	private TreeNode<F> m_root = new TreeNode<F>();
	public void put(String key, F value)
	{
		key = key.toLowerCase();
		int length = key.length();
		TreeNode<F> currentNode = m_root;
		for(int i=0; i<length; i++)
		{
			char currentChar = key.charAt(i);
			TreeNode<F> nextNode = currentNode.getChildNode(currentChar);
			if(nextNode==null)
			{
				currentNode.addChildNode(currentChar);
				nextNode = currentNode.getChildNode(currentChar);
			}
			currentNode = nextNode;
		}
		currentNode.setValue(value);
		m_keySet.add(key);
	}
	public F get(String key)
	{
		key = key.toLowerCase();
		int length = key.length();
		TreeNode<F> currentNode = m_root;
		for(int i=0; i<length; i++)
		{
			char currentChar = key.charAt(i);
			TreeNode<F> nextNode = currentNode.getChildNode(currentChar);
			if(nextNode==null)
			{
				return null;
			}
			currentNode = nextNode;
		}
		return currentNode.getValue();
	}
	public Vector<String> keySet()
	{
		return m_keySet;
	}
}
