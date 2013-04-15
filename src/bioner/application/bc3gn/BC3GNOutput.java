package bioner.application.bc3gn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


import bioner.application.api.BioNERDocumentOutput;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.data.BioNERCandidate;

public class BC3GNOutput implements BioNERDocumentOutput {

	private BufferedWriter fwriter = null;
	private BufferedWriter detailFwriter = null;
	private String m_outputFilename;
	public BC3GNOutput(String outputFilename)
	{
		m_outputFilename = outputFilename;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			fwriter.close();
			detailFwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
		
		
		try {
			fwriter = new BufferedWriter(new FileWriter(m_outputFilename, true));
			detailFwriter = new BufferedWriter(new FileWriter(m_outputFilename+".detail"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void outputByGeneID(BioNERCandidate[] candidates, BioNERDocument document)
	{
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i]==null) continue;
			try {
				fwriter.write(document.getID());
				fwriter.write("\t");
				fwriter.write(candidates[i].getRecord().getID());
				fwriter.write("\t");
				fwriter.write(Double.toString(candidates[i].getScore()));
				fwriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try {
			fwriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void outputDetail(BioNERCandidate[] candidates, BioNERDocument document, HashMap<String, Vector<BioNEREntity>> geneIDMap)
	{
		for(int i=0; i<candidates.length; i++)
		{
			if(candidates[i]==null) continue;
			Vector<BioNEREntity> gmVector = geneIDMap.get(candidates[i].getRecord().getID());
			Vector<String> gmStrVector = new Vector<String>();
			for(BioNEREntity gmEntity : gmVector)
			{
				String gmStr = gmEntity.getText();
				if(!gmStrVector.contains(gmStr))
				{
					gmStrVector.add(gmStr);
				}
			}
			try {
				detailFwriter.write(document.getID());
				detailFwriter.write("\t");
				detailFwriter.write(candidates[i].getRecord().getID());
				detailFwriter.write("\t");
				detailFwriter.write(Double.toString(candidates[i].getScore()));
				detailFwriter.write("\t");
				detailFwriter.write(candidates[i].getRecord().getSpeciesID());
				for(String gmStr : gmStrVector)
				{
					detailFwriter.write("\t");
					detailFwriter.write(gmStr);
				}
				detailFwriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	@Override
	synchronized public void outputDocument(BioNERDocument document) {
		// TODO Auto-generated method stub
		HashMap<String, Vector<String>> resultTable = new HashMap<String, Vector<String>>();
		HashMap<String, Double> scoreTable = new HashMap<String, Double>();
		String id = document.getID();
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				
				BioNERCandidate[] candidates = entity.getCandidates();
				if(candidates.length>0)
				{
					for(int i=0; i<1 && i<candidates.length; i++)
					{
						String geneID = candidates[i].getRecordID();
						String speciesID = candidates[i].getRecord().getSpeciesID();
						double score = candidates[i].getScore();
						//if(score<0.5) continue;
						Double geneScore = scoreTable.get(geneID);
						if(geneScore==null || geneScore<score) 
						{
							geneScore = score;
							scoreTable.put(geneID, geneScore);
						}
						
						
						Vector<String> textVector = resultTable.get(geneID);
						if(textVector==null)
						{
							textVector = new Vector<String>();
							resultTable.put(geneID, textVector);
						}
						String text = entity.getText();
						if(!textVector.contains(text))
						{
							textVector.add(text);
						}
						
					}//for(int i=0; i<2 && i<candidates.length; i++)
					
				}//if(candidates.length>0)
			}//for each entity
		}//for each sentence
		try {
			OutputNode[] outputArray = new OutputNode[resultTable.keySet().size()];
			int i=0;
			for(String geneID : resultTable.keySet())
			{
				Vector<String> textVector = resultTable.get(geneID);
				double score = scoreTable.get(geneID);
				StringBuffer lineBuffer = new StringBuffer(id+"\t"+geneID+"\t"+score);
				outputArray[i] = new OutputNode(lineBuffer.toString(), score);
				i++;
				/*for(String text : textVector)
				{
					lineBuffer.append("\t"+text);
				}*/
			}
			RankGenes.Rank(outputArray);
			for(i=0; i<outputArray.length; i++)
			{
				fwriter.write(outputArray[i].getLine());
				fwriter.newLine();
			}
			fwriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class OutputNode {
	private String m_line;
	private double m_score;
	public OutputNode(String line, double score)
	{
		m_line = line;
		m_score = score;
	}
	
	public double getScore()
	{
		return m_score;
	}
	public String getLine()
	{
		return m_line;
	}
}

class RankGenes {
	public static void Rank(OutputNode[] genes)
	{
		quickSort(genes);
	}
	
	private static void quickSort(OutputNode[] array) {
	        quickSort(array, 0, array.length - 1);
	}

    private static void quickSort(OutputNode[] array, int low, int high) {
        if (low < high) {
            int p = partition(array, low, high);
            quickSort(array, low, p - 1);
            quickSort(array, p + 1, high);
        }

    }

    private static int partition(OutputNode[] array, int low, int high) {
        double s = array[high].getScore();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j].getScore() > s) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, ++i, high);
        return i;
    }

    private static void swap(OutputNode[] array, int i, int j) {
    	OutputNode temp;
        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
