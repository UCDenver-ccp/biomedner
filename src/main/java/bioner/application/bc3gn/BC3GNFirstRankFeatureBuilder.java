package bioner.application.bc3gn;

import java.util.Vector;

import bioner.normalization.FirstRankFeatureBuilder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.feature.NormalizationPairFeatureBuilder;
import bioner.normalization.feature.PairFeatureBuilderFactory;
import bioner.normalization.feature.builder.AttributeInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.AverageCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.CommonSpeciesFeatureBuilder;
import bioner.normalization.feature.builder.ExtendContextIndexScore;
import bioner.normalization.feature.builder.GMCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.GeneInBracketFeatureBuilder;
import bioner.normalization.feature.builder.GeneOutsideBracketFeatureBuilder;
import bioner.normalization.feature.builder.HasGMIDInDocumentFeatureBuilder;
import bioner.normalization.feature.builder.MinEditDistanceFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInDocumentFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInGeneMention;
import bioner.normalization.feature.builder.SpeciesInParagraphFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInSectionFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesInTitleFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesLinkedFeatureBuilder;
import bioner.normalization.feature.builder.SpeciesNearbyFeatureBuilder;
import bioner.normalization.feature.builder.SymbolEditDistanceFeatureBuilder;
import bioner.normalization.feature.builder.SymbolNumberSimilarityFeatureBuilder;
import bioner.normalization.feature.builder.SynonymCoveredRateFeatureBuilder;
import bioner.normalization.feature.builder.SynonymExactMatchFeatureBuilder;
import bioner.normalization.feature.builder.SynonymsInSentenceFeatureBuilder;
import bioner.normalization.feature.builder.TokenizedIndexScoreFeatureBuilder;
import bioner.normalization.feature.builder.UntokenizedIndexScoreFeatureBuilder;

public class BC3GNFirstRankFeatureBuilder implements FirstRankFeatureBuilder {

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
		
		builderVector.add(new SymbolEditDistanceFeatureBuilder());
		builderVector.add(new MinEditDistanceFeatureBuilder());
		builderVector.add(new ExtendContextIndexScore());
		builderVector.add(new SynonymCoveredRateFeatureBuilder());
		builderVector.add(new GMCoveredRateFeatureBuilder());
		builderVector.add(new AverageCoveredRateFeatureBuilder());
		builderVector.add(new SynonymExactMatchFeatureBuilder());
		builderVector.add(new SymbolNumberSimilarityFeatureBuilder());
		
		/*builderVector.add(new SpeciesInGeneMention());
		builderVector.add(new SpeciesNearbyFeatureBuilder());
		builderVector.add(new SpeciesLinkedFeatureBuilder());
		builderVector.add(new SpeciesInDocumentFeatureBuilder());
		builderVector.add(new SpeciesInTitleFeatureBuilder());
		builderVector.add(new SpeciesInSentenceFeatureBuilder());
		builderVector.add(new SpeciesInParagraphFeatureBuilder());
		builderVector.add(new SpeciesInSectionFeatureBuilder());
		builderVector.add(new CommonSpeciesFeatureBuilder());*/
		
		builderVector.add(new AttributeInSentenceFeatureBuilder("type_of_gene"));
		builderVector.add(new UntokenizedIndexScoreFeatureBuilder());
		builderVector.add(new TokenizedIndexScoreFeatureBuilder());
		builderVector.add(new GeneInBracketFeatureBuilder());
		builderVector.add(new GeneOutsideBracketFeatureBuilder());
		builderVector.add(new SynonymsInSentenceFeatureBuilder());
		builderVector.add(new HasGMIDInDocumentFeatureBuilder());
		
		/*builderVector.add(new SymbolEditDistanceFeatureBuilder());
		builderVector.add(new MinEditDistanceFeatureBuilder());
		builderVector.add(new SpeciesInGeneMention());
		builderVector.add(new SpeciesNearbyFeatureBuilder());
		builderVector.add(new SpeciesLinkedFeatureBuilder());
		builderVector.add(new SpeciesInDocumentFeatureBuilder());
		builderVector.add(new SpeciesInTitleFeatureBuilder());
		builderVector.add(new CommonSpeciesFeatureBuilder());
		builderVector.add(new AttributeInSentenceFeatureBuilder("type_of_gene"));
		builderVector.add(new SymbolNumberSimilarityFeatureBuilder());
		builderVector.add(new UntokenizedIndexScoreFeatureBuilder());
		builderVector.add(new GeneInBracketFeatureBuilder());
		builderVector.add(new GeneOutsideBracketFeatureBuilder());
		builderVector.add(new SynonymsInSentenceFeatureBuilder());
		builderVector.add(new ExtendContextIndexScore());
		builderVector.add(new HasGMIDInDocumentFeatureBuilder());*/
		NormalizationPairFeatureBuilder[] pipeline = new NormalizationPairFeatureBuilder[builderVector.size()];
		for(int i=0; i<pipeline.length; i++)
		{
			pipeline[i] = builderVector.elementAt(i);
		}
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
