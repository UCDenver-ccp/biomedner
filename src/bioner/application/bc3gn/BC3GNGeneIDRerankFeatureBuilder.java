package bioner.application.bc3gn;

import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.GeneIDRerankFeatureBuilder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.RerankFeatureBuilder;
import bioner.normalization.rerank.feature.ContainKeywordFeatureBuilder;
import bioner.normalization.rerank.feature.GMCountFeatureBuilder;
import bioner.normalization.rerank.feature.GMCountRateFeatureBuilder;
import bioner.normalization.rerank.feature.GMExactMatchFeatureBuilder;
import bioner.normalization.rerank.feature.GMHasFullnameFeatureBuilder;
import bioner.normalization.rerank.feature.GMHasLabelFeatureBuilder;
import bioner.normalization.rerank.feature.GMInSectionFeatureBuilder;
import bioner.normalization.rerank.feature.GMInSectionNumFeatureBuilder;
import bioner.normalization.rerank.feature.GMMatchPatternFeatureBuilder;
import bioner.normalization.rerank.feature.GMSingleCountFeatureBuilder;
import bioner.normalization.rerank.feature.GMWordDistanceBoundBinaryFeatureBuilder;
import bioner.normalization.rerank.feature.GMWordMinDistanceLevelFeatureBuilder;
import bioner.normalization.rerank.feature.HasGMIDInDocumentFeatureBuilder;
import bioner.normalization.rerank.feature.HighestRankFeatureBuilder;
import bioner.normalization.rerank.feature.IsInAbstractFeatureBuilder;
import bioner.normalization.rerank.feature.IsInTitleFeatureBuilder;
import bioner.normalization.rerank.feature.IsSpeciesInAbstractFeatureBuilder;
import bioner.normalization.rerank.feature.IsSpeciesInTitleFeatureBuilder;
import bioner.normalization.rerank.feature.MaxFeatureValueFeatureBuilder;
import bioner.normalization.rerank.feature.MaxGMLabelCountFeatureBuilder;
import bioner.normalization.rerank.feature.MinFeatureValueFeatureBuilder;
import bioner.normalization.rerank.feature.MinGMTokenNumberFeatureBuilder;
import bioner.normalization.rerank.feature.SpeciesCountFeautureBuilder;
import bioner.normalization.rerank.feature.SpeciesCountRateFeautureBuilder;
import bioner.normalization.rerank.feature.SynonymCountInDocumentFeatureBuilder;

public class BC3GNGeneIDRerankFeatureBuilder implements
		GeneIDRerankFeatureBuilder {

	
	/**
	 * Get a pipeline made up by a sequence of feature builders.
	 * @return NormalizationPairFeatureBuilder[]
	 */
	public static RerankFeatureBuilder[] getFeatureBuilderPipeline()
	{
		Vector<RerankFeatureBuilder> builderVector = new Vector<RerankFeatureBuilder>();
		builderVector.add(new MinFeatureValueFeatureBuilder(0));
		builderVector.add(new MinFeatureValueFeatureBuilder(1));
		builderVector.add(new MaxFeatureValueFeatureBuilder(2));
		builderVector.add(new MaxFeatureValueFeatureBuilder(3));
		builderVector.add(new MaxFeatureValueFeatureBuilder(4));
		builderVector.add(new MaxFeatureValueFeatureBuilder(5));
		builderVector.add(new MaxFeatureValueFeatureBuilder(6));
		builderVector.add(new MaxFeatureValueFeatureBuilder(7));
		builderVector.add(new MaxFeatureValueFeatureBuilder(8));
		builderVector.add(new MaxFeatureValueFeatureBuilder(9));
		builderVector.add(new MaxFeatureValueFeatureBuilder(10));
		builderVector.add(new MaxFeatureValueFeatureBuilder(11));
		builderVector.add(new MaxFeatureValueFeatureBuilder(12));
		builderVector.add(new MaxFeatureValueFeatureBuilder(13));
		builderVector.add(new MaxFeatureValueFeatureBuilder(14));
		/*builderVector.add(new MaxFeatureValueFeatureBuilder(15));
		builderVector.add(new MaxFeatureValueFeatureBuilder(16));
		builderVector.add(new MaxFeatureValueFeatureBuilder(17));
		builderVector.add(new MaxFeatureValueFeatureBuilder(18));
		builderVector.add(new MaxFeatureValueFeatureBuilder(19));
		builderVector.add(new MaxFeatureValueFeatureBuilder(20));
		builderVector.add(new MaxFeatureValueFeatureBuilder(21));
		builderVector.add(new MaxFeatureValueFeatureBuilder(22));
		builderVector.add(new MaxFeatureValueFeatureBuilder(23));*/
		
		
		builderVector.add(new HasGMIDInDocumentFeatureBuilder());
		builderVector.add(new SynonymCountInDocumentFeatureBuilder());
		builderVector.add(new GMCountFeatureBuilder());
		builderVector.add(new GMCountRateFeatureBuilder());
		builderVector.add(new GMSingleCountFeatureBuilder());
		builderVector.add(new GMHasFullnameFeatureBuilder());
		builderVector.add(new GMExactMatchFeatureBuilder());
		builderVector.add(new HighestRankFeatureBuilder());
		builderVector.add(new MinGMTokenNumberFeatureBuilder());
		builderVector.add(new ContainKeywordFeatureBuilder());
		builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ITATIC));
		builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_CRF));
		builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_IDGM));
		builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_BC));
		builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_PROTEIN));
		builderVector.add(new GMHasLabelFeatureBuilder(GlobalConfig.ENTITY_LABEL_ABNER_DNA));
		//builderVector.add(new MaxGMLabelCountFeatureBuilder());
		
		
		builderVector.add(new SpeciesCountFeautureBuilder());
		builderVector.add(new SpeciesCountRateFeautureBuilder());
		builderVector.add(new IsSpeciesInAbstractFeatureBuilder());
		builderVector.add(new IsSpeciesInTitleFeatureBuilder());
		
		
		
		
		
		builderVector.add(new IsInAbstractFeatureBuilder());
		builderVector.add(new IsInTitleFeatureBuilder());
		builderVector.add(new GMInSectionFeatureBuilder("introduction|background"));
		builderVector.add(new GMInSectionFeatureBuilder("method"));
		builderVector.add(new GMInSectionFeatureBuilder("result"));
		builderVector.add(new GMInSectionFeatureBuilder("discussion"));
		builderVector.add(new GMInSectionFeatureBuilder("conclusion"));
		builderVector.add(new GMInSectionFeatureBuilder("figure|table|image"));
		builderVector.add(new GMInSectionFeatureBuilder("abbreviation"));
		builderVector.add(new GMInSectionFeatureBuilder("acknowledgement"));
		builderVector.add(new GMInSectionNumFeatureBuilder("introduction|background"));
		builderVector.add(new GMInSectionNumFeatureBuilder("method"));
		builderVector.add(new GMInSectionNumFeatureBuilder("result"));
		builderVector.add(new GMInSectionNumFeatureBuilder("discussion"));
		builderVector.add(new GMInSectionNumFeatureBuilder("conclusion"));
		builderVector.add(new GMInSectionNumFeatureBuilder("figure|table|image"));
		builderVector.add(new GMInSectionNumFeatureBuilder("abbreviation"));
		builderVector.add(new GMInSectionNumFeatureBuilder("acknowledgement"));
		builderVector.add(new GMInSectionNumFeatureBuilder("material|method|supporting|supplementary"));
		
		/*builderVector.add(new GMMatchPatternFeatureBuilder(".*\\balpha|beta|zeta|gamma|delta\\b.*"));
		builderVector.add(new GMMatchPatternFeatureBuilder(".*\\b([rmdyh]|Hu|Hum|me|mel)[A-Z]+.*\\b.*"));
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
		
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b(complex|complexes|superfamily|family|subfamily|families|superfamilies|subfamilies|genes|proteins|group|subgroup|cluster)s?\\b"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b(domain|channel|region|activity|antibody|regulation|promoter|binding|site|pathway|signaling|transporter|hormone|inhibitor|cell|syndrome|exon|tag|pull)s?\\b"));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b(a|an)\\b", 9999, 0));
		builderVector.add(new GMWordMinDistanceLevelFeatureBuilder("\\b[a-z]{3,}s\\b", 0, 9999));
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
		
		
		RerankFeatureBuilder[] pipeline = new RerankFeatureBuilder[builderVector.size()];
		for(int i=0; i<pipeline.length; i++)
		{
			pipeline[i] = builderVector.elementAt(i);
		}
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
