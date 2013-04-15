package bioner.normalization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.application.bc2gn.BC2GNDocumentBuilder;
import bioner.application.bc2gn.ProcessImpBC2GNGMFilter;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;
import bioner.tools.nlptools.CombinedNameRecognizer;
import bioner.tools.nlptools.DocumentChunkRecognizer;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class FilterListBuilder {
	
	public static Vector<String> getFilterList(BioNERDocument document)
	{
		Vector<String> vector = new Vector<String>();
		
		//addSpecialWordFilterGM(document, vector);
		filterByGNATPatternOne(document, vector);
		filterByGNATPatternTwo(document, vector);
		filterByLastWord(document,vector);
		filterByPlural(document, vector);
		addHaveAInFrontFilterGM(document,vector);
		addOtherSpeciesWordFilterGM(document,vector,"9606");
		//addOtherSpeciesWordFilterGM_Deca(document,vector,"9606");
		addPluralAbb(document, vector);
		refineFilterList(document, vector);
		//removeAbbHaveFullname(document, vector);
		return vector;
	}
	
	
	
	
	
	
	private static void removeAbbHaveFullname(BioNERDocument document,
			Vector<String> vector) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				String fullname = fullNameMap.get(gmText);
				if(fullname!=null && fullname.length()>gmText.length())
				{
					if(!vector.contains(gmText))
						vector.add(gmText);
				}
				
			}
		}
	}

	private static void filterByPlural(BioNERDocument document,
			Vector<String> vector)
	{
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				if(gmText.matches(".*\\b[a-z]+s|.*[A-Z]s"))
				{
					if(!vector.contains(gmText))
						vector.add(gmText);
				}
			}
		}
	}
	
	private static String patternLastWordStr = "^(analysis|complex|complexes|isoform|subunit|ligand|complement|chain|site|form|domain|autoantigen|antigen|sequence|homolog|type|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|regulator|inhibitor|suppressor|translocator|activator|[lL]igand|adaptor|adapter|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|RNA|DNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|rRNA|histone|collagen|neuron|caspase|kinase|phosphatase|polymerase|coactivator|activator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|binding|bound|containing|docking|transforming|breast|colon|stem|cell|muscle|cellular|extracellular|intestinal|nuclear|membrane|brain|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|fetal|chicken|mammalian|human|cancer|carcinoma|tumor|obesity|apoptosis|death|growth|maturation|necrosis|signal|repair|survival|stress|division|adhesion|control|excision|fusion|cycle|heat|shock|proteoglycan|core|chemokine|cytokine|potassium|calcium|sodium|retinol|tyrosine|pyruvate|vitamin|glutamate|zinc|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|GTP|low|high|highly|non|heterogeneous|homogeneous|light|heavy|negative|novel|putative|dependent|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|[a-z]+ine[\\s\\-]rich|[a-z]+ant|mobility|programmed|matrix|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)+('?s)?$";
	private static Pattern lastWordPattern = Pattern.compile(patternLastWordStr);
	private static void filterByLastWord(BioNERDocument document,
			Vector<String> vector) {
		Vector<String> whiteListVector = getGeneWhiteList(document);
		Vector<String> gmRecordVector = new Vector<String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNERToken[] tokens = sentence.getTokens();
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				boolean shouldSkip = false;
				for(String recordedStr : gmRecordVector)
				{
					if(gmText.equals(recordedStr))
					{
						shouldSkip = true;
						break;
					}
				}
				if(shouldSkip) continue;
				gmRecordVector.add(gmText);
				if(whiteListVector.contains(gmText)) continue;
				String lastWord = tokens[entity.getTokenEndIndex()].getText();
				Matcher matcher = lastWordPattern.matcher(lastWord);
				if(matcher.matches())
				{
					if(!vector.contains(gmText))
						vector.add(gmText);
					continue;
				}
				int extendLastWord = getLastWordIndex(entity);
				if(extendLastWord<tokens.length)
				{
					lastWord = tokens[extendLastWord].getText();
					matcher = lastWordPattern.matcher(lastWord);
					if(matcher.matches())
					{
						if(!vector.contains(gmText))
							vector.add(gmText);
					}
				}
			}
		}
	}
	private static int getLastWordIndex(BioNEREntity entity)
	{
		BioNERToken[] tokens = entity.get_Sentence().getTokens();
		int endIndex = entity.getTokenEndIndex();
		if(endIndex<tokens.length-1 && tokens[endIndex+1].getText().equals("("))
		{
			for(int i=endIndex+2; i<tokens.length-1; i++)
			{
				if(tokens[i].getText().equals(")")) return i+1;
			}
		}
		if(endIndex<tokens.length-2 && tokens[endIndex+1].getText().equals(")"))
		{
			return endIndex + 2;
		}
		return endIndex+1;
	}
	
	private static String gnat_patternOneStr = "^(aa|bp\\s[0-9]{1,2}|kd|mg|Ki|nM|CD|Sci|Proc|Acad|[\\d\\.]+[\\s\\-]?[Kk][Dd][Aa]|or\\sin|and\\s[1Ii]|for\\s4|[Aa]\\sgene|[Aa]t\\s5|[Aa]\\sC|is\\s1|at\\s\\d|factor[\\s\\-]\\d|factor[\\s\\-](alpha|beta|gamma|delta)|receptor\\s\\d|[A-Z]\\receptor|protein\\s[A-Za-z]|protein[\\s\\-][0-9]+|beta[\\s\\-]\\d+|\\d+[\\s\\-]beta([\\s\\-]\\d+)?|alpha[\\s\\-]\\d+|\\d+[\\s\\-]alpha([\\s\\-]\\d+)?|(alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)\\s[A-Za-z0-9]|[A-Za-z0-9][\\s\\-](alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)|(alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)\\schain|[A-Za-z][\\s\\-]protein|[Aa]\\s[0-9]{1,2})$";
	private static String gnat_patternTwoStr = "^([Mm]r|\\d+[pq]\\d+|complex|complexes|subunit|complement|site|form|domain|autoantigen|antigen|sequence|homolog|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|regulator|inhibitor|suppressor|translocator|adaptor|adapter|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|tRNA|tRna|trna|histone|collagen|neuron|phosphatase|coactivator|activator|activation|transduction|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|regulating|signaling|bound|containing|docking|transforming|breast|colon|stem|cell|muscle|cellular|extracellular|intestinal|surface|membrane|brain|epidermal|vesicle|mitochondrial|pancreatic|ubiquitous|fetal|chicken|calf|yeast|coli|cancer|carcinoma|tumor|obesity|apoptosis|maturation|necrosis|signal|repair|survival|stress|division|adhesion|control|excision|fusion|cycle|heat|shock|proteoglycan|core|cytokine|potassium|calcium|sodium|retinol|tyrosine|pyruvate|glutamate|zinc|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin|GTP|low|high|highly|non|heterogeneous|homogeneous|light|heavy|negative|novel|putative|dependent|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|putative|peroxisomal|basic|[a-z]+ine[\\s\\-]rich|[a-z]+ant|mobility|programmed|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)+('?s)?$";
	private static Pattern gnat_patternOne = Pattern.compile(gnat_patternOneStr);
	private static Pattern gnat_patternTwo = Pattern.compile(gnat_patternTwoStr);
	private static void filterByGNATPatternOne(BioNERDocument document,
			Vector<String> vector)
	{
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				Matcher matcher = gnat_patternOne.matcher(gmText);
				if(matcher.matches())
				{
					if(!vector.contains(gmText))
						vector.add(gmText);
				}
			}
		}
	}
	private static void filterByGNATPatternTwo(BioNERDocument document,
			Vector<String> vector)
	{
		Vector<String> whiteListVector = getGeneWhiteList(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNERToken[] tokens = sentence.getTokens();
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				
				String gmText = entity.getText();
				if(whiteListVector.contains(gmText)) continue;
				BioNEREntity chunkEntity = getChunkEntity(entity);
				int begin = chunkEntity.getTokenBeginIndex();
				int end = chunkEntity.getTokenEndIndex();
				begin = entity.getTokenBeginIndex();
				end = entity.getTokenEndIndex();
				for(int i=begin; i<=end && i<tokens.length; i++)
				{
					Matcher matcher = gnat_patternTwo.matcher(tokens[i].getText());
					if(matcher.matches())
					{
						if(!vector.contains(gmText))
							vector.add(gmText);
						break;
					}
				}
			}
		}
	}
	
	private static Vector<String> getGeneWhiteList(BioNERDocument document)
	{
		Vector<String> vector = new Vector<String>();
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNERToken[] tokens = sentence.getTokens();
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				BioNEREntity chunkEntity = getChunkEntity(entity);
				int begin = chunkEntity.getTokenBeginIndex();
				int end = chunkEntity.getTokenEndIndex();
				
				//Deal the case: Axxx Bxxx Cxxx (ABC) protein
				if(end<tokens.length-1 && tokens[end+1].getText().equals("("))
				{
					for(int i=end+1; i<tokens.length; i++)
					{
						if(tokens[i].getText().equals(")"))
						{
							end++;
							break;
						}
					}
				}
				
				
				
				for(int i=begin; i<=end && i<tokens.length; i++)
				{
					if(tokens[i].getText().toLowerCase().matches("human|protein|gene|h[A-Z].*"))
					{
						if(!vector.contains(gmText))
							vector.add(gmText);
					}
				}
			}
		}
		for(int i=0; i<vector.size(); i++)
		{
			String gmText = vector.elementAt(i);
			String fullname = fullNameMap.get(gmText);
			if(fullname!=null && !vector.contains(fullname))
				vector.add(fullname);
		}
		return vector;
	}
	
	
	private static void addOtherSpeciesWordFilterGM_Deca(BioNERDocument document,
			Vector<String> vector, String correctID) {
		Vector<String> correctList = new Vector<String>();
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				Vector<String> speciesIDVector = DecaResultReader.getSpeicesIDForGeneMention(entity);
				if(speciesIDVector.contains(correctID))
				{
					String gmText = entity.getText();
					if(!correctList.contains(gmText))
					{
						correctList.add(gmText);
					}
					String fullname = fullNameMap.get(gmText);
					if(fullname!=null && !correctList.contains(fullname))
					{
						correctList.add(fullname);
					}
				}
			}
		}
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		for(BioNEREntity speciesEntity : speciesVector)
		//for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNERSentence sentence = speciesEntity.get_Sentence();
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				if(correctList.contains(gmText)) continue;
				Vector<String> speciesIDVector = DecaResultReader.getSpeicesIDForGeneMention(entity);
				if(speciesIDVector.contains(correctID)) continue;
				
				if(!speciesIDVector.isEmpty())
				{//Do not contain correct species, and is not empty. It must contains incorrect species.
					
					vector.add(gmText);
				}
			}
		}
	}
	
	private static void addOtherSpeciesWordFilterGM(BioNERDocument document,
			Vector<String> vector, String correctID) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		Vector<String> correctList = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesVector)
		{
			
			if(speciesEntity.hasID(correctID))
			{
				BioNERSentence sentence = speciesEntity.get_Sentence();
				for(BioNEREntity gmEntity : sentence.getAllEntities())
				{
					int speciesBeginIndex = speciesEntity.getTokenBeginIndex();
					int speciesEndIndex = speciesEntity.getTokenEndIndex();
					int gmBeginIndex = gmEntity.getTokenBeginIndex();
					int gmEndIndex = gmEntity.getTokenEndIndex();
					String gmText = gmEntity.getText();
					if(speciesEndIndex<gmBeginIndex && Math.abs(speciesEndIndex-gmBeginIndex)<=5)
					{
						if(!correctList.contains(gmText))
							correctList.add(gmText);
					}
					else if(speciesBeginIndex>=gmBeginIndex && speciesEndIndex<=gmEndIndex)
					{
						if(!correctList.contains(gmText))
							correctList.add(gmText);
					}
					else
					{
						if(!correctList.contains(gmText))
							correctList.add(gmText);
					}
				}
			}
		}
		for(BioNEREntity speciesEntity : speciesVector)
		{
			
			BioNERSentence sentence = speciesEntity.get_Sentence();
		
			if(!speciesEntity.hasID(correctID))
			{
				
				for(BioNEREntity gmEntity : sentence.getAllEntities())
				{
					
					int speciesBeginIndex = speciesEntity.getTokenBeginIndex();
					int speciesEndIndex = speciesEntity.getTokenEndIndex();
					int gmBeginIndex = gmEntity.getTokenBeginIndex();
					int gmEndIndex = gmEntity.getTokenEndIndex();
					String gmText = gmEntity.getText();
					if(speciesEndIndex<gmBeginIndex && Math.abs(speciesEndIndex-gmBeginIndex)<=2)
					{
						if(!correctList.contains(gmText) && !vector.contains(gmText))
							vector.add(gmText);
					}
					else if(speciesBeginIndex>=gmBeginIndex && speciesBeginIndex<=gmEndIndex)
					{
						if(!correctList.contains(gmText) && !vector.contains(gmText))
							vector.add(gmText);
					}
					//if(!correctList.contains(gmText) && !vector.contains(gmText))
						//vector.add(gmText);
				}
			}
		}
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				if(gmText.matches("[a-z]+[A-Z].*"))
				{
					String speciesStr = getSpeciesStr(gmText);
					if(!getSpeciesID(speciesStr).equals(correctID)) 
					{
						if(!correctList.contains(gmText) && !vector.contains(gmText))
							vector.add(gmText);
					}
				}
			}
		}
		
	}
	private static String getSpeciesStr(String geneStr)
	{
		for(int i=0; i<geneStr.length(); i++)
		{
			char c = geneStr.charAt(i);
			if(Character.isUpperCase(c))
			{
				return geneStr.substring(0, i);
			}
		}
		return null;
	}
	private static String getSpeciesID(String speciesStr)
	{
		if(speciesStr.equals("h")) return "9606";
		if(speciesStr.equals("r")) return "10116";
		if(speciesStr.equals("m")) return "10090";
		if(speciesStr.equals("d")) return "7227";
		if(speciesStr.equals("me")) return "7227";
		if(speciesStr.equals("mel")) return "7227";
		if(speciesStr.equals("y")) return "4932";
		
		return "";
	}
	
	
	
	private static void refineFilterList(BioNERDocument document,
			Vector<String> vector) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		HashMap<String, String> combinedNameMap = CombinedNameRecognizer.getCombinedNameMap(document);
		int size = vector.size();
		for(int i=0; i<size; i++)
		{
			String gmText = vector.elementAt(i);
			String fullname = fullNameMap.get(gmText);
			if(fullname!=null && !vector.contains(fullname))
				vector.add(fullname);
			String combinedName = combinedNameMap.get(gmText);
			if(combinedName!=null && !vector.contains(combinedName))
				vector.add(combinedName);
		}
		size = vector.size();
		for(int i=0; i<size; i++)
		{
			String gmText = vector.elementAt(i);
			String newText = gmText.replaceAll(specialWordEndStr, "").trim();
			if(newText.length()>0 && !vector.contains(newText))
				vector.add(newText);
		}
		
	}
	private static void addPluralAbb(BioNERDocument document,
			Vector<String> vector) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			String text = sentence.getSentenceText();
			Pattern pattern = Pattern.compile("\\b[A-Z]+s\\b");
			Matcher matcher = pattern.matcher(text);
			while(matcher.find())
			{
				String str = text.substring(matcher.start(), matcher.end());
				if(!vector.contains(str))
					vector.add(str);
				//Get XXX from XXXs and add it
				str = str.substring(0, str.length()-1);
				if(!vector.contains(str))
					vector.add(str);
			}
		}
	}
	private static String specialWordEndStr = "element|experiment|syndrome|region|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster|chain|site|form|domain|sequence|homolog|homology|homologous|subtype|motif|transcript|[Ff]ragment|signaling|pathway|cell|muscle|cellular|extracellular|acid|nucleoprotein|oncoprotein|glycoprotein|proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptors|[lL]igand|adaptors|adapters|coactivator|activator|RNA|cDNA|DNA";
	//private static String specialWordStr = "region|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster|chain|site|form|domain|sequence|homolog|homology|homologous|subtype|motif|transcript|[Ff]ragment|signaling|pathway|cell|acid|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin";
	private static String specialWordContainStr = "pull\\-down";
	private static Pattern specialWordFilterPattern = Pattern.compile(".*\\b("+specialWordEndStr+")s?|.*\\b("+specialWordContainStr+")\\b.*");
	private static void addSpecialWordFilterGM(BioNERDocument document, Vector<String> vector)
	{
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		Vector<String> gmRecordVector = new Vector<String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				//String extendedText = getExtendedText(entity);
				String extendedText = getExtendedText_byChunk(entity);
				String gmText = entity.getText();
				//if(gmRecordVector.contains(gmText)) continue;
				boolean shouldSkip = false;
				for(String recordedStr : gmRecordVector)
				{
					if(gmText.equals(recordedStr))
					{
						shouldSkip = true;
						break;
					}
				}
				if(shouldSkip) continue;
				if(sentence!=document.getTitle())
					gmRecordVector.add(gmText);
				Matcher matcher = specialWordFilterPattern.matcher(extendedText);
				if(matcher.matches())
				{
					if(!vector.contains(gmText))
						vector.add(gmText);
					if(!vector.contains(extendedText))
						vector.add(extendedText);
				}
				String fullname = fullNameMap.get(gmText);
				if(fullname!=null)
				{
					matcher = specialWordFilterPattern.matcher(fullname);
					if(matcher.matches())
					{
						if(!vector.contains(gmText))
							vector.add(gmText);
						if(!vector.contains(fullname))
							vector.add(fullname);
					}
				}
				
			}
		}
	}
	
	private static void addHaveAInFrontFilterGM(BioNERDocument document, Vector<String> vector)
	{
		Vector<String> gmRecordVector = new Vector<String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			//BioNERToken[] tokens = sentence.getTokens();
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String gmText = entity.getText();
				boolean shouldSkip = false;
				for(String recordedStr : gmRecordVector)
				{
					if(gmText.equals(recordedStr))
					{
						shouldSkip = true;
						break;
					}
				}
				if(shouldSkip) continue;
				if(sentence!=document.getTitle())
					gmRecordVector.add(gmText);
				String extendedText = getExtendedText_byChunk(entity);
				if(extendedText.matches("(a|an) .* (gene|protein)|(a|an) .*"))
				{
					if(!vector.contains(gmText))
						vector.add(gmText);
				}
				/*for(int i=entity.getTokenEndIndex(); i>=0 && i>entity.getTokenBeginIndex()-4; i--)
				{
					if(tokens[i].getText().matches("\\,|\\.|\\;|\\:|and|or|of|with|by|to|from|without|about|in|at|for|on|before|after|above|bellow|the|this|that")) break;
					if(tokens[i].getText().matches("\\ba|an\\b"))
					{
						if(!vector.contains(gmText))
							vector.add(gmText);
					}
				}*/
			}
		}
	}
	
	private static Pattern extendPattern = Pattern.compile("\\b("+specialWordEndStr+")s?\\b");
	public static String getExtendedText(BioNEREntity entity)
	{
		StringBuffer sb = new StringBuffer();
		
		BioNERToken[] tokens = entity.get_Sentence().getTokens();
		int end = entity.getTokenEndIndex();
		for(int i=end; i<tokens.length && i<end+4; i++)
		{
			if(tokens[i].getText().matches("\\,|\\.|\\;|\\:|and|or|of|with|by|to|from|without|about|in|at|for|on|before|after|above|bellow")) break;
			Matcher matcher = extendPattern.matcher(tokens[i].getText());
			if(matcher.matches())
			{
				end = i;
				break;
			}
		}
		for(int i=entity.getTokenBeginIndex(); i<=end; i++)
		{
			sb.append(tokens[i].getText()+" ");
		}
		
		return sb.toString().trim();
	}
	private static DocumentChunkRecognizer chunkRecognizer = new DocumentChunkRecognizer();
	public static String getExtendedText_byChunk(BioNEREntity entity)
	{
		Vector<BioNEREntity> chunkEntityVector = chunkRecognizer.getChunksEntities(entity.get_Sentence(), "NP");
		for(BioNEREntity chunkEntity : chunkEntityVector)
		{
			//Remove some unreasonable chunk
			if(chunkEntity.getText().contains(" and ") 
					|| chunkEntity.getText().contains(",")
					|| chunkEntity.getText().contains(" as well as ")
					|| chunkEntity.getText().contains("(")
					|| chunkEntity.getText().contains(")")) continue;
			if(entity.getTokenBeginIndex()>=chunkEntity.getTokenBeginIndex()
					&&entity.getTokenEndIndex()<=chunkEntity.getTokenEndIndex())
			{//Find the chunk covers this entity, return the chunk's text.
				return chunkEntity.getText();
			}
		}
		//No covered chunk found, return the entity's text.
		return entity.getText();
	}
	public static BioNEREntity getChunkEntity(BioNEREntity entity)
	{
		Vector<BioNEREntity> chunkEntityVector = chunkRecognizer.getChunksEntities(entity.get_Sentence(), "NP");
		for(BioNEREntity chunkEntity : chunkEntityVector)
		{
			//Remove some unreasonable chunk
			if(chunkEntity.getText().contains(" and ") 
					|| chunkEntity.getText().contains(",")
					|| chunkEntity.getText().contains(" as well as ")
					|| chunkEntity.getText().contains("(")
					|| chunkEntity.getText().contains(")")) continue;
			if(entity.getTokenBeginIndex()>=chunkEntity.getTokenBeginIndex()
					&&entity.getTokenEndIndex()<=chunkEntity.getTokenEndIndex())
			{//Find the chunk covers this entity, return the chunk's text.
				return chunkEntity;
			}
		}
		//No covered chunk found, return the entity's text.
		return entity;
	}
	
	
	public static void main(String[] args) throws IOException
	{
		String dataDir = "../../BC2GN/data/testingData";
		String outputFilename = "../../BC2GN/filterList.txt";
		
		GlobalConfig.ReadConfigFile();
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder(dataDir);
		BioNERDocument[] documents = docBuilder.buildDocuments();
		BioNERProcess[] pipeline = new BioNERProcess[4];
		pipeline[0] = new ProcessImpCRFPP();
		pipeline[1] = new ProcessImpProteinIndexNER();
		pipeline[2] = new ProcessImpProteinABNER();
		pipeline[3] = new ProcessImpBC2GNGMFilter();
		
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFilename));
		for(int i=0; i<documents.length; i++)
		{
			if(!documents[i].getID().equals("12860405")) continue;
			System.out.print("Processing #"+i+"...");
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(documents[i]);
			}
			Vector<String> vector = getFilterList(documents[i]);
			fwriter.write(documents[i].getID());
			fwriter.newLine();
			for(String filterGM : vector)
			{
				fwriter.write(filterGM);
				fwriter.newLine();
			}
			fwriter.newLine();
			System.out.println("Finished!");
		}
		fwriter.close();
	}
}
