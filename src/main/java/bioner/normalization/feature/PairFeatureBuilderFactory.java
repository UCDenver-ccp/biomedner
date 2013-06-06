package bioner.normalization.feature;

import bioner.normalization.feature.builder.AttributeInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.ChromosomeInDocumentFeatureBuilder;
import bioner.normalization.feature.builder.ChromosomeInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.CommonSpeciesFeatureBuilder;
import bioner.normalization.feature.builder.ExtendContextIndexScore;
import bioner.normalization.feature.builder.GeneAbbrevInDocument;
import bioner.normalization.feature.builder.GeneInBracketFeatureBuilder;
import bioner.normalization.feature.builder.GeneOutsideBracketFeatureBuilder;
import bioner.normalization.feature.builder.IndexScoreFeatureBuilder;
import bioner.normalization.feature.builder.IsReceptorFeatureBuilder;
import bioner.normalization.feature.builder.IsSpecificSpeciesFeatureBuilder;
import bioner.normalization.feature.builder.MinEditDistanceFeatureBuilder;
import bioner.normalization.feature.builder.NCBIRankFeatureBuilder;
import bioner.normalization.feature.builder.NoLinkedSpeciesHumanFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInDocumentFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInGeneMention;
import bioner.normalization.feature.builder.SpeciesInParagraphFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInSectionFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInTitleFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesLinkedFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesNearbyFeatureBuilder;
import bioner.normalization.feature.builder.SpecificWordFeatureBuilder;
import bioner.normalization.feature.builder.SymbolEditDistanceFeatureBuilder;
import bioner.normalization.feature.builder.SymbolNumberSimilarityFeatureBuilder;
import bioner.normalization.feature.builder.SynonymNumberSimilarityFeatureBuilder;
import bioner.normalization.feature.builder.SynonymsInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.TokenizedIndexScoreFeatureBuilder;
import bioner.normalization.feature.builder.UntokenizedIndexScoreFeatureBuilder;

public class PairFeatureBuilderFactory {
	
	
	public static String[] getWekaAttributeFileHead()
	{
		String[] lines = new String[15];
		
		lines[0] = "@attribute species_in_gene_mention real";
		lines[1] = "@attribute species_nearby real";
		lines[2] = "@attribute linked_species real";
		lines[3] = "@attribute species_in_document real";
		lines[4] = "@attribute species_in_title real";
		lines[5] = "@attribute is_common_species real";
		lines[6] = "@attribute type_of_gene real";
		lines[7] = "@attribute symbol_edit_distance real";
		lines[8] = "@attribute synonyms_edit_distance real";
		lines[9] = "@attribute symbol_number_similarity real";
		//lines[10] = "@attribute untoken_lucene_score real";
		lines[10] = "@attribute token_lucene_score real";
		lines[11] = "@attribute gene_in_bracket real";
		lines[12] = "@attribute gene_outside_bracket real";
		//lines[14] = "@attribute ncbi_rank real";
		lines[13] = "@attribute synonyms_in_sentence real";
		lines[14] = "@attribute extended_gm_lucene_score real";
		
		return lines;
	}
	
	/**
	 * Get a pipeline made up by a sequence of feature builders.
	 * @return NormalizationPairFeatureBuilder[]
	 */
	public static NormalizationPairFeatureBuilder[] getFeatureBuilderPipeline()
	{
		NormalizationPairFeatureBuilder[] pipeline = new NormalizationPairFeatureBuilder[15];
		pipeline[0] = new SpeciesInGeneMention();
		pipeline[1] = new SpeciesNearbyFeatureBuilder();
		pipeline[2] = new SpeciesLinkedFeatureBuilder();
		pipeline[3] = new SpeciesInDocumentFeatureBuilder();
		pipeline[4] = new SpeciesInTitleFeatureBuilder();
		pipeline[5] = new CommonSpeciesFeatureBuilder();
		pipeline[6] = new AttributeInSentenceFeatureBuilder("type_of_gene");
		pipeline[7] = new SymbolEditDistanceFeatureBuilder();
		pipeline[8] = new MinEditDistanceFeatureBuilder();
		pipeline[9] = new SymbolNumberSimilarityFeatureBuilder();
		pipeline[10] = new UntokenizedIndexScoreFeatureBuilder();
		//pipeline[10] = new TokenizedIndexScoreFeatureBuilder();
		pipeline[11] = new GeneInBracketFeatureBuilder();
		pipeline[12] = new GeneOutsideBracketFeatureBuilder();
		//pipeline[14] = new NCBIRankFeatureBuilder();
		pipeline[13] = new SynonymsInSentenceFeatureBuilder();
		pipeline[14] = new ExtendContextIndexScore();
		return pipeline;
	}
}
