package bioner.application.bc2gn;

import java.util.Vector;

import bioner.normalization.FirstRankFeatureBuilder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.normalization.feature.PairFeatureBuilderFactory;
import bioner.normalization.feature.builder.AttributeInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.AverageCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.ChromosomeInDocumentFeatureBuilder;
import bioner.normalization.feature.builder.ChromosomeInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.CombinedWordFeatureBuilder;
import bioner.normalization.feature.builder.CommonSpeciesFeatureBuilder;
import bioner.normalization.feature.builder.ExtendContextIndexScore;
import bioner.normalization.feature.builder.GMCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.GeneAbbrevInDocument;
import bioner.normalization.feature.builder.GeneInBracketFeatureBuilder;
import bioner.normalization.feature.builder.GeneOutsideBracketFeatureBuilder;
import bioner.normalization.feature.builder.IsReceptorFeatureBuilder;
import bioner.normalization.feature.builder.IsSpecificSpeciesFeatureBuilder;
import bioner.normalization.feature.builder.LuceneSemanticScoreFeatureBuilder;
import bioner.normalization.feature.builder.MinEditDistanceFeatureBuilder;
import bioner.normalization.feature.builder.NCBIRankFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInDocumentFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInGeneMention;
import bioner.normalization.feature.builder.SpeciesInTitleFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesLinkedFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesNearbyFeatureBuilder;
import bioner.normalization.feature.builder.SymbolEditDistanceFeatureBuilder;
import bioner.normalization.feature.builder.SymbolNumberSimilarityFeatureBuilder;
import bioner.normalization.feature.builder.SynonymCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.SynonymsInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.TokenizedIndexScoreFeatureBuilder;
import bioner.normalization.feature.builder.UntokenizedIndexScoreFeatureBuilder;
import bioner.normalization.gmclassification.GMClassificationFeatureBuilder;

public class BC2GNFirstRankFeatureBuilder implements FirstRankFeatureBuilder {

	@Override
	public String[] getFeatures(BioNERCandidate candidate) {
		// TODO Auto-generated method stub
		String[] features = new String[m_pipeline.length];
		
		for(int i=0; i<features.length; i++)
		{
			features[i] = m_pipeline[i].extractFeature(candidate);
		}
		
		return features;
	}
	private static NormalizationPairFeatureBuilder[] m_pipeline = getFeatureBuilderPipeline();
	public static NormalizationPairFeatureBuilder[] getFeatureBuilderPipeline()
	{
		Vector<NormalizationPairFeatureBuilder> builderVector = new Vector<NormalizationPairFeatureBuilder>();
		builderVector.add(new MinEditDistanceFeatureBuilder());
		builderVector.add(new SymbolEditDistanceFeatureBuilder());
		builderVector.add(new SymbolNumberSimilarityFeatureBuilder());
		builderVector.add(new TokenizedIndexScoreFeatureBuilder());
		builderVector.add(new SynonymsInSentenceFeatureBuilder());
		builderVector.add(new LuceneSemanticScoreFeatureBuilder());
		builderVector.add(new CombinedWordFeatureBuilder());
		builderVector.add(new SynonymCoveredRateFeatureBuilder());
		builderVector.add(new GMCoveredRateFeatureBuilder());
		builderVector.add(new AverageCoveredRateFeatureBuilder());
		builderVector.add(new AttributeInSentenceFeatureBuilder("type_of_gene"));
		builderVector.add(new ChromosomeInDocumentFeatureBuilder());
		builderVector.add(new ChromosomeInSentenceFeatureBuilder());
		builderVector.add(new ExtendContextIndexScore());
		builderVector.add(new GeneAbbrevInDocument());
		builderVector.add(new GeneInBracketFeatureBuilder());
		builderVector.add(new GeneOutsideBracketFeatureBuilder());
		builderVector.add(new IsReceptorFeatureBuilder());
		builderVector.add(new NCBIRankFeatureBuilder());
		
		NormalizationPairFeatureBuilder[] pipeline = new NormalizationPairFeatureBuilder[builderVector.size()];
		for(int i=0; i<pipeline.length; i++)
		{
			pipeline[i] = builderVector.elementAt(i);
		}
		/*pipeline[0] = new SpeciesInGeneMention();
		pipeline[1] = new SpeciesNearbyFeatureBuilder();
		pipeline[2] = new SpeciesLinkedFeatureBuilder();
		pipeline[3] = new SpeciesInDocumentFeatureBuilder();
		pipeline[4] = new SpeciesInTitleFeatureBuilder();
		pipeline[14] = new IsSpecificSpeciesFeatureBuilder("9606");
		pipeline[5] = new CommonSpeciesFeatureBuilder();*/
		//pipeline[12] = new GeneInBracketFeatureBuilder();
		//pipeline[13] = new GeneOutsideBracketFeatureBuilder();
		//pipeline[14] = new NCBIRankFeatureBuilder();
		
		//pipeline[0] = new SymbolEditDistanceFeatureBuilder();
		//pipeline[0] = new MinEditDistanceFeatureBuilder();
		//pipeline[1] = new SymbolNumberSimilarityFeatureBuilder();
		//pipeline[2] = new UntokenizedIndexScoreFeatureBuilder();
		//pipeline[2] = new TokenizedIndexScoreFeatureBuilder();
		//pipeline[3] = new GMCoveredRateFeatureBuilder();
		//pipeline[4] = new SynonymCoveredRateFeatureBuilder();
		//pipeline[3] = new AverageCoveredRateFeatureBuilder();
		//pipeline[3] = new SynonymsInSentenceFeatureBuilder();
		//pipeline[4] = new ExtendContextIndexScore();
		//pipeline[9] = new ChromosomeInSentenceFeatureBuilder();
		//pipeline[5] = new ChromosomeInDocumentFeatureBuilder();
		//pipeline[6] = new CombinedWordFeatureBuilder();
		//pipeline[7] = new AttributeInSentenceFeatureBuilder("type_of_gene");
		return pipeline;
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
