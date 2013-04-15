package crf.eval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


public class CRFNEREvaluater {
	private int resultTotalNum = 0;
	private int goldTotalNum = 0;
	private int correctTotalNum = 0;
	
	
	public void evalFile(String filename, int resultColNum, int goldColNum, String type)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			Vector<String> sentenceVector = new Vector<String>();
			while((line=freader.readLine()) != null)
			{
				if(line.length()>0)
				{
					sentenceVector.add(line);
				}else if(!sentenceVector.isEmpty())
				{
					String[] resultSeq = getColoumSeq(sentenceVector, resultColNum);
					String[] goldSeq = getColoumSeq(sentenceVector, goldColNum);
					addSeqPair(resultSeq, goldSeq, type);
					sentenceVector.clear();
				}
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void PrintEvalResult()
	{
		double precision = (double)correctTotalNum / (double)resultTotalNum;
		double recall = (double)correctTotalNum / (double)goldTotalNum;
		if (Double.isNaN(precision)) precision = 0.0;
		if (Double.isNaN(recall)) recall = 0.0;
		double fscore = 2 * precision * recall / (precision + recall);
		if (Double.isNaN(fscore)) fscore = 0.0;
		
		System.out.println("Correct Entity Number:"+correctTotalNum);
		System.out.println("Result Entity Number:"+resultTotalNum);
		System.out.println("GoldStandard Entity Number:"+goldTotalNum);
		System.out.println("Precision:"+precision);
		System.out.println("Recall:"+recall);
		System.out.println("F score:"+fscore);
		
	}
	
	public void clear()
	{
		resultTotalNum = 0;
		goldTotalNum = 0;
		correctTotalNum = 0;
	}
	private String[] getColoumSeq(Vector<String> sentenceVector, int num)
	{
		int size = sentenceVector.size();
		String[] seq = new String[size];
		for(int i=0; i<size; i++)
		{
			String[] parts = sentenceVector.elementAt(i).split("\\s+");
			seq[i] = parts[num];
		}
		return seq;
	}
	
	private void addSeqPair(String[] resultSeq, String[] goldSeq, String type)
	{
		Vector<EvalEntity> resultEntityVector = getEntitiesFromBIOSeq(resultSeq);
		Vector<EvalEntity> goldEntityVector = getEntitiesFromBIOSeq(goldSeq);
		
		int resultNum = 0;
		int goldNum = 0;
		int correctNum = 0;
		
		for(EvalEntity resultEntity : resultEntityVector)
		{
			if(type.equals("ALL") || resultEntity.getType().equals(type))
			{
				resultNum++;
				for(EvalEntity goldEntity : goldEntityVector)
				{
					if(resultEntity.equals(goldEntity))
					{
						correctNum++;
					}
				}
			}
		}
		for(EvalEntity goldEntity : goldEntityVector)
		{
			if(type.equals("ALL") || goldEntity.getType().equals(type))
			{
				goldNum++;
			}
		}
		
		resultTotalNum += resultNum;
		goldTotalNum += goldNum;
		correctTotalNum += correctNum;
	}
	
	
	private Vector<EvalEntity> getEntitiesFromBIOSeq(String[] seq)
	{
		Vector<EvalEntity> entityVector = new Vector<EvalEntity>();
		
		int beginPos = 0;
		int endPos = 0;
		String currentType = null;
		boolean inEntity = false;
		for(int i=0; i<seq.length; i++)
		{
			if(seq[i].startsWith("B"))
			{
				beginPos = i;
				int pos = seq[i].indexOf('-');
				currentType = seq[i].substring(pos+1);
				inEntity = true;
			}else if(!seq[i].equals("I-"+currentType) && inEntity)
			{
				endPos = i-1;
				EvalEntity entity = new EvalEntity(beginPos,endPos,currentType);
				entityVector.add(entity);
				inEntity = false;
			}
		}
		if(inEntity)
		{
			endPos = seq.length-1;
			EvalEntity entity = new EvalEntity(beginPos,endPos,currentType);
			entityVector.add(entity);
			inEntity = false;
		}
		
		return entityVector;
	}
}
