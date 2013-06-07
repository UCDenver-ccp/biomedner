package bioner.data.document;

import java.util.Vector;

public class BioNERParagraph {
	private String m_text = null;
	private Vector<BioNERSentence> m_sentenceVector = new Vector<BioNERSentence>();
	private BioNERSection m_section = null;
	private int docBegin=0;

	@Deprecated
	public BioNERParagraph() {}
	
	public BioNERParagraph(int docBegin) {
		this.docBegin = docBegin;
	}

	public void setText(String m_text) { this.m_text = m_text; }
	public String getText() { return m_text; }

	public void setSentence(Vector<BioNERSentence> sentenceVector) {
		this.m_sentenceVector = sentenceVector;
		for(BioNERSentence sentence : sentenceVector)
		{
			sentence.setParagraph(this);
		}
	}

	public void setSentence(String[] sentences) {
		setSentence(sentences, 0);
	}

	public void setSentence(String[] sentences,int  currentDocLength) {
		int beginIndex = 0;

		for (int i=0; i<sentences.length; i++) {
			// try to map sentence back to paragraph text (m_text).
			// This is odd because they consist of different things.
			// The sentence text has been tokenized and has added spaces.
			int pos = m_text.indexOf(sentences[i], beginIndex);

			BioNERSentence sentence = new BioNERSentence(sentences[i], pos, currentDocLength );
			beginIndex += sentences[i].length();
			currentDocLength += sentences[i].length();
			sentence.setParagraph(this);
			m_sentenceVector.add(sentence);
		}
	}

	public Vector<BioNERSentence> getSentence() {
		return m_sentenceVector;
	}

	public void addSentence(BioNERSentence sentence) {
		sentence.setParagraph(this);
		m_sentenceVector.add(sentence);
	}

	public void setSection(BioNERSection section) { this.m_section = section; }

	public BioNERSection getSection() { return m_section; }
	
	public void linkComponent() {
		for (BioNERSentence sentence : this.m_sentenceVector) {
			sentence.setParagraph(this);
		}
	}

	public int getLength() {
		int paragraphLength=0;
		for (BioNERSentence bns : getSentence()) {
			paragraphLength += bns.getLength();
		}
		return paragraphLength;	

		/***
		// This does indeed throw. The individual sentences
		// have been tokenized and are longer.
		if (sentenceLength == getText().length()) {
			return paragraphLength;	
		}
		else {
			throw new RuntimeException("BioNERParagraph.getLength() has inconsistent results:" 
				+ getText().length() + " and " + sentenceLength 
				+ " num sentences:" + getSentence().size() 
				+ " difference: " + (getText().length() - sentenceLength) 
			);
		}
		***/
	}
}
