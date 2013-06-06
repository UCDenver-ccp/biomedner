package bioner.normalization.data.index;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Explanation.IDFExplanation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import bioner.normalization.data.BioNERCandidate;

public class LuceneIndexReader implements IndexReader{
	private Directory directory;
	private IndexSearcher isearcher;
	private IndexSearcher isearcher_idf;
	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
	private QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"names", analyzer);
	private Similarity sim;


	public LuceneIndexReader(String indexDir)
	{
		IndexConfig.ReadConfigFile();
		try {
			directory = FSDirectory.open(new File(indexDir));
			isearcher = new IndexSearcher(directory, true);
			File indexDirFile = new File(indexDir+"_idf");
			if(indexDirFile.exists())
			{
				directory = FSDirectory.open(indexDirFile);
				isearcher_idf = new IndexSearcher(directory, true);
			}
			else
			{
				isearcher_idf = isearcher;
			}
			sim = isearcher_idf.getSimilarity();
			
		} catch (IOException e) {
            System.err.println("LuceneIndexReader ERROR:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		
	}
	public BioNERCandidate[] search(String queryStr)
	{
		return search(queryStr, IndexConfig.MAX_RESULT_NUM);
	}
	
	public BioNERCandidate[] search(String queryStr, int maxNum)
	{
		queryStr = queryStr.trim();
		if(queryStr.length()==0) return new BioNERCandidate[0];
		try {
			Query query = parser.parse(queryStr.toLowerCase());
			ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
			
			
			Vector<BioNERCandidate> candidateVector = new Vector<BioNERCandidate>();
			Vector<String> uniqueIDVector = new Vector<String>();
			for (int i = 0; i < hits.length && i<maxNum; i++) 
			{
		      Document hitDoc = isearcher.doc(hits[i].doc);
		      String id = hitDoc.get("id");
		      if(!uniqueIDVector.contains(id))
		      {
		    	  BioNERCandidate candidate = new BioNERCandidate();
		    	  candidate.setRecordID(hitDoc.get("id"));
		    	  candidate.setScore(hits[i].score);
		    	  candidateVector.add(candidate);
		    	  uniqueIDVector.add(id);
		      }
		    }
			int size = candidateVector.size();
			BioNERCandidate[] candidates = new BioNERCandidate[size];
			for(int i=0; i<size; i++)
			{
				candidates[i] = candidateVector.elementAt(i);
			}

            ///normalizeScores(candidates); // TODO re-evaluate this
			
			return candidates;
		} catch (ParseException e) {
            System.err.println("LuceneIndexReader ERROR:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
            System.err.println("LuceneIndexReader ERROR:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close()
	{
		try {
			directory.close();
			isearcher.close();
		} catch (IOException e) {
            System.err.println("LuceneIndexReader ERROR:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		
	}
	
	

	@Override
	public BioNERCandidate[] searchIDs(Vector<String> tokenVector) {
		// TODO Auto-generated method stub
		StringBuffer strBuffer = new StringBuffer();
		for(String token : tokenVector)
		{
			strBuffer.append(token.toLowerCase());
			strBuffer.append(" ");
		}
		return search(strBuffer.toString());
	}
	
	@Override
	public double getIDF(String termStr)
	{
		
		try {
			Term term = new Term("names",termStr);
			double score = sim.idfExplain(term, isearcher_idf).getIdf();
			term = new Term("names",termStr.toLowerCase());
			double normalScore = sim.idfExplain(term, isearcher_idf).getIdf();
			if(normalScore<score) score = normalScore;
			return score;
		} catch (IOException e) {
            System.err.println("LuceneIndexReader ERROR:" + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		}
	}
	
	public static void main(String args[])
	{
		LuceneIndexReader reader = new LuceneIndexReader(IndexConfig.GENE_INDEX_DIRECTORY);
		BioNERCandidate[] candidates = reader.search("s IL 1 beta 2");
		
		for(BioNERCandidate candidate : candidates)
		{
			System.out.println(candidate.getRecordID());
		}
		double idf = reader.getIDF("1");
		System.out.println("idf="+idf);
		reader.close();
	}
	@Override
	public BioNERCandidate[] searchIDs(Vector<String> tokenVector, int maxNum) {
		// TODO Auto-generated method stub
		StringBuffer strBuffer = new StringBuffer();
		for(String token : tokenVector)
		{
			strBuffer.append(token.toLowerCase());
			strBuffer.append(" ");
		}
		return search(strBuffer.toString(),maxNum);
	}

  /** added by roederc **/
  private static void normalizeScores(BioNERCandidate[] candidates)
    {
        double max = -Double.MAX_VALUE;
        for(int i=0; i<candidates.length; i++)
        {
            double score = candidates[i].getScore();
            if(score>max) max = score;
        }
        for(int i=0; i<candidates.length; i++)
        {
            double score = candidates[i].getScore();
            candidates[i].setScore(score/max);
        }
    }

}
