package bioner.tools.dictner;

import java.util.Vector;

public class PostionPairRecorder {
	private Vector<NumPair> pairVector = new Vector<NumPair>();
	public void addPair(int num_1, int num_2)
	{
		if(!contains(num_1,num_2))
		{
			NumPair pair = new NumPair(num_1,num_2);
			this.pairVector.add(pair);
		}
	}
	public boolean contains(int num_1, int num_2)
	{
		for(NumPair pair : this.pairVector)
		{
			if(pair.equals(num_1, num_2))
			{
				return true;
			}
		}
		return false;
	}
	public void clear()
	{
		this.pairVector.clear();
	}
}

class NumPair
{
	int m_num_1;
	int m_num_2;
	public NumPair(int num_1, int num_2)
	{
		m_num_1 = num_1;
		m_num_2 = num_2;
	}
	public boolean equals(int num_1, int num_2)
	{
		if(m_num_1==num_1&&m_num_2==num_2) return true;
		if(m_num_2==num_1&&m_num_1==num_2) return true;
		return false;
	}
}