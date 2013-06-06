package bioner.normalization.data.index;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.GeneMentionTokenizer;
import bioner.process.organismner.OrgnismDictionaryBuilder;
import bioner.tools.NERProcessor;
import bioner.tools.dictionary.BioNERDictionary;


public class LuceneIndexNER implements NERProcessor{
	private Directory directory;
	private IndexSearcher isearcher;
	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
	private QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"names", analyzer);
    private String label = null;
	
	public LuceneIndexNER(String indexDir, String label) {
        this(indexDir);
        this.label = label;
    }

	public LuceneIndexNER(String indexDir)
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
	
	public Vector<BioNEREntity> indexNER(BioNERSentence sentence)
	{
		Vector<BioNEREntity> entityVector = new Vector<BioNEREntity>();
		
		Vector<String> tokenVector = GeneMentionTokenizer.getTokens(sentence.getSentenceText());
		if(tokenVector.isEmpty())
		{
			return entityVector;
		}
		StringBuffer strBuffer = new StringBuffer();
		for(String token : tokenVector)
		{
			if(token.length()<=1||token.matches("[\\d\\W]+")) continue;
			strBuffer.append(token);
			strBuffer.append(" ");
		}
		Vector<PatternNode> patternStrs = getPatternString(strBuffer.toString());
		String sentenceText = sentence.getSentenceText().toLowerCase();
		for(PatternNode patternStr : patternStrs)
		{
			Pattern pattern = Pattern.compile(patternStr.getPattern());
			Matcher matcher = pattern.matcher(sentenceText);
			while(matcher.find())
			{
				int begin = matcher.start();
				int end = matcher.end()-1;
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				entity.set_position(begin, end);
				entity.addID(patternStr.getID());
                if (label != null) {
                    entity.addLabel(label);
                }
				entityVector.add(entity);
			}
		}
		
		//Remove entities covered by other bigger entity.
		//When recognize 'D mel' 'D' and 'mel', only keep 'D mel'
		Vector<BioNEREntity> uncoverdEntityVector = new Vector<BioNEREntity>();
		for(BioNEREntity entity : entityVector)
		{
			boolean covered = false;
			for(BioNEREntity otherEntity : uncoverdEntityVector)
			{
				if(otherEntity==entity) continue;
				if(otherEntity.getTokenBeginIndex()<= entity.getTokenBeginIndex()
						&& otherEntity.getTokenEndIndex() >= entity.getTokenEndIndex())
				{
					covered = true;
					if(otherEntity.getTokenBeginIndex()== entity.getTokenBeginIndex()
							&& otherEntity.getTokenEndIndex() == entity.getTokenEndIndex())
					for(String id : entity.getID())
					{
						otherEntity.addID(id);
					}
					break;
				}
				if(otherEntity.getTokenBeginIndex()> entity.getTokenBeginIndex()
						&& otherEntity.getTokenEndIndex() < entity.getTokenEndIndex())
				{
					covered = true;
					otherEntity.clearID();
					for(String id : entity.getID())
					{
						otherEntity.addID(id);
					}
					otherEntity.setTokenBeginIndex(entity.getTokenBeginIndex());
					otherEntity.setTokenEndIndex(entity.getTokenEndIndex());
					break;
				}
			}
			if(!covered)
			{
				uncoverdEntityVector.add(entity);
			}
		}
		return uncoverdEntityVector;
	}
	
	public Vector<PatternNode> getPatternString(String queryStr)
	{
		queryStr = queryStr.trim();
		if(queryStr.length()==0) return new Vector<PatternNode>();
		try {
			Query query = parser.parse(queryStr.toLowerCase());
			ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
			
			Vector<PatternNode> patternStrs = new Vector<PatternNode>();
			for (int i = 0; i < hits.length; i++) 
			{
		      Document hitDoc = isearcher.doc(hits[i].doc);
		     
		      String nameStr = hitDoc.get("names");
		      String id = hitDoc.get("id");
		      String[] parts = nameStr.split("\\s+");
		      StringBuffer sb = new StringBuffer("\\b");
		      for(int j=0; j<parts.length; j++)
		      {
		    	  if(j>0 && parts[j].length()>0)
		    	  {
		    		  sb.append("[\\W\\_]*");
		    	  }
		    	  sb.append(parts[j].replaceAll("[\\W\\_]+", "[\\\\W\\\\_]*"));
		      }
		      
		      sb.append("\\b");
		      String patternStr = sb.toString();
		      
		      PatternNode node = new PatternNode(id, patternStr);
		      patternStrs.add(node);
		    }
			return patternStrs;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		String sentenceText = "We have a hub mice mouse man identified Buchnera aphidicola a novel human cDNA with a predicted protein sequence that has 28% amino acid identity with the E. coli Hsp70 co-chaperone GrpE and designated it HMGE.";
		//sentenceText = "The exhaustive search across databases allows identifying 5 putative members of the Obp83 group ( A. gambiae OBP17 ( XM001231182 ) , C. pipiens quinquefasciatus OBP precursor ( AAL86413 ) , C. tarsalis OBP ( AAO73465 ) , and A. aegypti OBP1 ( AAO74643 ) , OBP3-1 ( AAO74645 ) and OBP3-2 ( AAO74646 ) ) .";
		//sentenceText = "E. coli";
		BioNERSentence sentence = new BioNERSentence(sentenceText,0);
		IndexConfig.ReadConfigFile();
		LuceneIndexNER ner = new LuceneIndexNER(IndexConfig.SPECIES_INDEX_DIRECTORY);
		Vector<BioNEREntity> entityVector = ner.indexNER(sentence);
		for(BioNEREntity entity : entityVector)
		{
			System.out.print(entity.getText()+":");
			for(String id : entity.getID())
			{
				System.out.print(" "+id);
			}
			System.out.println();
		}
	}

	@Override
	public BioNEREntity[] recognizeSentence(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> entityVector = indexNER(sentence);
		int size = entityVector.size();
		BioNEREntity[] entities = new BioNEREntity[size];
		for(int i=0; i<size; i++)
		{
			entities[i] = entityVector.elementAt(i);
		}
		return entities;
	}
}
class PatternNode{
	private String id;
	private String pattern;
	public PatternNode(String id, String pattern)
	{
		this.id = id;
		this.pattern = pattern;
	}
	public void setID(String id) {
		this.id = id;
	}
	public String getID() {
		return id;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getPattern() {
		return pattern;
	}
	
}
