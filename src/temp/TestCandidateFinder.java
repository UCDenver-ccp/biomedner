package temp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.normalization.GeneMentionTokenizer;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.data.index.IndexConfig;
import bioner.normalization.data.index.LuceneIndexReader;

public class TestCandidateFinder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LuceneIndexReader finder = new LuceneIndexReader(IndexConfig.GENE_INDEX_DIRECTORY);
		try {
			BufferedReader freader = new BufferedReader(new FileReader("../../BC3GN/TrainingSet1.gm.txt"));
			
			//BufferedReader freader = new BufferedReader(new FileReader("J:/source/gene corps/bc2GNandGMgold_Subs/sourceforgeDistrib-22-Sept-07/genenormalization/bc2test.genelist"));
			String line;
			int coverdNum = 0;
			int totalNum = 0;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\t+");
				if(parts.length<3) continue;
				totalNum++;
				//System.out.println("Process #"+totalNum);
				String geneID = parts[1];
				boolean covered = false;
				for(int i=2; i<parts.length; i++)
				{
					Vector<String> tokenVector = GeneMentionTokenizer.getTokens(parts[i]);
					/*if(parts[i].split("[\\W|\\s]+").length==1)
					{
						tokenVector.add(parts[i]);
					}*/
					tokenVector.add(parts[i].replaceAll("\\W", " "));
					BioNERCandidate[] candidates = finder.searchIDs(tokenVector);
					for(BioNERCandidate candidate : candidates)
					{
						if(candidate.getRecordID().equals(geneID))
						{
							coverdNum++;
							covered = true;
							break;
						}
					}
					if(covered) break;
				}
				if(!covered) System.out.println(line);
			}
			freader.close();
			double rate = (double)coverdNum / (double)totalNum;
			System.out.println("Total num:"+totalNum);
			System.out.println("Covered num:"+coverdNum);
			System.out.println("Cover rate:"+rate);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
