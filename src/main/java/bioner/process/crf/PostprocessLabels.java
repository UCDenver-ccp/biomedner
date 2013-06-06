package bioner.process.crf;

import bioner.data.document.BioNERToken;

public class PostprocessLabels {
	public static void postProcessLabels(String[] labels, BioNERToken[] tokens)
	{
		for(int i=0; i<labels.length; i++)
		{
			if(tokens[i].getText().equals("."))
			{
				if(i==labels.length-1 || labels[i+1].equals("O"))
				{
					labels[i] = "O";
				}
			}
		}
	}
}
