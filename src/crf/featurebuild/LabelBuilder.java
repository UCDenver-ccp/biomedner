package crf.featurebuild;

import java.util.Vector;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;

public class LabelBuilder {
	public Vector<String> buildLabel(BioNERSentence sentence)
	{
		Vector<String> labelVector = new Vector<String>();
		BioNERToken[] tokens = sentence.getTokens();
		int size = tokens.length;
		labelVector.setSize(size);
		for(int i=0; i<size; i++)
		{
			labelVector.set(i, "O");
		}
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			int begin = entity.getTokenBeginIndex();
			int end = entity.getTokenEndIndex();
			labelVector.set(begin, "B-PRO");
			for(int i=begin+1; i<=end; i++)
			{
				labelVector.set(i, "I-PRO");
			}
		}
		return labelVector;
	}
}
