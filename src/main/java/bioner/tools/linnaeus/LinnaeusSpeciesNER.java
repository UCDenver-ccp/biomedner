package bioner.tools.linnaeus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import uk.ac.man.entitytagger.Mention;
import uk.ac.man.entitytagger.matching.Matcher;
import uk.ac.man.entitytagger.matching.Matcher.Disambiguation;
import uk.ac.man.entitytagger.matching.Postprocessor;
import uk.ac.man.entitytagger.matching.matchers.MatchPostProcessor;
import uk.ac.man.entitytagger.matching.matchers.MultiMatcher;
import uk.ac.man.entitytagger.matching.matchers.VariantDictionaryMatcher;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.tools.NERProcessor;

public class LinnaeusSpeciesNER implements NERProcessor {

	private Matcher[] m_matchers = null;
	private Matcher m_postprocessMatcher = null;
	public static String SPECIES_OBJECT_FILENAME = GlobalConfig.ROOT_DIR+"data/dict/linnaeus_species_object.gz";
	private LinnaeusSpeciesNER()
	{
		System.err.print("Loading linnuaeus datas....");
		m_matchers = new Matcher[4];
		m_matchers[0] = VariantDictionaryMatcher.load(new File(GlobalConfig.ROOT_DIR+"data/dict/dict-celllines-strict.tsv"), false);
		m_matchers[1] = VariantDictionaryMatcher.load(new File(GlobalConfig.ROOT_DIR+"data/dict/dict-genera-proxy.tsv"), false);
		m_matchers[2] = VariantDictionaryMatcher.load(new File(GlobalConfig.ROOT_DIR+"data/dict/synonyms-manual-variants.tsv"), true);
		try {
			m_matchers[3] = LinnaeusMatcherLoader.loadFromFile(SPECIES_OBJECT_FILENAME);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Matcher> matcherList = new ArrayList<Matcher>();
		for(int i=0; i<m_matchers.length; i++)
		{
			matcherList.add(m_matchers[i]);
		}
		Matcher multiMatcher = new MultiMatcher(matcherList, true);
		File[] stopTermFile = new File[]{new File(GlobalConfig.ROOT_DIR+"data/dict/stoplist.tsv")};
		File[] acronymProbFile = new File[]{new File(GlobalConfig.ROOT_DIR+"data/dict/synonyms-acronyms.tsv")};
		File[] entityFrequencyFile = new File[]{new File(GlobalConfig.ROOT_DIR+"data/dict/species-frequency.tsv")};
		Postprocessor postprocessor = new Postprocessor(stopTermFile, acronymProbFile, entityFrequencyFile, null, null);
		m_postprocessMatcher = new MatchPostProcessor(multiMatcher, Disambiguation.ON_WHOLE, true, null, postprocessor);
		System.err.println("Finished");
	}
	@Override
	public BioNEREntity[] recognizeSentence(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> vector = new Vector<BioNEREntity>();
		String text = sentence.getSentenceText();
		/*ArrayList<Mention> allMentionList = new ArrayList<Mention>();
		for(Matcher matcher : m_matchers)
		{
			List<Mention> mentionList = matcher.match(text);
			for(Mention mention : mentionList)
			{
				allMentionList.add(mention);
			}
		}
		List<Mention> mentionList = VariantDictionaryMatcher.combineMatches(allMentionList);
		mentionList = VariantDictionaryMatcher.disambiguate(text, mentionList, Disambiguation.ON_WHOLE);
		VariantDictionaryMatcher.detectEnumerations(mentionList, text);*/
		List<Mention> mentionList = m_postprocessMatcher.match(text);
		for(Mention mention : mentionList)
		{
			BioNEREntity entity = new BioNEREntity();
			entity.set_Sentence(sentence);
			entity.set_position(mention.getStart(), mention.getEnd()-1);
			for(String id : mention.getIds())
			{
				int pos = id.lastIndexOf(':');
				id = id.substring(pos+1);
				entity.addID(id);
			}
			vector.add(entity);
		}
		
		int size = vector.size();
		BioNEREntity[] array = new BioNEREntity[size];
		for(int i=0; i<size; i++)
		{
			array[i] = vector.elementAt(i);
		}
		return array;
	}
	
	private static LinnaeusSpeciesNER m_instance = new LinnaeusSpeciesNER();
	public static LinnaeusSpeciesNER getLinneausSpeciesNER()
	{
		return m_instance;
	}
	public static void main(String[] args)
	{
		String sentenceText = "We have boy a hub identified IGF022/01 cellline Buchnera aphidicola a novel human cDNA with a predicted protein sequence that has 28% amino acid identity with the E. coli Hsp70 co-chaperone GrpE and designated it HMGE.";
		BioNERSentence sentence = new BioNERSentence(sentenceText,0);
		NERProcessor ner = new LinnaeusSpeciesNER();
		BioNEREntity[] entities = ner.recognizeSentence(sentence);
		for(BioNEREntity entity : entities)
		{
			System.out.print(entity.getText()+":");
			for(String id: entity.getID())
			{
				System.out.print(" "+id);
			}
			System.out.println();
		}
	}
}
