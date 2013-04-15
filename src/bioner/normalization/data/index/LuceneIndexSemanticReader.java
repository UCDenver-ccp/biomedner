package bioner.normalization.data.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.BC2GNBuildNormalizationTrainData;
import bioner.application.bc2gn.BC2GNDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.BioNERCandidate;
import bioner.tools.dictionary.WordJudge;
import bioner.tools.nlptools.DocumentChunkRecognizer;

public class LuceneIndexSemanticReader {
	private Directory directory;
	private IndexSearcher isearcher;
	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
	private QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"names", analyzer);
	public LuceneIndexSemanticReader(String indexDir)
	{
		IndexConfig.ReadConfigFile();
		try {
			directory = FSDirectory.open(new File(indexDir));
			isearcher = new IndexSearcher(directory, true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			return candidates;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public BioNERCandidate[] searchDocument(BioNERDocument document)
	{
		if(m_currentDocument==document) return m_currentArray;
		m_currentDocument = document;
		m_currentArray = searchDocument(document, 2000);
		return m_currentArray;
	}
	
	private BioNERDocument m_currentDocument = null;
	private BioNERCandidate[] m_currentArray = null;
	private static DocumentChunkRecognizer chunkRecognizer = DocumentChunkRecognizer.getDocumentChunkRecognizer();
	public BioNERCandidate[] searchDocument(BioNERDocument document, int maxNum)
	{
		Vector<String> chunkVector = chunkRecognizer.getChunks(document, "NP");
		Vector<String> tokenContainVector = new Vector<String>();
		StringBuffer sb = new StringBuffer();
		for(String chunkStr : chunkVector)
		{
			String[] tokens = chunkStr.split("\\W+");
			for(String token : tokens)
			{
				//if(token.matches("(element|experiment|syndrome|region|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster|chain|site|form|domain|sequence|homolog|homology|homologous|subtype|motif|transcript|[Ff]ragment|signaling|pathway|cell|muscle|cellular|extracellular|acid|nucleoprotein|oncoprotein|glycoprotein|proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptors|[lL]igand|adaptors|adapters|coactivator|activator|transporter|RNA|cDNA|DNA|mRNA)+('?s)?$"));
				if(token.matches("[a-z]{3,}") && WordJudge.isWordIndex(token))
				{
					if(!tokenContainVector.contains(token))
					{
						tokenContainVector.add(token);
						sb.append(token);
						sb.append(" ");
					}
				}
			}
			sb.append(" ");
		}
		/*tokenContainVector.clear();
		sb = new StringBuffer();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			String[] tokens = sentence.getSentenceText().split("\\W+");
			for(String token : tokens)
			{
				if(token.matches("(element|experiment|syndrome|region|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster|chain|site|form|domain|sequence|homolog|homology|homologous|subtype|motif|transcript|[Ff]ragment|signaling|pathway|cell|muscle|cellular|extracellular|acid|nucleoprotein|oncoprotein|glycoprotein|proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptors|[lL]igand|adaptors|adapters|coactivator|activator|transporter|RNA|cDNA|DNA|mRNA)+('?s)?$"));
				if(token.matches("[a-z]{3,}") && WordJudge.isWordIndex(token))
				{
					if(!tokenContainVector.contains(token))
					{
						tokenContainVector.add(token);
						sb.append(token);
						sb.append(" ");
					}
				}
			}
			sb.append(" ");
		}*/
		return search(sb.toString(), maxNum);
	}
	
	private static LuceneIndexSemanticReader m_reader = new LuceneIndexSemanticReader(IndexConfig.GENE_INDEX_DIRECTORY+"_semantic");
	public static LuceneIndexSemanticReader getLuceneIndexSemanticReader()
	{
		return m_reader;
	}
	
	public static void main(String[] args)
	{
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder("../../BC2GN/data/testingData");
		HashMap<String,Vector<String>> idTable = BC2GNBuildNormalizationTrainData.getGeneIDTable("../../BC2GN/data/testing.genelist");
		BioNERDocument[] documents = docBuilder.buildDocuments();
		
		int coveredIDNum=0;
		int totalIDNum=0;
		for(BioNERDocument document : documents)
		{
			LuceneIndexSemanticReader reader = getLuceneIndexSemanticReader();
			BioNERCandidate[] candidates = reader.searchDocument(document);
			String docID = document.getID();
			Vector<String> idVector = idTable.get(docID);
			if(idVector==null) continue;
			for(String geneID : idVector)
			{
				totalIDNum++;
				for(int i=0; i<candidates.length; i++)
				{
					if(geneID.equals(candidates[i].getRecordID()))
					{
						coveredIDNum++;
						break;
					}
				}
			}
		}
		double coverRate = (double)coveredIDNum / (double)totalIDNum;
		System.out.println("coverd ID:"+coveredIDNum+" total ID:"+totalIDNum+" rate:"+coverRate);
	}
}
