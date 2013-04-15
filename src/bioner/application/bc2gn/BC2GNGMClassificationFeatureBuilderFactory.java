package bioner.application.bc2gn;


import java.util.Vector;

import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilderFactory;
import bioner.normalization.gmclassification.feature.GMCandidadateHighestScoreFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMContainsKeywordFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMContainsLabelFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMContainsStringFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMCorrectSpeciesDistanceFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMCountFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMExtendedMatcherPattern;
import bioner.normalization.gmclassification.feature.GMFullnameHaveSpeceialNameFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMFullnameHeaderFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMFullnameHeaderPluralFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMFullnameHeaderStrFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMFullnameLastWordStrFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMFullnameMatchPatternFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMHaveSpecialWordFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMHeaderPluralFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMHeaderWordFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMHeaderWordStrFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMIncorrectSpeciesDistanceFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMLastWordStrFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMSameGMExtendBeforeMatchPatternFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMWordDistanceFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMWordNumFeatureBuilder;
import bioner.normalization.gmclassification.feature.MaxRankFeatureValueFeatureBuilder;
import bioner.normalization.gmclassification.feature.MinRankFeatureValueFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMMatchPatternFeatureBuilder;


public class BC2GNGMClassificationFeatureBuilderFactory implements GMClassificationFeatureBuilderFactory{
	@Override
	public String[] getWekaAttributeFileHead()
	{
		String[] lines = new String[m_pipeline.length];
		for(int i=0; i<lines.length; i++)
		{
			lines[i] = "@attribute feautre_"+i+" "+m_pipeline[i].getDataType();
		}
		return lines;
	}
	
	/**
	 * Get a pipeline made up by a sequence of feature builders.
	 * @return NormalizationPairFeatureBuilder[]
	 */
	public static GMClassificationFeatureBuilder[] getFeatureBuilderPipeline()
	{
		Vector<GMClassificationFeatureBuilder> builderVector = new Vector<GMClassificationFeatureBuilder>();
		
		builderVector.add(new MinRankFeatureValueFeatureBuilder(0));
		builderVector.add(new MinRankFeatureValueFeatureBuilder(1));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(3));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(7));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(8));
		
		
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\balpha|beta|zeta|gamma|delta|kappa\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder("(h|Hu|Hum)[A-Z]+.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder("[a-gi-z][A-Z]+.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*[IVX]+"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*[0-9]+.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*[A-Z]+.*"));
		
		builderVector.add(new GMWordDistanceFeatureBuilder("\\b(superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|complex|complexes|cluster)s?\\b"));
		builderVector.add(new GMWordDistanceFeatureBuilder("\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordDistanceFeatureBuilder("\\b(a|an)\\b", 9999, 0));
		builderVector.add(new GMWordDistanceFeatureBuilder("\\b[a-z]{3,}s\\b", 0, 9999));
		builderVector.add(new GMWordDistanceFeatureBuilder("\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));
		builderVector.add(new GMWordDistanceFeatureBuilder("\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMWordDistanceFeatureBuilder(".*\\b(gene|protein)\\b.*"));
		
		
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|complex|complexes|cluster)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*[a-z]{3,}s"));
		
		builderVector.add(new GMCorrectSpeciesDistanceFeatureBuilder("9606"));
		builderVector.add(new GMIncorrectSpeciesDistanceFeatureBuilder("9606"));
		builderVector.add(new GMCountFeatureBuilder());
		builderVector.add(new GMContainsLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_CRF));
		GMClassificationFeatureBuilder[] pipeline = new GMClassificationFeatureBuilder[builderVector.size()];
		for(int i=0; i<builderVector.size(); i++)
		{
			pipeline[i] = builderVector.elementAt(i);
		}
		return pipeline;
	}
	/*public static GMClassificationFeatureBuilder[] getFeatureBuilderPipeline()
	{
		
		String wordsStr = "isoform|receptor|subunit|ligand|complement|chain|site|form|domain|autoantigen|antigen|sequence|homolog|homology|homologous|type|subtype|motif|group|candidate|molecule|superfamily|family|subfamily|transcript|[Ff]ragment|[fF]actor|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptor|[lL]igand|adaptor|adapter|nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|[pP]rotein|gene|genes|proteins|cDNA|RNA|DNA|dna|rna|mRNA|mrna|mRna|tRNA|tRna|trna|histone|collagen|neuron|caspase|kinase|phosphatase|polymerase|coactivator|activator|transporter|[eE]xpression|activation|transduction|transcription|adhesion|interaction|[aA]ssociated|induced|coupled|related|linked|associated|conserved|mediated|expressed|advanced|activating|regulating|signaling|binding|bound|containing|docking|transforming|breast|colon|stem|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|brain|epidermal|ectodermal|vesicle|mitochondrial|pancreatic|ubiquitous|fetal|chicken|mammalian|human|mouse|mice|yeast|fly|cancer|carcinoma|tumor|obesity|apoptosis|death|growth|maturation|necrosis|signal|repair|survival|stress|division|adhesion|control|excision|fusion|cycle|heat|shock|proteoglycan|core|chemokine|cytokine|potassium|calcium|sodium|retinol|tyrosine|pyruvate|vitamin|glutamate|zinc|estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin|GTP|low|high|highly|non|heterogeneous|homogeneous|light|heavy|negative|novel|putative|dependent|accessory|peripheral|regulatory|deficient|terminal|transcriptional|inducible|soluble|dual|specificity|specific|nucleic|acid|putative|peroxisomal|basic|[a-z]+ine[\\s\\-]rich|[a-z]+ant|two|to|by|that|like|a|[tT]he|for|of|and|or|with|in|mobility|programmed|matrix|channel|end|ciliary|neurotrophic|retinoid|germinal|center|neural|finger|[Aa]ntigen|lymphocyte|cytoplasmic|helicase|retinoic|acid|plasminogen|cytoskeletal|anchor|[Aa]nti|integral|membrane|[Nn]eutrophil|ubiquitin|basic|leucine|zipper|putative|transmembrane|proteasome|responsive";
		String[] words = wordsStr.split("\\|");
		Vector<GMClassificationFeatureBuilder> builderVector = new Vector<GMClassificationFeatureBuilder>();
		builderVector.add(new GMWordNumFeatureBuilder());
		builderVector.add(new GMContainsKeywordFeatureBuilder());
		builderVector.add(new GMCandidadateHighestScoreFeatureBuilder());
		builderVector.add(new GMContainsStringFeatureBuilder("-"));
		builderVector.add(new GMContainsStringFeatureBuilder("/"));
		builderVector.add(new GMCountFeatureBuilder());
		builderVector.add(new GMHeaderPluralFeatureBuilder());
		builderVector.add(new GMFullnameHeaderPluralFeatureBuilder());
		builderVector.add(new GMContainsLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_CRF));
		builderVector.add(new MinRankFeatureValueFeatureBuilder(0));
		builderVector.add(new MinRankFeatureValueFeatureBuilder(1));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(2));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(3));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(4));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(5));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(6));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(7));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(8));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(9));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(10));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(11));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(12));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(13));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(14));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(15));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(16));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(17));
		builderVector.add(new MaxRankFeatureValueFeatureBuilder(18));
		//builderVector.add(new GMHeaderWordStrFeatureBuilder());
		//builderVector.add(new GMLastWordStrFeatureBuilder());
		//builderVector.add(new GMFullnameHeaderStrFeatureBuilder());
		//builderVector.add(new GMFullnameLastWordStrFeatureBuilder());
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\balpha|beta|zeta|gamma|delta|kappa\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*[0-9]+.*"));
		
		String sureErrorEndWordStr = "element|experiment|syndrome|region|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster|site|form|domain|sequence|homolog|homology|homologous|subtype|motif||signaling|pathway|cell";
		//builderVector.add(new GMExtendedMatcherPattern(".*\\b"+sureErrorEndWordStr+"s?"));
		String sureErrorContainWordStr = "chromosome|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster|homolog|homology|homologous";
		//builderVector.add(new GMExtendedMatcherPattern(".*\\b"+sureErrorContainWordStr+"\\b.*"));
		
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(pathway|signaling)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\bchains?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(RNA|mRNA)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(regulator|inhibitor|suppressor)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\btranslocators?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\bactivators?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(coactivator|activator)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\btransporters?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(proteasome)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(estrogen)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(sequence|chromosome|element|experiment|syndrome|pull\\-down)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(site|form|domain|region)s?\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*\\b(gene|protein)\\b.*"));
		builderVector.add(new GMExtendedMatcherPattern("(a|an)\\s.*"));
		builderVector.add(new GMExtendedMatcherPattern(".*[a-z]{3,}s"));
		
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(pathway|signaling)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\bchains?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(RNA|mRNA)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(regulator|inhibitor|suppressor)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\btranslocators?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\bactivators?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(coactivator|activator)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\btransporters?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(proteasome)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(estrogen)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(sequence|chromosome|element|experiment|syndrome|pull\\-down)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(site|form|domain|region)s?\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder(".*\\b(gene|protein)\\b.*"));
		builderVector.add(new GMSameGMExtendBeforeMatchPatternFeatureBuilder("(a|an)\\s.*"));
		
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(pathway|signaling)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\bchains?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(RNA|mRNA)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(regulator|inhibitor|suppressor)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\btranslocators?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\bactivators?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(coactivator|activator)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\btransporters?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(proteasome)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(estrogen)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(sequence|chromosome|element|experiment|syndrome|pull\\-down)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(site|form|domain|region)s?\\b.*"));
		builderVector.add(new GMFullnameMatchPatternFeatureBuilder(".*\\b(gene|protein)\\b.*"));
		/*wordsStr = "superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|complex|complexes|cluster";
		builderVector.add(new GMHeaderWordFeatureBuilder(wordsStr));
		builderVector.add(new GMHaveSpecialWordFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHaveSpeceialNameFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHeaderFeatureBuilder(wordsStr));
		
		wordsStr = "chain|site|form|domain|autoantigen|antigen|sequence|homolog|homology|homologous|type|subtype|motif|transcript|[Ff]ragment|signaling|pathway|cell|muscle|cellular|extracellular|intestinal|nuclear|surface|membrane|acid";
		builderVector.add(new GMHeaderWordFeatureBuilder(wordsStr));
		builderVector.add(new GMHaveSpecialWordFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHaveSpeceialNameFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHeaderFeatureBuilder(wordsStr));
		
		wordsStr = "[fF]actor|regulator|inhibitor|suppressor|translocator|activator|[rR]eceptor|[lL]igand|adaptor|adapter|coactivator|activator|transporter";
		builderVector.add(new GMHeaderWordFeatureBuilder(wordsStr));
		builderVector.add(new GMHaveSpecialWordFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHaveSpeceialNameFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHeaderFeatureBuilder(wordsStr));
		
		wordsStr = "nucleoprotein|oncoprotein|phosphoprotein|glycoprotein|proteasome";
		builderVector.add(new GMHeaderWordFeatureBuilder(wordsStr));
		builderVector.add(new GMHaveSpecialWordFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHaveSpeceialNameFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHeaderFeatureBuilder(wordsStr));
		
		wordsStr = "estrogen|thrombin|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|activin|chromatin|calmodulin|tubulin|cyclin|immunoglobulin|heparin";
		builderVector.add(new GMHeaderWordFeatureBuilder(wordsStr));
		builderVector.add(new GMHaveSpecialWordFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHaveSpeceialNameFeatureBuilder(wordsStr));
		builderVector.add(new GMFullnameHeaderFeatureBuilder(wordsStr));*/
		
		/*for(String word : words)
		{
			builderVector.add(new GMHeaderWordFeatureBuilder(word));
			builderVector.add(new GMHaveSpecialWordFeatureBuilder(word));
			builderVector.add(new GMFullnameHaveSpeceialNameFeatureBuilder(word));
			builderVector.add(new GMFullnameHeaderFeatureBuilder(word));
		}*/
		
		
		/*GMClassificationFeatureBuilder[] pipeline = new GMClassificationFeatureBuilder[builderVector.size()];
		for(int i=0; i<builderVector.size(); i++)
		{
			pipeline[i] = builderVector.elementAt(i);
		}
		return pipeline;
	}*/
	
	public static GMClassificationFeatureBuilder[] m_pipeline = getFeatureBuilderPipeline();
	@Override
	public String[] getFeatures(BioNEREntity entity)
	{
		String[] features = new String[m_pipeline.length];
		
		for(int i=0; i<features.length; i++)
		{
			features[i] = m_pipeline[i].extractFeature(entity);
		}
		
		return features;
	}
}
