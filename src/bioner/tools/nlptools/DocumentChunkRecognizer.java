package bioner.tools.nlptools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.lang.english.TreebankChunker;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;

public class DocumentChunkRecognizer {
	
	private static DocumentChunkRecognizer m_recognizer = new DocumentChunkRecognizer();
	public static DocumentChunkRecognizer getDocumentChunkRecognizer()
	{
		return m_recognizer;
	}
	
	
	private TokenizerME tokenizer;
	private POSTaggerME posTagger;
	private ChunkerME chunker;
	public DocumentChunkRecognizer()
	{
		try {
			System.out.print("Loading OpenNLP....");
			this.tokenizer = new Tokenizer("models/tokenize/EnglishTok.bin.gz");
			
			
			Dictionary dict = new Dictionary(false);
			this.posTagger = new PosTagger("models/postag/tag.bin.gz", dict);
			this.chunker = new TreebankChunker("models/chunker/EnglishChunk.bin.gz");
			System.out.println("Finished!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Vector<String> getChunks(BioNERDocument document, String type)
	{
		Vector<String> vector = new Vector<String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			addChunksInSentence(sentence.getSentenceText(), vector, type);
		}
		return vector;
	}
	public Vector<String> getChunks(BioNERSentence sentence, String type)
	{
		Vector<String> vector = new Vector<String>();
		
		addChunksInSentence(sentence.getSentenceText(), vector, type);
	
		return vector;
	}
	
	private BioNERDocument m_currentDocument = null;
	private String m_currentType = null;
	private HashMap<BioNERSentence, Vector<BioNEREntity>> m_currentMap = null;
	
	
	public Vector<BioNEREntity> getChunksEntities(BioNERSentence sentence, String type)
	{
		if(m_currentDocument==sentence.getDocument() && type.equals(m_currentType))
		{
			return m_currentMap.get(sentence);
		}
		m_currentDocument = sentence.getDocument();
		m_currentType = type;
		m_currentMap = new HashMap<BioNERSentence, Vector<BioNEREntity>>();
		for(BioNERSentence everySentence : m_currentDocument.getAllSentence())
		{
			Vector<String> chunkVector = getChunks(everySentence, type);
			Vector<BioNEREntity> entityVector = new Vector<BioNEREntity>();
			String text = everySentence.getSentenceText();
			for(String chunkStr : chunkVector)
			{
				int pos = text.indexOf(chunkStr);
				while(pos>0)
				{
					BioNEREntity entity = new BioNEREntity();
					entity.set_Sentence(everySentence);
					int end = pos+chunkStr.length()-1;
					entity.set_position(pos, end);
					entityVector.add(entity);
					pos = text.indexOf(chunkStr, end+1);
				}
			}
			m_currentMap.put(everySentence, entityVector);
		}
	
		return m_currentMap.get(sentence);
	}
	
	public void addChunksInSentence(String text, Vector<String> vector, String type)
	{
		String[] tokens = tokenizer.tokenize(text);
		String[] tags = posTagger.tag(tokens);
		String[] chunkTags = chunker.chunk(tokens, tags);
		StringBuffer sb = new StringBuffer();
		String tagB = "B-"+type;
		String tagI = "I-"+type;
		
		for(int i=0; i<chunkTags.length; i++)
		{
			if((tokens[i].equals("and")||tokens[i].equals(",")) && !chunkTags[i].equals("O"))
			{
				chunkTags[i] = "O";
				if(i<chunkTags.length-1 && chunkTags[i+1].startsWith("I-"))
				{
					chunkTags[i+1] = chunkTags[i+1].replaceAll("I\\-", "B\\-");
				}
			}
		}
		for(int i=1; i<chunkTags.length-1; i++)
		{
			if(tokens[i].equals("(") && chunkTags[i-1].endsWith("-NP") && chunkTags[i+1].equals("B-NP"))
			{
				chunkTags[i] = "I-NP";
				chunkTags[i+1] = "I-NP";
			}
		}
		
		for(int i=0; i<chunkTags.length; i++)
		{
			if(chunkTags[i].equals(tagB))
			{
				String chunkText = sb.toString();
				if(chunkText.length()>0 && !vector.contains(chunkText))
					vector.add(chunkText);
				sb = new StringBuffer();
				sb.append(tokens[i]);
			}
			else if(chunkTags[i].equals(tagI))
			{
				sb.append(" ");
				sb.append(tokens[i]);
			}
		}
		String chunkText = sb.toString();
		if(chunkText.length()>0 && !vector.contains(chunkText))
			vector.add(chunkText);
	}
	
	public static void main(String[] args)
	{
		Vector<String> vector = new Vector<String>();
		DocumentChunkRecognizer recognizer = new DocumentChunkRecognizer();
		String text = "A full-length cDNA homologous to RAB7, a member of the RAB-related GTP-binding protein subfamily, was isolated from a human placenta cDNA library.";
		recognizer.addChunksInSentence(text, vector, "NP");
		for(String chunkStr : vector)
		{
			System.out.println(chunkStr);
		}
	}
}
