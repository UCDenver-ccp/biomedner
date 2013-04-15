package bioner.tools.linnaeus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import bioner.global.GlobalConfig;

import uk.ac.man.entitytagger.Mention;
import uk.ac.man.entitytagger.matching.Matcher;
import uk.ac.man.entitytagger.matching.matchers.VariantDictionaryMatcher;

public class LinnaeusMatcherLoader implements Serializable {
	private Matcher m_matcher;

	public void setMatcher(Matcher matcher) {
		this.m_matcher = matcher;
	}

	public Matcher getMatcher() {
		return m_matcher;
	}
	public static void saveToFile(Matcher matcher, String filename) throws IOException
	{
		LinnaeusMatcherLoader loader = new LinnaeusMatcherLoader();
		loader.setMatcher(matcher);
		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)));
		out.writeObject(loader);
		out.close();
	}
	public static Matcher loadFromFile(String filename) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(filename)));
		LinnaeusMatcherLoader loader = (LinnaeusMatcherLoader)in.readObject();
		return loader.getMatcher();
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		//Matcher matcher = VariantDictionaryMatcher.load(new File("/home/ljc/software/LINNAEUS/species/dict-species.tsv"), true);
		//saveToFile(matcher, LinnaeusSpeciesNER.SPECIES_OBJECT_FILENAME);
		//matcher = loadFromFile(LinnaeusSpeciesNER.SPECIES_OBJECT_FILENAME);
		Matcher matcher = VariantDictionaryMatcher.load(new File("/home/ljc/EntrezGene/gene-dict.tsv"), true);
		saveToFile(matcher, "/home/ljc/EntrezGene/gene-dict.obj.gz");
		matcher = loadFromFile("/home/ljc/EntrezGene/gene-dict.obj.gz");
		System.err.println("Finish loading");
		String text = "We have boy a hub identified p49879_1p32 IGF022/01 cellline Buchnera aphidicola a novel human cDNA with a predicted protein sequence that has 28% amino acid identity with the E. coli Hsp70 co-chaperone GrpE and designated it HMGE.";
		List<Mention> mentionList = matcher.match(text);
		for(Mention mention : mentionList)
		{
			System.out.println(mention.getText());
		}
	}
}
