package genetukit.api;

import bioner.data.document.BioNERSentence;

public class GNSpan {
	private String text;
    private String sentence;
    private int begin;
    private int end;
    private BioNERSentence nerSentence;

    public GNSpan(String text, int begin, int end, String sentence, BioNERSentence nerSentence) {
        this.text = text;
        this.begin = begin;
        this.end = end;
        this.sentence = sentence;
        this.nerSentence = nerSentence;
    }

    public String getText() { return text; }
    public int getBegin() { return begin; }
    public int getEnd() { return end; }
    public String getSentence() { return sentence; }

    public BioNERSentence getNerSentence() { return nerSentence; }
}

