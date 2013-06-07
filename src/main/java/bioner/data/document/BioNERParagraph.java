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
		int beginIndex = 0;
		for (int i=0; i<sentences.length; i++) {
			int pos = m_text.indexOf(sentences[i], beginIndex);
if (pos == -1)
	System.out.println(" trouble " + pos);

			System.out.println("BioNERParagraph: sentence start:" + (docBegin +  pos) + " " + beginIndex);
			BioNERSentence sentence = new BioNERSentence(sentences[i], pos, docBegin +  pos);
System.out.println("WTF1:" + sentence.getDocBegin());
			beginIndex += sentences[i].length();
			sentence.setParagraph(this);
			m_sentenceVector.add(sentence);
System.out.println("WTF2:" + sentence.getDocBegin());
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
}
