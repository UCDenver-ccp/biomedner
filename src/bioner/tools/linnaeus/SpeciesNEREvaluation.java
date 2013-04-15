package bioner.tools.linnaeus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.tools.nlptools.OpenNLPTool;
import bioner.tools.nlptools.SentenceSpliter;

public class SpeciesNEREvaluation {
	private static SentenceSpliter sentenceSpliter = new OpenNLPTool();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GlobalConfig.ReadConfigFile();
		String inputDir = "/home/ljc/software/LINNAEUS/manual-corpus-species-1.0/txt/";
		String outputFilename = "/home/ljc/software/LINNAEUS/manual-corpus-species-1.0/result.txt";
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
		File[] files = new File(inputDir).listFiles();
		int num=0;
		for(File file : files)
		{
			num++;
			System.out.print("Processing #"+num+"....");
			BioNERDocument document = buildOneDocument(file);
			Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
			for(BioNEREntity speciesEntity : speciesVector)
			{
				BioNERSentence sentence = speciesEntity.get_Sentence();
				sentence.addEntity(speciesEntity);
			}
			String documentStr = getFileStr(file);
			String docID = document.getID();
			for(BioNERSentence sentence : document.getAllSentence())
			{
				String sentenceStr = sentence.getSentenceText();
				int pos = documentStr.indexOf(sentenceStr);
				if(pos<0)
				{
					System.err.println("Error in finding sentence:"+sentenceStr);
					continue;
				}
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					for(String id : entity.getID())
					{
						fwriter.write("species:ncbi:"+id);
						fwriter.write("\t");
						fwriter.write(docID);
						fwriter.write("\t");
						int begin = entity.get_Begin()+pos;
						fwriter.write(Integer.toString(begin));
						fwriter.write("\t");
						int end = entity.get_End()+pos+1;
						fwriter.write(Integer.toString(end));
						fwriter.write("\t");
						fwriter.write(entity.getText());
						fwriter.newLine();
					}
				}
			}
			System.out.println("Finished!");
		}
		fwriter.close();
	}
	
	public static String getFileStr(File file) throws IOException
	{
		BufferedReader freader = new BufferedReader(new FileReader(file));
		String line;
		StringBuffer sb = new StringBuffer();
		while((line=freader.readLine())!=null)
		{
			sb.append(line);
		}
		freader.close();
		return sb.toString();
	}
	
	public static BioNERDocument buildOneDocument(File file) throws IOException
	{
		BioNERDocument document = new BioNERDocument();
		String filename = file.getName();
		int pos = filename.lastIndexOf('.');
		String docID = filename.substring(0, pos);
		document.setID(docID);
		BufferedReader freader = new BufferedReader(new FileReader(file));
		String line;
		line=freader.readLine();
		document.setTitle(line);
		
		BioNERSection section = null;
		section = new BioNERSection();
		section.setDocument(document);
		document.addSection(section);
		while((line=freader.readLine())!=null)
		{
			if(line.length()<=0) continue;
			if(line.matches("[A-Z\\W]+"))
			{
				section = new BioNERSection();
				section.setDocument(document);
				document.addSection(section);
				BioNERSentence titleSentence = new BioNERSentence(line, 0);
				section.setTitleSentence(titleSentence);
			}
			else
			{
				BioNERParagraph paragraph = new BioNERParagraph();
				paragraph.setText(line);
				String[] sentenceStrs = sentenceSpliter.sentenceSplit(line);
				paragraph.setSentence(sentenceStrs);
				section.addParagraph(paragraph);
			}
		}
		freader.close();
		document.linkComponent();
		return document;
	}
}
