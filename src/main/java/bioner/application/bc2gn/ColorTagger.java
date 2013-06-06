package bioner.application.bc2gn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.feature.builder.SpeciesEntityStore;

public class ColorTagger {
	public static String getColorLabeledText(String text, Vector<String> labelVector, String colorStr)
	{
		String colorText = text;
		
		for(String labelStr : labelVector)
		{
			labelStr = labelStr.replaceAll("\\(", "\\\\(");
			labelStr = labelStr.replaceAll("\\)", "\\\\)");
			labelStr = labelStr.replaceAll("\\-", "\\\\-");
			labelStr = labelStr.replaceAll("\\.", "\\\\.");
			labelStr = labelStr.replaceAll("\\?", "\\\\?");
			labelStr = labelStr.replaceAll("\\[", "\\\\[");
			labelStr = labelStr.replaceAll("\\]", "\\\\]");
			colorText = colorText.replaceAll("\\b"+labelStr+"\\b", "<font color="+colorStr+">"+labelStr+"</font>");
		}
		
		return colorText;
	}
	
	private static HashMap<String, Vector<String>> readFPResultFile(String filename) throws IOException
	{
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		
		BufferedReader freader = new BufferedReader(new FileReader(filename));
		String line;
		while((line=freader.readLine())!=null)
		{
			String[] parts = line.split("\\t+");
			if(parts.length<4) continue;
			if(parts[0].startsWith("FP:"))
			{
				String docID = parts[0].substring(3);
				Vector<String> gmVector = map.get(docID);
				if(gmVector==null)
				{
					gmVector = new Vector<String>();
					map.put(docID, gmVector);
				}
				for(int i=3; i<parts.length; i++)
				{
					if(!gmVector.contains(parts[i]))
						gmVector.add(parts[i]);
				}
			}
		}
		freader.close();
		return map;
	}
	private static HashMap<String, Vector<String>> readTPResultFile(String filename) throws IOException
	{
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		
		BufferedReader freader = new BufferedReader(new FileReader(filename));
		String line;
		while((line=freader.readLine())!=null)
		{
			String[] parts = line.split("\\t+");
			if(parts.length<4) continue;
			if(parts[0].startsWith("TP:"))
			{
				String docID = parts[0].substring(3);
				Vector<String> gmVector = map.get(docID);
				if(gmVector==null)
				{
					gmVector = new Vector<String>();
					map.put(docID, gmVector);
				}
				for(int i=3; i<parts.length; i++)
				{
					if(!gmVector.contains(parts[i]))
						gmVector.add(parts[i]);
				}
			}
		}
		freader.close();
		return map;
	}
	private static HashMap<String, Vector<String>> readFNResultFile(String filename) throws IOException
	{
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		
		BufferedReader freader = new BufferedReader(new FileReader(filename));
		String line;
		while((line=freader.readLine())!=null)
		{
			String[] parts = line.split("\\t+");
			if(parts.length<3) continue;
			if(parts[0].startsWith("FN:"))
			{
				String docID = parts[0].substring(3);
				Vector<String> gmVector = map.get(docID);
				if(gmVector==null)
				{
					gmVector = new Vector<String>();
					map.put(docID, gmVector);
				}
				for(int i=2; i<parts.length; i++)
				{
					if(!gmVector.contains(parts[i]))
						gmVector.add(parts[i]);
				}
			}
		}
		freader.close();
		return map;
	}
	private static String patternWordStr = "\\b(homology|isoform|subunit|ligand|complement|chain|site|form|domain|autoantigen|antigen|sequence|homolog|type|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|regulator|inhibitor|suppressor|translocator|activator|[lL]igand|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|RNA|DNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|histone|collagen|neuron|caspase|kinases|proteins|genes|phosphatase|polymerase|coactivator|activator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|bound|containing|docking|transforming|colon|stem|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|carcinoma|obesity|apoptosis|necrosis|signal|survival|stress|division|adhesion|control|excision|fusion|shock|proteoglycan|core|chemokine|cytokine|retinol|tyrosine|pyruvate|glutamate|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|heterogeneous|homogeneous|putative|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|mobility|programmed|matrix|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)+('?s)?\\b";
	private static Pattern pattern = Pattern.compile(patternWordStr); 
	private static Vector<String> getFilterPatternWords(String text)
	{
		Vector<String> vector = new Vector<String>();
		Matcher matcher = pattern.matcher(text);
		while(matcher.find())
		{
			String word = text.substring(matcher.start(), matcher.end());
			if(!vector.contains(word))
				vector.add(word);
		}
		return vector;
	}
	
	
	private static HashMap<String, Vector<String>> fp_map = null;
	private static HashMap<String, Vector<String>> tp_map = null;
	private static HashMap<String, Vector<String>> fn_map = null;
	private static int num=0;
	private static void colorTagOneDocument(String filename, BufferedWriter fwriter) throws IOException
	{
		BufferedReader freader = new BufferedReader(new FileReader(filename));
		String titleText = freader.readLine();
		freader.readLine();
		String absText = freader.readLine();
		freader.close();
		File file = new File(filename);
		filename = file.getName();
		int pos = filename.indexOf('.');
		String docID = filename.substring(0, pos);
		Vector<String> gmVector = fp_map.get(docID);
		if(gmVector!=null)
		{
			titleText = getColorLabeledText(titleText, gmVector, "red");
			absText = getColorLabeledText(absText, gmVector, "red");
		}
		gmVector = tp_map.get(docID);
		if(gmVector!=null)
		{
			titleText = getColorLabeledText(titleText, gmVector, "green");
			absText = getColorLabeledText(absText, gmVector, "green");
		}
		gmVector = fn_map.get(docID);
		if(gmVector!=null)
		{
			titleText = getColorLabeledText(titleText, gmVector, "blue");
			absText = getColorLabeledText(absText, gmVector, "blue");
		}
		BioNERDocument document = BC2GNDocumentBuilder.getOneDocument(file.getAbsolutePath());
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		Vector<String> speciesStrVector = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesVector)
		{
			if(!speciesStrVector.contains(speciesEntity.getText()))
				speciesStrVector.add(speciesEntity.getText());
		}
		titleText = getColorLabeledText(titleText, speciesStrVector, "purple");
		absText = getColorLabeledText(absText, speciesStrVector, "purple");
		
		gmVector = getFilterPatternWords(titleText+" "+absText);
		titleText = getColorLabeledText(titleText, gmVector, "orange");
		absText = getColorLabeledText(absText, gmVector, "orange");
		num++;
		fwriter.write("#"+num+" "+docID);
		fwriter.newLine();
		fwriter.write("<br>");
		fwriter.write(titleText);
		fwriter.newLine();
		fwriter.write("<br>");
		fwriter.write(absText);
		fwriter.write("<br><br>");
		fwriter.newLine();
	}
	
	public static void main(String[] args) throws IOException
	{
		String resultFilename = "../../BC2GN/fnfp.gn";
		String dataDir = "../../BC2GN/data/testingData/";
		String outputDir = "../../BC2GN/color_data.html";
		if(args.length==3)
		{
			resultFilename = args[0];
			dataDir = args[1];
			outputDir = args[2];
		}
		File dir = new File(dataDir);
		File[] files = dir.listFiles();
		fp_map = readFPResultFile(resultFilename);
		tp_map = readTPResultFile(resultFilename);
		fn_map = readFNResultFile(resultFilename);
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputDir));
		for(File file : files)
		{
			colorTagOneDocument(file.getAbsolutePath(), fwriter);
		}
	}
}
