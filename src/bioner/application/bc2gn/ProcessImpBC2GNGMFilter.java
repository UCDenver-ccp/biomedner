package bioner.application.bc2gn;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.global.GlobalConfig;
import bioner.normalization.ChunkHeaderRecognizer;
import bioner.normalization.FilterListBuilder;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.normalization.feature.builder.SpeciesNearbyFeatureBuilder;
import bioner.normalization.gmclassification.feature.PosTagStore;
import bioner.process.BioNERProcess;
import bioner.tools.nlptools.CombinedNameRecognizer;
import bioner.tools.nlptools.DocumentChunkRecognizer;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class ProcessImpBC2GNGMFilter implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		
		for(BioNERSentence sentence : document.getAllSentence())
		{
			
			mergeCoveredEntities(sentence, false);
			extendGeneMentionBoundary(sentence);
			
			addFullnameCombinedGM(sentence);
			
			mergeCoveredEntities(sentence, false);
			removePrefix(sentence);
			
			spliteCombinedGM(sentence);
			//filterByPatterns(sentence);
		}
		//filterByFilterList(document);
		/*for(BioNERSentence sentence : document.getAllSentence())
		{
			
			mergeCoveredEntities(sentence, true);
			
			//extendGeneMentionBoundary_chunk(sentence);
			extendGeneMentionBoundary(sentence);
			
			addFullnameCombinedGM(sentence);
			
			mergeCoveredEntities(sentence, false);
			
			//filterChunkBeginWithA(sentence);
			//filterUnreliableGeneMention(sentence);
			
			removePrefix(sentence);
			
			spliteCombinedGM(sentence);
			
			//filterAllLowerCase(sentence);
			filterByPatterns(sentence);
			
			//filterByPatterns_chunk(sentence);
			//filterByPatterns_chunkheader(sentence);
			filterByPlural(sentence);
			
		}
		filterByFilterList(document);*/
		
		//filterByPatternsInExtend_spread(document);
		//filterByPatternsInExtend_nospread(document);
		
		//filterByFullName(document);
		//filterByFullName_chunkheader(document);
		/*HashMap<BioNEREntity, Vector<BioNEREntity>> speciesMap = getSpeciesForGM(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			filterBySpecies(sentence, speciesMap);
		}*/
	}
	
	public void filterByPlural(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		String[] postags = PosTagStore.getPosTag(sentence, sentence.getDocument());
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{
			int endIndex = entity.getTokenEndIndex();
			boolean shouldFilter = false;
			String gmText = entity.getText();
			if(postags[endIndex].equals("NNS") && gmText.endsWith("s"))
			{
				shouldFilter = true;
			}
			if(!shouldFilter) sentence.addEntity(entity);
		}
	}

	public void addFullnameCombinedGM(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(sentence.getDocument());
		HashMap<String, String> combinedNameMap = CombinedNameRecognizer.getCombinedNameMap(sentence.getDocument());
		String sentenceText = sentence.getSentenceText();
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			String gmText = entity.getText();
			String fullname = fullNameMap.get(gmText);
			if(fullname!=null)
			{
				int pos = sentenceText.indexOf(fullname);
				while(pos>=0)
				{
					BioNEREntity newEntity = new BioNEREntity();
					newEntity.set_Sentence(sentence);
					newEntity.set_position(pos, pos+fullname.length()-1);
					newEntity.setLabelVector(entity.getLabelVector());
					sentence.addEntity(newEntity);
					pos = sentenceText.indexOf(fullname, pos+1);
				}
			}
			String combinedName = combinedNameMap.get(gmText);
			if(combinedName!=null)
			{
				int pos = sentenceText.indexOf(combinedName);
				while(pos>=0)
				{
					BioNEREntity newEntity = new BioNEREntity();
					newEntity.set_Sentence(sentence);
					newEntity.set_position(pos, pos+combinedName.length()-1);
					newEntity.setLabelVector(entity.getLabelVector());
					sentence.addEntity(newEntity);
					pos = sentenceText.indexOf(combinedName, pos+1);
				}
			}
		}
	}

	public void spliteCombinedGM(BioNERSentence sentence)
	{
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			String gmText = entity.getText();
			if(!gmText.contains("/")) continue;
			String[] parts = gmText.split("[\\\\\\/]+");
			if(parts.length!=2) continue;
			int begin = entity.get_Begin();
			int end = entity.get_End();
			entity.set_position(begin, begin+parts[0].length()-1);
			BioNEREntity secondEntity = new BioNEREntity();
			secondEntity.set_Sentence(sentence);
			secondEntity.set_position(end-parts[1].length()+1, end);
			secondEntity.setLabelVector(entity.getLabelVector());
			sentence.addEntity(secondEntity);
		}
	}
	
	public void filterByFilterList(BioNERDocument document)
	{
		Vector<String> filterList = FilterListBuilder.getFilterList(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entityArray)
			{
				String gmText = entity.getText();
				boolean shouldFilter = false;
				for(String filterText : filterList)
				{
					if(gmText.equals(filterText) /*|| filterText.contains(gmText)*/)
					{
						shouldFilter = true;
						break;
					}
				}
				if(!shouldFilter) sentence.addEntity(entity);
			}
		}
	}
	
	public void filterChunkBeginWithA(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{
			if(!entity.getText().startsWith("a ") && !entity.getText().startsWith("an "))
					sentence.addEntity(entity);
		}
	}

	private ChunkHeaderRecognizer chunkHeaderRecognizer = new ChunkHeaderRecognizer("../../BC2GN/parsed_data.txt");
	//private String chunkHeaderFilerStr = ".*\\b(receptor|NF[\\-\\s][Kk]appa[\\-\\s]?[Bb]|[Ff]actor|transporter|isoenzyme|proteinosis|immunotoxins|acetylglucosamine|insulin|hydroxylase|phosphatase|glycanase|reticulum|protection|hormone|carboxypeptidase|[Cc]hromosome|flavoprotein|glycoprotein|kinase|channel|molecule|family|superfamily|subfamily|site|domain|region|complex|complexes|homology|proteins|genes|receptors|antigen|mRNA|cDNA|kinases|group|cell|culture|antibody|antibodies|pathway|signaling|transcription|interaction|subunit|type|activation)s?\\b.*";
	private String chunkHeaderFilerStr = ".*\\b(NF[\\-\\s][Kk]appa[\\-\\s]?[Bb]|ligases|exon|terminus|promoter|region|clone|polypeptides|isoform|receptor|subunit|ligand|complement|chain|site|form|domain|autoantigen|antigen|sequence|homolog|homology|homologous|type|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|[fF]actor|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptor|[lL]igand|adaptor|adapter|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|genes|proteins|cDNA|RNA|DNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|histone|collagen|neuron|caspase|kinase|phosphatase|polymerase|coactivator|activator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|binding|bound|containing|docking|transforming|breast|colon|stem|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|brain|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|fetal|chicken|mammalian|human|mouse|mice|yeast|fly|cancer|carcinoma|tumor|obesity|apoptosis|death|growth|maturation|necrosis|signal|repair|survival|stress|division|adhesion|control|excision|fusion|cycle|heat|shock|proteoglycan|core|chemokine|cytokine|potassium|calcium|sodium|retinol|tyrosine|pyruvate|vitamin|glutamate|zinc|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|GTP|low|high|highly|non|heterogeneous|homogeneous|light|heavy|negative|novel|putative|dependent|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|[a-z]+ine[\\s\\-]rich|[a-z]+ant|two|to|by|that|like|a|[tT]he|for|of|and|or|with|in|mobility|programmed|matrix|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)s?\\b.*";
	private Pattern chunkHeaderFilterPattern = Pattern.compile(chunkHeaderFilerStr);
	public void filterByPatterns_chunkheader(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{
			Vector<Integer> headerIndexVector = chunkHeaderRecognizer.getChunkHeaders(sentence, entity.getTokenBeginIndex(), entity.getTokenEndIndex());
			for(Integer index : headerIndexVector)
			{
				String headerWord = sentence.getTokens()[index].getText();
				
				Matcher matcher = chunkHeaderFilterPattern.matcher(headerWord);
				boolean shouldFilter = false;
				if(matcher.matches())
				{
					shouldFilter = true;
					break;
				}
				if(!shouldFilter) sentence.addEntity(entity);
			}
			if(headerIndexVector.isEmpty())
			{
				sentence.addEntity(entity);
			}
		}
	}

	public void filterByFullName_chunkheader(BioNERDocument document) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entityArray)
			{
				String gmText = entity.getText();
				String fullName = fullNameMap.get(gmText);
				
				if(fullName==null)
				{
					String[] words = entity.getText().split("\\s+");
					for(String word : words)
					{
						fullName = fullNameMap.get(word);
						if(fullName!=null) break;
					}
				}
				
				
				if(fullName==null) sentence.addEntity(entity);
				else
				{
					int pos = sentence.getSentenceText().indexOf(fullName);
					if(pos<0) continue;
					
					//Get the token index of the full name
					int beginIndex = sentence.getTokenIndex(pos);
					int endIndex = sentence.getTokenIndex(pos+fullName.length()-1);
					Vector<Integer> headerIndexVector = chunkHeaderRecognizer.getChunkHeaders(sentence, beginIndex, endIndex);
					
					boolean shouldFilter = false;
					for(Integer index : headerIndexVector)
					{
						Matcher matcher = chunkHeaderFilterPattern.matcher(sentence.getTokens()[index].getText());
						if(matcher.matches())
						{
							shouldFilter = true;
						}
					}
					if(!shouldFilter) sentence.addEntity(entity);
				}
			}
		}
	}
	
	public void filterByFullName(BioNERDocument document) {
		// TODO Auto-generated method stub
		HashMap<String, String> fullNameMap = DocumentFullNameRecognizer.getFullNameMap(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entityArray)
			{
				String gmText = entity.getText();
				String fullName = fullNameMap.get(gmText);
				if(fullName==null) sentence.addEntity(entity);
				else
				{
					boolean shouldFilter = false;
					Matcher matcher = chunkFilterPattern.matcher(fullName);
					if(matcher.matches())
					{
						shouldFilter = true;
					}
					if(!shouldFilter) sentence.addEntity(entity);
				}
			}
		}
	}

	private String chunkFilterStr = ".*\\b(NF[\\-\\s][kK]appa[\\-\\s]?[Bb]|[ATCGU]+|growth factor|binding protein|cDNA|adapter protein|complex|family|subfamily|phosphogluconolactonase|[A-Z]+Pase)s?\\b.*";
	private Pattern chunkFilterPattern = Pattern.compile(chunkFilterStr);
	public void filterByPatterns_chunk(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> chunkEntityVector = chunkRecognizer.getChunksEntities(sentence, "NP");
		
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{
			boolean shouldFilter = false;
			for(BioNEREntity chunkEntity : chunkEntityVector)
			{
				if(entity.getTokenBeginIndex()>=chunkEntity.getTokenBeginIndex()
						&&entity.getTokenEndIndex()<=chunkEntity.getTokenEndIndex())
				{
					String chunkText = chunkEntity.getText();
					Matcher matcher = chunkFilterPattern.matcher(chunkText);
					if(matcher.matches())
					{
						shouldFilter = true;
						break;
					}
				}
			}
			if(!shouldFilter) sentence.addEntity(entity);
		}
	}

	private DocumentChunkRecognizer chunkRecognizer = new DocumentChunkRecognizer();
	public void extendGeneMentionBoundary_chunk(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		Vector<BioNEREntity> chunkEntityVector = chunkRecognizer.getChunksEntities(sentence, "NP");
		
		Vector<BioNEREntity> usefulChunkEntityVector = new Vector<BioNEREntity>();
		for(BioNEREntity chunkEntity : chunkEntityVector)
		{
			//Remove some unreasonable chunk
			if(chunkEntity.getText().contains(" and ") 
					|| chunkEntity.getText().contains(",")
					|| chunkEntity.getText().contains(" as well as ")
					|| chunkEntity.getText().contains("(")
					|| chunkEntity.getText().contains(")")) continue;
			int coveredNum = 0;
			
			//Remove the chunks that cover more than one gene mention.
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				if(entity.getTokenBeginIndex()>=chunkEntity.getTokenBeginIndex()
						&&entity.getTokenEndIndex()<=chunkEntity.getTokenEndIndex())
				{
					coveredNum++;
				}
			}
			if(coveredNum!=1) continue;
			usefulChunkEntityVector.add(chunkEntity);
		}
		
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			for(BioNEREntity chunkEntity : usefulChunkEntityVector)
			{
				if(entity.getTokenBeginIndex()>=chunkEntity.getTokenBeginIndex()
						&&entity.getTokenEndIndex()<=chunkEntity.getTokenEndIndex())
				{//Reset the gene mention's boundary to the whole chunk
					entity.setTokenBeginIndex(chunkEntity.getTokenBeginIndex());
					entity.setTokenEndIndex(chunkEntity.getTokenEndIndex());
					break;
				}
			}
		}
	}


	
	
	
	public void filterByPatternsInExtend_nospread(BioNERDocument document) {
		// TODO Auto-generated method stub
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entityArray)
			{
				String extendText = "";
				BioNERToken[] tokens = sentence.getTokens();
				int i=entity.getTokenBeginIndex();
				if(i<0) i=0;
				for(; i<tokens.length && i<entity.getTokenEndIndex()+3; i++)
				{
					extendText += tokens[i].getText()+" ";
				}
				extendText = extendText.trim();
				Pattern pattern = patterns[patterns.length-1];
				Matcher matcher = pattern.matcher(extendText);
				if(!matcher.matches())
				{
					sentence.addEntity(entity);
				}
			}
		}
	}




	public void filterByPatternsInExtend_spread(BioNERDocument document) {
		// TODO Auto-generated method stub
		Vector<String> filterGMVector = new Vector<String>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				String extendText = "";
				BioNERToken[] tokens = sentence.getTokens();
				int i=entity.getTokenBeginIndex();
				if(i<0) i=0;
				for(; i<tokens.length && i<entity.getTokenEndIndex()+3; i++)
				{
					extendText += tokens[i].getText()+" ";
				}
				extendText = extendText.trim();
				Pattern pattern = patterns[patterns.length-1];
				Matcher matcher = pattern.matcher(extendText);
				if(matcher.matches() && !filterGMVector.contains(entity.getText()))
				{
					filterGMVector.add(entity.getText());
				}
			}
		}
		for(BioNERSentence sentence : document.getAllSentence())
		{
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for(BioNEREntity entity : entityArray)
			{
				String gmText = entity.getText();
				boolean shouldFilter = false;
				for(String filterGMText : filterGMVector)
				{
					if(gmText.equals(filterGMText))
					{
						shouldFilter = true;
						break;
					}
				}
				if(!shouldFilter) sentence.addEntity(entity);
			}
		}
	}


	//Pattern extendPattern = Pattern.compile("\\b(superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster|chain|site|form|domain|autoantigen|antigen|sequence|homolog|homology|homologous|type|subtype|motif|transcript|[Ff]ragment|signaling|pathway|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|acid|[fF]actor|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptor|[lL]igand|adaptor|adapter|coactivator|activator|transporter|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|proteasome|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin)s?\\b");
	Pattern extendPattern = Pattern.compile("\\b([0-9]+|[A-Z]|alpha|beta|gamma|delta|zeta|kappa|[IVX]{1,3}|chain)s?\\b");

	public void extendGeneMentionBoundary(BioNERSentence sentence)
	{
		String text = sentence.getSentenceText();
		BioNERToken[] tokens = sentence.getTokens();
		Vector<BioNEREntity> chunkEntityVector = chunkRecognizer.getChunksEntities(sentence, "NP");
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			int begin = entity.get_Begin();
			for(; begin>0; begin--)
			{
				char c = text.charAt(begin-1);
				if(c==' '||c=='.'||c==',') break;
			}
			int end = entity.get_End();
			for(; end < text.length()-1; end++)
			{
				char c = text.charAt(end+1);
				if(c==' '||c=='.'||c==',') break;
				if(c=='-'&&end < text.length()-2)
				{
					int index = sentence.getTokenIndex(end+2);
					String tokenStr = sentence.getTokens()[index].getText();
					if(tokenStr.equals("dependent")
							|| tokenStr.equals("mediated"))
						break;
				}
			}
			int beginIndex = sentence.getTokenIndex(begin);
			int endIndex = sentence.getTokenIndex(end);
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
					for(int i=endIndex+1; i<tokens.length&&i<chunkEntity.getTokenEndIndex(); i++)
					{
						String tokenStr = tokens[i].getText();
						if(tokenStr.matches("\\,|\\.|\\;|\\:|and|or|of|with|by|to|from|without|about|in|at|for|on|before|after|above|bellow|the|this|that|upon"))
							break;
						Matcher matcher = extendPattern.matcher(tokenStr);
						if(matcher.matches())
						{
							endIndex = i;
							break;
						}
					}
					entity.setTokenIndex(beginIndex, endIndex);
				}//if covered by chunk
			}//for chunkEntity
		}
	}
	public void filterAllLowerCase(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{
			if(!entity.getText().matches("[a-z\\s]+"))
			{
				sentence.addEntity(entity);
			}
		}
	}
	public void removePrefix(BioNERSentence sentence) {
		// TODO Auto-generated method stub
	
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			String text = entity.getText().toLowerCase();
			
			if(text.startsWith("a ") && !text.equals("a"))
			{
				entity.setTokenBeginIndex(entity.getTokenBeginIndex()+1);
			}
			if(text.startsWith("the ") && !text.equals("the"))
			{
				entity.setTokenBeginIndex(entity.getTokenBeginIndex()+1);
			}
			if(text.startsWith("human ") && !text.equals("human"))
			{
				entity.setTokenBeginIndex(entity.getTokenBeginIndex()+1);
			}
			if(text.startsWith("and ") && !text.equals("and"))
			{
				entity.setTokenBeginIndex(entity.getTokenBeginIndex()+1);
			}
			
			
		}
	}

	public void filterBySpecies(BioNERSentence sentence, HashMap<BioNEREntity, Vector<BioNEREntity>> speciesMap)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{
			Vector<BioNEREntity> speciesVector = speciesMap.get(entity);
			boolean hasHuman = false;
			boolean hasNonHuman = false;
			for(BioNEREntity speciesEntity : speciesVector)
			{
				if(speciesEntity.hasID("9606")) hasHuman = true;
				else
				{
					hasNonHuman = true;
				}
			}
			if(!hasNonHuman || hasHuman)
			{
				sentence.addEntity(entity);
			}
		}
	}
	
	
	public static HashMap<BioNEREntity, Vector<BioNEREntity>> getSpeciesForGM(BioNERDocument document)
	{
		HashMap<BioNEREntity, Vector<BioNEREntity>> map = new HashMap<BioNEREntity, Vector<BioNEREntity>>();
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		for(BioNERSentence sentence : document.getAllSentence())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				
				Vector<BioNEREntity> nearSpeciesVector = new Vector<BioNEREntity>();
				map.put(entity, nearSpeciesVector);
				for(BioNEREntity speciesEntity : speciesVector)
				{
					if(speciesEntity.get_Sentence()==sentence)
					{
						if(speciesEntity.getTokenEndIndex() < entity.getTokenBeginIndex())
						{
							if(Math.abs(speciesEntity.getTokenEndIndex()-entity.getTokenBeginIndex())<=2) nearSpeciesVector.add(speciesEntity);
						}
						else if(speciesEntity.getTokenBeginIndex() > entity.getTokenEndIndex())
						{
							if(Math.abs(speciesEntity.getTokenBeginIndex() - entity.getTokenEndIndex())<=2) nearSpeciesVector.add(speciesEntity);
						}
					}	
				}
				if(!nearSpeciesVector.isEmpty()) 
				{
					continue;
				}
				
				//Find species in the same sentence.
				for(BioNEREntity speciesEntity : speciesVector)
				{
					if(speciesEntity.get_Sentence()==sentence)
					{
						nearSpeciesVector.add(speciesEntity);
					}	
				}
				if(!nearSpeciesVector.isEmpty())
				{
					continue;
				}
				/*
				//Find species in the title
				for(BioNEREntity speciesEntity : speciesVector)
				{
					if(speciesEntity.get_Sentence()==document.getTitle())
					{
						nearSpeciesVector.add(speciesEntity);
					}
				}*/
			}
		}
		
		
		return map;
	}
	
	
	public void filterUnreliableGeneMention(BioNERSentence sentence)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{//Only keep the gene mentions with 'ita' label or have more than one labels.
			if(entity.getLabelVector().size()>1)
			{
				sentence.addEntity(entity);
			}
			else if(!entity.containLabel(GlobalConfig.ENTITY_LABEL_INDEX))
			{
				sentence.addEntity(entity);
			}
		}
	}
	public boolean isCovered(BioNEREntity entity, BioNEREntity other_entity)
	{
		if(entity.getTokenBeginIndex() < other_entity.getTokenBeginIndex()
				&& entity.getTokenEndIndex() < other_entity.getTokenBeginIndex())
			return false;
		if(entity.getTokenBeginIndex() > other_entity.getTokenEndIndex()
				&& entity.getTokenEndIndex() > other_entity.getTokenEndIndex())
			return false;
		return true;
	}
	public void mergeCoveredEntities(BioNERSentence sentence, boolean intersection)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray)
		{//For each original entity
			//Find an entity in the sentence to cover it
			boolean covered = false;
			for(BioNEREntity entity_in_sentence : sentence.getAllEntities())
			{
				if(isCovered(entity, entity_in_sentence))
				{//When found a entity to cover it
					covered = true;
					//Merge the two entities
					
					int begin;
					int end;
					if(entity.containLabel(GlobalConfig.ENTITY_LABEL_INDEX)|| entity.containLabel("gold"))
					{//For a entity from index ner, only add its label to the entity in sentence.
						if(entity.containLabel("gold"))
							entity_in_sentence.addLabel("gold");
						if(entity.containLabel(GlobalConfig.ENTITY_LABEL_INDEX))
							entity_in_sentence.addLabel(GlobalConfig.ENTITY_LABEL_INDEX);
					}
					else
					{//When it is not a index ner entity, merge it.
						if(intersection)
						{
							//Use the big one as the begin index
							if(entity.getTokenBeginIndex()>entity_in_sentence.getTokenBeginIndex()) begin = entity.getTokenBeginIndex();
							else begin = entity_in_sentence.getTokenBeginIndex();
							//Use the small one as the end index
							if(entity.getTokenEndIndex() < entity_in_sentence.getTokenEndIndex()) end = entity.getTokenEndIndex();
							else end = entity_in_sentence.getTokenEndIndex();
							entity_in_sentence.setTokenIndex(begin, end);
						}
						else
						{
							//Use the small one as the begin index
							if(entity.getTokenBeginIndex()<entity_in_sentence.getTokenBeginIndex()) begin = entity.getTokenBeginIndex();
							else begin = entity_in_sentence.getTokenBeginIndex();
							//Use the big one as the end index
							if(entity.getTokenEndIndex() > entity_in_sentence.getTokenEndIndex()) end = entity.getTokenEndIndex();
							else end = entity_in_sentence.getTokenEndIndex();
							entity_in_sentence.setTokenIndex(begin, end);
						}
						//Add all labels of the entity to the entity already in sentence.
						for(String label : entity.getLabelVector())
						{
							entity_in_sentence.addLabel(label);
						}
					}
					break;
				}
			}
			//If not found covered, add it to the sentence.
			if(!covered)
			{
				sentence.addEntity(entity);
			}
			
		}
	}
	
	
	private static Pattern[] patterns = getPatterns();
	public void filterByPatterns(BioNERSentence sentence)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		
		for(BioNEREntity entity : entityArray)
		{
			String entityText = entity.getText();
			boolean shouldFilterOut = false;
			for(Pattern pattern : patterns)
			{
				Matcher matcher = pattern.matcher(entityText);
				if(matcher.matches())
				{
					shouldFilterOut = true;
					break;
				}
			}
			if(!shouldFilterOut) sentence.addEntity(entity);
		}
	}
	public static Pattern[] getPatterns()
	{
		Pattern[] patterns = new Pattern[1];
		String patternStr = ".*(aa|bp\\s[0-9]{1,2}|kd|mg|Ki|nM|CD|Sci|Proc|Acad|[\\d\\.]+[\\s\\-]?[Kk][Dd][Aa]|or\\sin|and\\s[1Ii]|for\\s4|[Aa]\\sgene|[Aa]t\\s5|[Aa]\\sC|is\\s1|at\\s\\d|factor[\\s\\-]\\d|factor[\\s\\-](alpha|beta|gamma|delta)|receptor\\s\\d|[A-Z]\\receptor|protein\\s[A-Za-z]|protein[\\s\\-][0-9]+|beta[\\s\\-]\\d+|\\d+[\\s\\-]beta([\\s\\-]\\d+)?|alpha[\\s\\-]\\d+|\\d+[\\s\\-]alpha([\\s\\-]\\d+)?|(alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)\\s[A-Za-z0-9]|[A-Za-z0-9][\\s\\-](alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)|(alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)\\schain|[A-Za-z][\\s\\-]protein|[Aa]\\s[0-9]{1,2})$";
		//patterns[0] = Pattern.compile(patternStr);
		patterns[0] = Pattern.compile(".{0,1}|Ca2|Ca\\+|NF[\\-\\s]?[Kk]appa[\\-\\s]?B|Its|All|MR|BLAST|ADP|ATP|GTP|GDP|AP\\-[0-9]+|type (IVXivx)+ receptor|interleukin\\-+\\d+");
		//patternStr = ".*([\\s\\,\\.\\-\\;\\:\\(\\)]|isoform|subunit|ligand|complement|chain|site|form|domain|autoantigen|antigen|sequence|homolog|type|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|[fF]actor|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptor|[lL]igand|adaptor|adapter|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|[pP]rotein|RNA|DNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|histone|collagen|neuron|caspase|kinase|phosphatase|polymerase|coactivator|activator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|binding|bound|containing|docking|transforming|breast|colon|stem|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|brain|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|fetal|chicken|mammalian|human|cancer|carcinoma|tumor|obesity|apoptosis|death|growth|maturation|necrosis|signal|repair|survival|stress|division|adhesion|control|excision|fusion|cycle|heat|shock|proteoglycan|core|chemokine|cytokine|potassium|calcium|sodium|retinol|tyrosine|pyruvate|vitamin|glutamate|zinc|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|GTP|low|high|highly|non|heterogeneous|homogeneous|light|heavy|negative|novel|putative|dependent|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|[a-z]+ine[\\s\\-]rich|[a-z]+ant|two|to|by|that|like|a|[tT]he|for|of|and|or|with|in|mobility|programmed|matrix|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)+('?s)?$";
		//patternStr = ".*\\b(homology|isoform|subunit|ligand|complement|chain|site|form|domain|autoantigen|antigen|sequence|homolog|type|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|regulator|inhibitor|suppressor|translocator|activator|[lL]igand|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|RNA|DNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|histone|collagen|neuron|caspase|kinases|proteins|genes|phosphatase|polymerase|coactivator|activator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|bound|containing|docking|transforming|colon|stem|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|carcinoma|obesity|apoptosis|necrosis|signal|survival|stress|division|adhesion|control|excision|fusion|shock|proteoglycan|core|chemokine|cytokine|retinol|tyrosine|pyruvate|glutamate|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|heterogeneous|homogeneous|putative|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|mobility|programmed|matrix|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)+('?s)?\\b.*";
		//patternStr = ".*\\b(homology|complement|site|form|domain|autoantigen|sequence|homolog|subtype|motif|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|regulator|inhibitor|suppressor|translocator|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|RNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|histone|collagen|neuron|caspase|kinases|proteins|genes|phosphatase|polymerase|coactivator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|induced|coupled|linked|conserved|mediated|expressed|advanced|activating|regulating|signaling|bound|containing|docking|transforming|colon|stem|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|carcinoma|obesity|apoptosis|signal|survival|stress|division|adhesion|control|excision|fusion|shock|proteoglycan|core|cytokine|retinol|pyruvate|glutamate|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|heterogeneous|homogeneous|putative|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|mobility|programmed|matrix|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)+('?s)?\\b.*";
		//patternStr = ".*\\b(isoform|chain|site|subunit|form|autoantigen|subtype|motif|fragment|domain|region|family|ligand|sequence|subfamily|homolog|homology|homologies|superfamily|polymeric|cell|acid|antibody|antibodies|proteins|complex|genes|kinases|antigen|subfamily|group|reagent|RNA|DNA|mRNA|tRNA|cDNA|transcript|complexes|culture|cellline|proteasome|nucleoprotein|oncoprotein|phosphoprotein|histone|collagen|neuron|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|binding|bound|containing|docking|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin)+('?s)?\\b.*";
		//patternStr = ".*\\b(family|superfamily|subfamily|cluster|site|domain|region|complex|complexes|homology|proteins|genes|receptors|antigen|mRNA|cDNA|group|[cC]ell|culture|antibody|antibodies|pathway|signaling|transcription|interaction)s?$";
		//patterns[1] = Pattern.compile(patternStr);
		return patterns;
	}
	
	public static void main(String[] args)
	{
		BioNERSentence sentence = new BioNERSentence("This defines a new SNF2 family consisting of hHel1 and its homologues.", 0);
		BioNEREntity entity = new BioNEREntity();
		entity.set_Sentence(sentence);
		sentence.addEntity(entity);
		entity.setTokenIndex(4, 4);
		System.out.println(entity.getText());
		ProcessImpBC2GNGMFilter filter = new ProcessImpBC2GNGMFilter();
		filter.extendGeneMentionBoundary(sentence);
		filter.filterByPatterns(sentence);
		System.out.println(entity.getText());
		System.out.println(sentence.getAllEntities().length);
	}
}
