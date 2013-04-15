package bioner.application.bc2gn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.GeneIDRerankFeatureBuilder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.normalization.gmclassification.feature.GMExtendedMatcherPattern;
import bioner.normalization.gmclassification.feature.GMWordDistanceFeatureBuilder;
import bioner.normalization.rerank.RerankFeatureBuilder;
import bioner.normalization.rerank.feature.ContainKeywordFeatureBuilder;
import bioner.normalization.rerank.feature.GMCorrectSpeciesMaxDistanceFeatureBuilder;
import bioner.normalization.rerank.feature.GMCorrectSpeciesMinDistanceFeatureBuilder;
import bioner.normalization.rerank.feature.GMCountFeatureBuilder;
import bioner.normalization.rerank.feature.GMEnjuRelatedPluralWordFeatureBuilder;
import bioner.normalization.rerank.feature.GMEnjuRelatedWordsFeatureBuilder;
import bioner.normalization.rerank.feature.GMExtendMatchPatternFeatureBuilder;
import bioner.normalization.rerank.feature.GMHasLabelFeatureBuilder;
import bioner.normalization.rerank.feature.GMIncorrectSpeciesMaxDistanceFeatureBuilder;
import bioner.normalization.rerank.feature.GMIncorrectSpeciesMinDistanceFeatureBuilder;
import bioner.normalization.rerank.feature.GMMatchPatternFeatureBuilder;
import bioner.normalization.rerank.feature.GMWordDistanceBoundBinaryFeatureBuilder;
import bioner.normalization.rerank.feature.GMWordMaxDistanceFeatureBuilder;
import bioner.normalization.rerank.feature.GMWordMinDistanceFeatureBuilder;
import bioner.normalization.rerank.feature.GMWordMinDistanceLevelFeatureBuilder;
import bioner.normalization.rerank.feature.HighestRankFeatureBuilder;
import bioner.normalization.rerank.feature.IsInTitleFeatureBuilder;
import bioner.normalization.rerank.feature.IsSpeciesInTitleFeatureBuilder;
import bioner.normalization.rerank.feature.LuceneSemanticScoreFeatureBuilder;
import bioner.normalization.rerank.feature.MaxFeatureValueFeatureBuilder;
import bioner.normalization.rerank.feature.MaxGMClassificationScoreFeatureBuilder;
import bioner.normalization.rerank.feature.MinFeatureValueFeatureBuilder;
import bioner.normalization.rerank.feature.MinGMTokenNumberFeatureBuilder;
import bioner.normalization.rerank.feature.SynonymCountInDocumentFeatureBuilder;

public class BC2GNGeneIDRerankFeatureBuilder implements
		GeneIDRerankFeatureBuilder {

	
	/**
	 * Get a pipeline made up by a sequence of feature builders.
	 * @return NormalizationPairFeatureBuilder[]
	 */
	public static RerankFeatureBuilder[] getFeatureBuilderPipeline()
	{
		Vector<RerankFeatureBuilder> builderVector = new Vector<RerankFeatureBuilder>();
		/*builderVector.add(new MinFeatureValueFeatureBuilder(0));
		builderVector.add(new MinFeatureValueFeatureBuilder(1));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(2));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(3));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(4));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(5));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(6));
		builderVector.add(new MaxFeatureValueFeatureBuilder(7));
		builderVector.add(new MaxFeatureValueFeatureBuilder(8));
		builderVector.add(new MaxFeatureValueFeatureBuilder(9));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(10));
		builderVector.add(new MaxFeatureValueFeatureBuilder(11));//chromosome
		//builderVector.add(new MaxFeatureValueFeatureBuilder(12));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(13));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(14));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(15));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(16));
		//builderVector.add(new MaxFeatureValueFeatureBuilder(17));
		builderVector.add(new MaxFeatureValueFeatureBuilder(18));*/
		
		//builderVector.add(new HighestRankFeatureBuilder());
		
		//builderVector.add(new ContainKeywordFeatureBuilder());
		//builderVector.add(new MaxGMClassificationScoreFeatureBuilder());
		//builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b(gene|protein)\\b.*"));
		
		builderVector.add(new GMCountFeatureBuilder());
		builderVector.add(new MinGMTokenNumberFeatureBuilder());
		builderVector.add(new IsInTitleFeatureBuilder());
		//builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_CRF));
		//builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_INEXACT_CRF));//10
		builderVector.add(new SynonymCountInDocumentFeatureBuilder());
		builderVector.add(new LuceneSemanticScoreFeatureBuilder());//13
		
		
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\balpha|beta|zeta|gamma|delta\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b(h|Hu|Hum)[A-Z]+.*\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b([rmdy]|me|mel)[A-Z]+.*\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*[IVX]+"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b[A-Z]\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\bp[0-9]+\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*[0-9]+.*"));//20
		builderVector.add(new GMMatchPatternFeatureBuilder(".*[A-Z]+.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b(\\w*[A-Z]\\w*[a-z]\\w*|\\w*[a-z]\\w*[A-Z]\\w*)\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b(\\w*[A-Z]\\w*[0-9]\\w*|\\w*[0-9]\\w*[A-Z]\\w*)\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b(\\w+\\-[0-9])\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*(\\(|\\)).*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*(\\-|\\/|\\\\).*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b([\\-0-9]+[\\.\\,]+[0-9]\\.\\,]+)\\b*"));//28
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b(NF[\\-\\s][kK]appa[\\-\\s]?[Bb]|[ATCGU]+|growth factor|binding protein|adapter protein|[A-Z]+Pase)s?\\b.*"));
		
		/*builderVector.add(new GMWordMaxDistanceFeatureBuilder("\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder("\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder("\\b(a|an)\\b", 9999, 0));//44
		builderVector.add(new GMWordMaxDistanceFeatureBuilder("\\b[a-z]{3,}s\\b", 0, 9999));//45
		builderVector.add(new GMWordMaxDistanceFeatureBuilder("\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));//46
		builderVector.add(new GMWordMaxDistanceFeatureBuilder("\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));//49
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMWordMaxDistanceFeatureBuilder(".*\\b(gene|protein)\\b.*"));
		
		builderVector.add(new GMCorrectSpeciesMaxDistanceFeatureBuilder("9606"));
		builderVector.add(new GMIncorrectSpeciesMaxDistanceFeatureBuilder("9606"));//59*/
		
		/*builderVector.add(new GMWordMinDistanceFeatureBuilder("\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder("\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder("\\b(a|an)\\b", 9999, 0));
		builderVector.add(new GMWordMinDistanceFeatureBuilder("\\b[a-z]{3,}s\\b", 0, 9999));
		builderVector.add(new GMWordMinDistanceFeatureBuilder("\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder("\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));//71
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceFeatureBuilder(".*\\b(gene|protein)\\b.*"));*/
		
		/*builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b(a|an)\\b", 9999, 0));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b[a-z]{3,}s\\b", 0, 9999));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));//71
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder(".*\\b(gene|protein)\\b.*"));*/
		
		/*builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, "\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, "\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, "\\b(a|an)\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, "\\b[a-z]{3,}s\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, "\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, "\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\btranscripts?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b[rR]eceptors|[rR]eceptor\\b.*"));//71
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(0, 2, ".*\\b(gene|protein)\\b.*"));
		
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, "\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, "\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, "\\b(a|an)\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, "\\b[a-z]{3,}s\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, "\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, "\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\btranscripts?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b[rR]eceptors|[rR]eceptor\\b.*"));//71
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(3, 5, ".*\\b(gene|protein)\\b.*"));
		
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, "\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, "\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, "\\b(a|an)\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, "\\b[a-z]{3,}s\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, "\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, "\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\btranscripts?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b[rR]eceptors|[rR]eceptor\\b.*"));//71
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMWordDistanceBoundBinaryFeatureBuilder(6, 10, ".*\\b(gene|protein)\\b.*"));
		builderVector.add(new GMCorrectSpeciesMinDistanceFeatureBuilder("9606"));
		builderVector.add(new GMIncorrectSpeciesMinDistanceFeatureBuilder("9606"));//79*/
		
		/*builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\b(a|an)\\b"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\b([Hh]uman|[Mm]ammalian|[Hh]omo)s?\\b"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\b([Yy]east|[Mm]ouse|[Mm]ice|[Mm]urine|[Ff]ly|[Cc]alf|[Rr]at|[Dd]rosophila|[Vv]irus|coli|[Aa]mphibian|[Rr]eptilian)s?\\b"));
		
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(RNA|mRNA|cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(transporter|translocator|coactivator|activator|regulator|inhibitor|suppressor|promoter|inhibitor)s?\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));//40
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(proteasome|estrogen|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity)s?\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(sequence|site|form|domain|region|channel|pathway|signaling|exon|chain)s?\\b.*"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder(".*\\b(gene|protein)\\b.*"));*/
		builderVector.add(new GMEnjuRelatedPluralWordFeatureBuilder());
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\balpha|beta|zeta|gamma|delta|mu|epsilon\\b"));
		builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\a|an\\b"));
		
		int countThreshold = 4;
		try {
			BufferedReader freader = new BufferedReader(new FileReader("../../BC2GN/RelatedWords.txt"));
			String line;
			while((line=freader.readLine())!=null)
			{
				String[] parts = line.split("\\t+");
				if(parts.length!=2) continue;
				int count = Integer.parseInt(parts[1]);
				if(count<countThreshold) continue;
				builderVector.add(new GMEnjuRelatedWordsFeatureBuilder("\\b"+parts[0]+"\\b"));
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/*builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(pathway|signaling)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\bchains?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\btranscripts?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(RNA|mRNA|tRNA)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(cDNA|DNA)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(regulator)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(inhibitor)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(suppressor)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\btranslocators?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\bactivators?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b[rR]eceptors|[rR]eceptor\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(coactivator|activator)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\btransporters?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(adaptor|adapter)s?\\b.*"));
		//builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(nucleoprotein|oncoprotein|glycoprotein)s?\\b.*"));
		//builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(proteasome)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(estrogen)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(superfamily|family|subfamily|families|superfamilies|subfamilies|group|complex|complexes|cluster)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(sequence|chromosome|element|experiment|syndrome|pull\\-down|Mr|cancer|cell line|cell type)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(cell)s?"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(site)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(form)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(domain)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(region)s?\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(gene|protein)\\b.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b(a|an)\\s.*"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\b[a-z]+s"));
		builderVector.add(new GMExtendMatchPatternFeatureBuilder(".*\\band\\b.*\\b[a-z]+s"));*/
		
		/*String uncertainWordsStr = "transcript|nucleoprotein|oncoprotein|glycoprotein|arrestin|actin|ubiquitin|mucin|urotensin|disintegrin|chromatin|calmodulin|tubulin|immunoglobulin|heparin|transporter|translocator|coactivator|activator|regulator|suppressor|promoter|inhibitor|chromosome|element|experiment|syndrome|pull|hormone|cell|syndrome|activity|sequence|form|domain|region|channel|pathway|signaling|chain|cell|exon|tag|pull|regulation|binding|site|pathway|signaling";
		String[] uncertainWords = uncertainWordsStr.split("\\|");
		for(int i=0; i<uncertainWords.length; i++)
		{
			builderVector.add(new GMWordMaxDistanceFeatureBuilder("\\b+"+uncertainWords[i]+"s?\\b"));
			builderVector.add(new GMWordMinDistanceFeatureBuilder("\\b+"+uncertainWords[i]+"s?\\b"));
		}*/
		
		RerankFeatureBuilder[] pipeline = new RerankFeatureBuilder[builderVector.size()];
		for(int i=0; i<pipeline.length; i++)
		{
			pipeline[i] = builderVector.elementAt(i);
		}
		//pipeline[0] = new MinFeatureValueFeatureBuilder(0);
		/*pipeline[0] = new MinFeatureValueFeatureBuilder(0);
		pipeline[1] = new MaxFeatureValueFeatureBuilder(1);
		pipeline[2] = new MaxFeatureValueFeatureBuilder(2);
		pipeline[3] = new MaxFeatureValueFeatureBuilder(3);
		pipeline[4] = new MaxFeatureValueFeatureBuilder(4);
		pipeline[5] = new MaxFeatureValueFeatureBuilder(5);
		pipeline[6] = new MaxFeatureValueFeatureBuilder(6);
		//pipeline[7] = new MaxFeatureValueFeatureBuilder(7);
		//pipeline[9] = new MaxFeatureValueFeatureBuilder(9);
		//pipeline[8] = new MaxFeatureValueFeatureBuilder(8);
		//pipeline[9] = new MaxFeatureValueFeatureBuilder(9);
		
		//pipeline[7] = new GMCountFeatureBuilder();
		//pipeline[16] = new SpeciesCountFeautureBuilder();
		
		//pipeline[17] = new IsInAbstractFeatureBuilder();
		pipeline[8] = new IsInTitleFeatureBuilder();
		//pipeline[20] = new IsSpeciesInAbstractFeatureBuilder();
		//pipeline[8] = new IsSpeciesInTitleFeatureBuilder();
		pipeline[9] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_INDEX);
		pipeline[10] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_CRF);
		//pipeline[19] = new MaxGMLabelCountFeatureBuilder();
		pipeline[11] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_BC);
		pipeline[12] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_PROTEIN);
		pipeline[13] = new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_DNA);
		pipeline[14] = new HighestRankFeatureBuilder();
		pipeline[15] = new MinGMTokenNumberFeatureBuilder();
		pipeline[16] = new ContainKeywordFeatureBuilder();
		pipeline[17] = new MaxGMClassificationScoreFeatureBuilder();
		pipeline[18] = new GMMatchPatternFeatureBuilder(".*\\b(gene|protein)\\b.*");*/
		return pipeline;
	}
	
	private static RerankFeatureBuilder[] m_pipeline = getFeatureBuilderPipeline();
	
	@Override
	public String[] getFeatures(BioNERDocument document,
			HashMap<String, Vector<BioNEREntity>> map, BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String[] features = new String[m_pipeline.length];
		
		for(int i=0; i<features.length; i++)
		{
			features[i] = m_pipeline[i].extractFeature(document, map, candidate);
		}
		
		return features;
	}

	@Override
	public String[] getWekaAttributeFileHead() {
		// TODO Auto-generated method stub
		String[] lines = new String[m_pipeline.length];
		for(int i=0; i<lines.length; i++)
		{
			lines[i] = "@attribute feautre_"+i+" real";
		}
		return lines;
	}

}
