package bioner.normalization;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.normalization.data.index.IndexConfig;
import bioner.normalization.data.index.LuceneIndexNER;
import bioner.normalization.feature.builder.SpeciesEntityStore;
import bioner.process.BioNERProcess;
import bioner.tools.NERFactory;
import bioner.tools.NERProcessor;
import bioner.tools.nlptools.CombinedNameRecognizer;
import bioner.tools.nlptools.DocumentFullNameRecognizer;

public class ProcessImpFilterGeneMention implements BioNERProcess {

	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		readGMFilterList("./data/filter/tabulist.txt");
		for (BioNERSentence sentence : document.getAllSentence()) {
			filterBySection(sentence);
			mergeCoveredEntities(sentence, true);
			extendGeneMentionBoundary(sentence);

			addFullnameCombinedGM(sentence);
			mergeCoveredEntities(sentence, false);
			filterUnreliableGeneMention(sentence);
			processSentence(sentence);
			//filterSpeciesInGeneMention(sentence);
			filterGMByList(sentence);
			extendGeneMentionBoundary(sentence);
		}
		filterGMCoveredBySpecies(document);
	}
	
	//filter out the GMs contained by a species name. This kind of GM should be errors, such as cell line names.
	//  This is about confusing species mentions for gene mentions, not about normalization.
	private void filterGMCoveredBySpecies(BioNERDocument document)
	{
		
		Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(document);
		Vector<String> speciesTextVector = new Vector<String>();
		
		//collect all the species names found in the article
		//  TODO: use sets
		for (BioNEREntity speciesEntity : speciesVector) {
			String speciesText = speciesEntity.getText();
			if(!speciesTextVector.contains(speciesText))
				speciesTextVector.add(speciesText);
		}
		
		
		for (BioNERSentence sentence : document.getAllSentence()) {
            //examine the sentences one by one
			BioNEREntity[] entityArray = sentence.getAllEntities();
			sentence.clearEntities();
			for (BioNEREntity entity : entityArray) {
				String entityText = entity.getText();
				boolean shouldFilter = false;
				for (String speciesText : speciesTextVector) {
					if (speciesText.contains(entityText)) {
                        // if the GM is contained by a species name
						shouldFilter = true;
						break;
					}
				}
				if (!shouldFilter) {
                    //The GMs not contained by species names
					sentence.addEntity(entity);
				}
			}
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
	
	private void filterUnreliableGeneMention(BioNERSentence sentence)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		for(BioNEREntity entity : entityArray) {    

            // Only keep the gene mentions with 'ita' label or have more than one labels.
			if (entity.containLabel("ita")) {
				sentence.addEntity(entity);
			} else if (entity.getLabelVector().size()>=2) {

                // keep entity if a concensus of NERs have labelled it
				sentence.addEntity(entity);

                // debug
                if (false) {
                    System.out.println("had enough labels:" + entity.getLabelVector().size());
                    System.out.print("    " + entity.getText() + ": ");
                    for (String l : entity.getLabelVector()) {
                        System.out.print("\"" + l + "\",");
                    }
                    System.out.println("\n");
                }
			}
            else if (false) {
                // debug
                System.out.println("had too few labels:" + entity.getLabelVector().size());
                System.out.print("    " + entity.getText() + ": ");
                for (String l : entity.getLabelVector()) {
                    System.out.print("\"" + l + "\",");
                }
                System.out.println("\n");
            }
		}
	}
	
	private void extendGeneMentionBoundary(BioNERSentence sentence)
	{
		String text = sentence.getSentenceText();
		for(BioNEREntity entity : sentence.getAllEntities())
		{
			int begin = entity.get_Begin();
			for(; begin>0; begin--)
			{
				char c = text.charAt(begin-1);
				if(c==' ') break;
			}
			int end = entity.get_End();
			for(; end < text.length()-1; end++)
			{
				char c = text.charAt(end+1);
				if(c==' ') break;
			}
			entity.set_position(begin, end);
		}
	}
	private void filterBySection(BioNERSentence sentence)
	{
		BioNERSection section = sentence.getSection();

		if (section==null) return;

		String type = section.getType();
		if (type==null) return;

		if (type.contains("material") 
            || type.contains("method") 
            || type.contains("supporting") 
            || type.contains("supplementary")) {

			sentence.clearEntities();
		}
	}

	/******
    private void filterBySection(BioNERSentence sentence)
	{		
		BioNERSection currentSection = sentence.getSection();
		while(currentSection!=null)
		{
			BioNERSentence titleSentence =  currentSection.getTitleSentence();
			if(titleSentence!=null)
			{
				String sectionTitle = titleSentence.getSentenceText().toLowerCase().trim();
				if(sectionTitle.contains("material") || sectionTitle.contains("method") || sectionTitle.contains("supporting") || sectionTitle.contains("supplementary"))
				{
					sentence.clearEntities();
					break;
				}
			}
			String type = currentSection.getType();
			if(type!=null)
			{
				if(type.contains("material") || type.contains("method") || type.contains("supporting") || type.contains("supplementary"))
				{
					sentence.clearEntities();
					break;
				}
			}
			currentSection = currentSection.getParentSection();
		}
	}
    *******/
	
	private void mergeCoveredEntities(BioNERSentence sentence, boolean intersection)
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
					if(entity.containLabel(GlobalConfig.ENTITY_LABEL_INDEX))
					{//For a entity from index ner, only add its label to the entity in sentence.
						entity_in_sentence.addID(GlobalConfig.ENTITY_LABEL_INDEX);
					}
					else
					{//When it is not a index ner entity, merge it.
						if(intersection)
						{
							//Use the big one as the begin index
							if (entity.getTokenBeginIndex()>entity_in_sentence.getTokenBeginIndex()) 
                                begin = entity.getTokenBeginIndex();
							else    
                                begin = entity_in_sentence.getTokenBeginIndex();

							//Use the small one as the end index
							if (entity.getTokenEndIndex() < entity_in_sentence.getTokenEndIndex()) 
                                end = entity.getTokenEndIndex();
							else 
                                end = entity_in_sentence.getTokenEndIndex();
							entity_in_sentence.setTokenIndex(begin, end);
						}
						else
						{
							//Use the small one as the begin index
							if (entity.getTokenBeginIndex()<entity_in_sentence.getTokenBeginIndex()) 
                                begin = entity.getTokenBeginIndex();
							else 
                                begin = entity_in_sentence.getTokenBeginIndex();

							//Use the big one as the end index
							if (entity.getTokenEndIndex() > entity_in_sentence.getTokenEndIndex()) 
                                end = entity.getTokenEndIndex();
							else 
                                end = entity_in_sentence.getTokenEndIndex();
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
	private boolean isCovered(BioNEREntity entity, BioNEREntity other_entity)
	{
		if(entity.getTokenBeginIndex() < other_entity.getTokenBeginIndex()
				&& entity.getTokenEndIndex() < other_entity.getTokenBeginIndex())
			return false;
		if(entity.getTokenBeginIndex() > other_entity.getTokenEndIndex()
				&& entity.getTokenEndIndex() > other_entity.getTokenEndIndex())
			return false;
		return true;
	}
	
	
	private Pattern m_GMFilterPattern = null;
	private void readGMFilterList(String filename)
	{
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line = freader.readLine();
			StringBuffer sb = new StringBuffer(line);
			while ((line=freader.readLine()) != null) {
				sb.append("|");
				sb.append(line);
			}
			freader.close();
			m_GMFilterPattern = Pattern.compile(sb.toString());
		} catch (FileNotFoundException e) {
            System.err.println("error on ProcessImpFilterGeneMention.readGMFilterList() " + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
            System.err.println("error on ProcessImpFilterGeneMention.readGMFilterList() " + e);
			e.printStackTrace();
            throw new RuntimeException(e);
		}
	}
	private void filterGMByList(BioNERSentence sentence)
	{
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		
		for(BioNEREntity entity : entityArray)
		{
			String gmStr = entity.getText();
			Matcher matcher = m_GMFilterPattern.matcher(gmStr);
			if (!matcher.matches()) {
				sentence.addEntity(entity);
			}
            else if (false) {
                System.out.println("ProcessImpFilterGeneMention.filterGMByLIst() dropping mention \"" + gmStr 
                    + "\" readGMFilterList(\"./data/filter/tabulist.txt\")" );
            }
		}
	}
	
	
	private static Pattern[] patterns = getPatterns();

	private void processSentence(BioNERSentence sentence)
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
                    //System.err.println("ProcessImpFilterGeneMention.processSentence()  removing entity:" + entityText );
					break;
				}
			}
			if (!shouldFilterOut) {
                sentence.addEntity(entity);
            }
		}
	}
	
	private NERProcessor m_speciesNER = NERFactory.getSpeciesIndexNER();
	private Pattern m_speciesPattern = Pattern.compile("\\b([A-Z][a-z]{0,2}\\.\\s?[a-z]+|(in|ex)\\W?(vivo|vitro)|((T|t)able|(F|f)igure\\W?\\w+)|[ATGCUatgcu]+)\\b");
	private void filterSpeciesInGeneMention(BioNERSentence sentence)
	{
		String sentenceStr = sentence.getSentenceText();
		BioNEREntity[] speciesEntities = m_speciesNER.recognizeSentence(sentence);
		Vector<String> speciesVector = new Vector<String>();
		for(BioNEREntity speciesEntity : speciesEntities)
		{
			String speciesStr = speciesEntity.getText();
			if(!speciesVector.contains(speciesStr))
			{
				speciesVector.add(speciesStr);
			}
		}
		
		Matcher matcher = m_speciesPattern.matcher(sentenceStr);
		while(matcher.find())
		{
			String speciesStr = sentenceStr.substring(matcher.start(), matcher.end());
			if(!speciesVector.contains(speciesStr))
			{
				speciesVector.add(speciesStr);
			}
		}
		
		BioNEREntity[] entityArray = sentence.getAllEntities();
		sentence.clearEntities();
		
		for(BioNEREntity entity : entityArray)
		{
			String geneMentionStr = entity.getText();
			for(String speciesStr : speciesVector)
			{
				int pos = geneMentionStr.lastIndexOf(speciesStr);
				if(pos>=0)
				{
					geneMentionStr = geneMentionStr.substring(pos+speciesStr.length()).trim();
				}
			}
			if(geneMentionStr.length()>0)
			{
				int begin = sentenceStr.lastIndexOf(geneMentionStr, entity.get_End());
				int end = begin + geneMentionStr.length() -1;
				entity.set_position(begin, end);
				sentence.addEntity(entity);
			}
		}
	}
	
	private static Pattern[] getPatterns()
	{
		Pattern[] patterns = new Pattern[2];

		//String patternStr1 = ".*(aa|bp\\s[0-9]{1,2}|kd|mg|Ki|nM|CD|Sci|Proc|Acad|[\\d\\.]+[\\s\\-]?[Kk][Dd][Aa]|or\\sin|and\\s[1Ii]|for\\s4|[Aa]\\sgene|[Aa]t\\s5|[Aa]\\sC|is\\s1|at\\s\\d|factor[\\s\\-]\\d|factor[\\s\\-](alpha|beta|gamma|delta)|receptor\\s\\d|[A-Z]\\receptor|protein\\s[A-Za-z]|protein[\\s\\-][0-9]+|beta[\\s\\-]\\d+|\\d+[\\s\\-]beta([\\s\\-]\\d+)?|alpha[\\s\\-]\\d+|\\d+[\\s\\-]alpha([\\s\\-]\\d+)?|(alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)\\s[A-Za-z0-9]|[A-Za-z0-9][\\s\\-](alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)|(alpha|beta|gamma|delta|epsilon|eta|kappa|lambda)\\schain|[A-Za-z][\\s\\-]protein|[Aa]\\s[0-9]{1,2})$";
		//patterns[0] = Pattern.compile(patternStr1);
		patterns[0] = Pattern.compile(".{0,1}");

		//String patternStr2 = ".*([\\s\\,\\.\\-\\;\\:\\(\\)]|isoform|subunit|ligand|complement|chain|site|form|domain|autoantigen|antigen|sequence|homolog|type|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|[fF]actor|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptor|[lL]igand|adaptor|adapter|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|[pP]rotein|RNA|DNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|histone|collagen|neuron|caspase|kinase|phosphatase|polymerase|coactivator|activator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|binding|bound|containing|docking|transforming|breast|colon|stem|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|brain|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|fetal|chicken|mammalian|human|cancer|carcinoma|tumor|obesity|apoptosis|death|growth|maturation|necrosis|signal|repair|survival|stress|division|adhesion|control|excision|fusion|cycle|heat|shock|proteoglycan|core|chemokine|cytokine|potassium|calcium|sodium|retinol|tyrosine|pyruvate|vitamin|glutamate|zinc|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|GTP|low|high|highly|non|heterogeneous|homogeneous|light|heavy|negative|novel|putative|dependent|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|[a-z]+ine[\\s\\-]rich|[a-z]+ant|two|to|by|that|like|a|[tT]he|for|of|and|or|with|in|mobility|programmed|matrix|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive)+('?s)?$";
		String patternStr3 = ".*([\\s\\,\\.\\-\\;\\:\\(\\)]|domain|region|family|ligand|sequence|homolog|superfamily|polymeric|cell|acid|antibody|antibodies|proteins|complex|genes|antigen|subfamily|group|reagent|et al)+('?s)?$";
		patterns[1] = Pattern.compile(patternStr3);

		return patterns;
	}
	
	public static void main(String[] args)
	{
		Pattern[] patterns = getPatterns();
		String str = "ABC domain abc";
		Matcher matcher = patterns[0].matcher(str);
		if(matcher.matches()) System.out.println("Match!");
		else System.out.println("Not Match!");
		
		BioNERSentence sentence = new BioNERSentence("Drosophila abc", 0);
		BioNEREntity entity = new BioNEREntity();
		entity.set_Sentence(sentence);
		entity.setTokenIndex(0, 0);
		sentence.addEntity(entity);
		BioNERDocument document = new BioNERDocument();
		document.setTitle(sentence);
		ProcessImpFilterGeneMention process = new ProcessImpFilterGeneMention();
		process.Process(document);
	}

}
