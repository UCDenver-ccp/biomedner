package bioner.process.proteinner;

import abner.Tagger;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpProteinABNER implements BioNERProcess {

	private static Tagger m_taggerBioCreative = new Tagger(Tagger.BIOCREATIVE);
	private static Tagger m_taggerNLPBA = new Tagger(Tagger.NLPBA);
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : GetNERSentence.getNERSentence(document))
		{
			getEntities(sentence);
		}
	}
	private static void getEntities(BioNERSentence sentence)
	{
		String text = sentence.getSentenceText();
		String[][] entities = m_taggerBioCreative.getEntities(text);
		for(String gmStr : entities[0])
		{
			int pos = text.indexOf(gmStr);
			while(pos>=0)
			{
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				entity.set_position(pos, pos+gmStr.length()-1);
				entity.addLabel(GlobalConfig.ENTITY_LABEL_ABNER_BC);
				sentence.addEntity(entity);
				pos = text.indexOf(gmStr, pos+gmStr.length());
			}
		}
		
		entities = m_taggerNLPBA.getEntities(text);
		
		for(int i=0; i<entities[0].length; i++)
		{
			String label;
			if(entities[1][i].equals("PROTEIN")) 
			{
				label = GlobalConfig.ENTITY_LABEL_ABNER_PROTEIN;
			}
			else if(entities[1][i].equals("DNA"))
			{
				label = GlobalConfig.ENTITY_LABEL_ABNER_DNA;
			}
			else continue;
			String gmStr = entities[0][i];
			int pos = text.indexOf(gmStr);
			while(pos>=0)
			{
				BioNEREntity entity = new BioNEREntity();
				entity.set_Sentence(sentence);
				entity.set_position(pos, pos+gmStr.length()-1);
				
				entity.addLabel(label);
				sentence.addEntity(entity);
				pos = text.indexOf(gmStr, pos+gmStr.length());
			}
			
		}
	}
	public static void main(String[] args)
	{
		String text = "SLAP-2 interacts with Cbl in vivo in a phosphorylation independent manner and with ZAP-70 and T cell receptor zeta chain upon T cell receptor activation.";
		BioNERSentence sentence = new BioNERSentence(text, 0);
		getEntities(sentence);
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			System.out.println(entity.getLabelVector().elementAt(0)+":"+entity.getText());
		}
	}
}
