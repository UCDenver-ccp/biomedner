package crf.featurebuild;

import bioner.global.GlobalConfig;
import bioner.normalization.data.index.IndexConfig;
import bioner.process.knowledgebase.KnowledgeBaseDictBuilder;
import bioner.process.organismner.OrgnismDictionaryBuilder;
import bioner.process.proteinner.ProteinDictionaryBuilder;
import bioner.tools.dictionary.BioNERDictionary;
import crf.featurebuild.builder.ABNERBCGMFeatureBuilder;
import crf.featurebuild.builder.ABNERNLPBAFeatureBuilder;
import crf.featurebuild.builder.AminoAcidPositionFeatureBuilder;
import crf.featurebuild.builder.BANNERGMFeatureBuilder;
import crf.featurebuild.builder.DictionaryFeatureBuilder;
import crf.featurebuild.builder.IndexNERLabelFeatureBuilder;
import crf.featurebuild.builder.LinnaeusSpeciesFeatureBuilder;
import crf.featurebuild.builder.MorphologyTypeIIFeatureBuilder;
import crf.featurebuild.builder.MorphologyTypeIIIFeatureBuilder;
import crf.featurebuild.builder.OriginalWordFeatureBuilder;
import crf.featurebuild.builder.OrthographicFeatureBuilder;
import crf.featurebuild.builder.POSFeatureBuilder;
import crf.featurebuild.builder.PPIPatternFeatureBuilder;
import crf.featurebuild.builder.ShortLetterNumFeatureBuilder;
import crf.featurebuild.builder.SingleCharFeatureBuilder;
import crf.featurebuild.builder.StemFeatureBuilder;
import crf.featurebuild.builder.TokenLengthFeatureBuilder;
import crf.featurebuild.builder.WordListFeatureBuilder;

/**
 * This is a factory class for feature builder. The output is a array of TokenFeatureBuilder, forming a pipeline.
 * @author Liu Jingchen
 *
 */
public class TokenFeatureBuilderFactory {
	public static TokenFeatureBuilder[] createTokenFeatureBuilderPipeline()
	{
		TokenFeatureBuilder[] builderPipeline = new TokenFeatureBuilder[53];
		
		builderPipeline[0] = new OriginalWordFeatureBuilder();
		builderPipeline[1] = new StemFeatureBuilder();
		
		builderPipeline[2] = new OrthographicFeatureBuilder("[A-Z].*");
		builderPipeline[3] = new OrthographicFeatureBuilder("[A-Z][a-z]*");
		builderPipeline[4] = new OrthographicFeatureBuilder("[A-Z]+");
		builderPipeline[5] = new OrthographicFeatureBuilder("[a-z]+");
		builderPipeline[6] = new OrthographicFeatureBuilder("[A-Za-z]+");
		builderPipeline[7] = new OrthographicFeatureBuilder(".*[0-9].*");
		builderPipeline[8] = new OrthographicFeatureBuilder("[\\-0-9]+[\\.\\,]+[0-9]\\.\\,]+");
		builderPipeline[9] = new OrthographicFeatureBuilder("[A-Za-z0-9]+");
		builderPipeline[10] = new OrthographicFeatureBuilder("[ivxdlcm]+|[IVXDLCM]+");//Roman number
		builderPipeline[11] = new OrthographicFeatureBuilder(".*[A-Z]");
		builderPipeline[12] = new OrthographicFeatureBuilder(".*[A-Z].*[a-z].*|.*[a-z].*[A-Z].*");//MixCase RalGDS
		builderPipeline[13] = new OrthographicFeatureBuilder("[A-Z]");
		builderPipeline[14] = new OrthographicFeatureBuilder("[^A-Z]*[A-Z][^A-Z]*");//SingleCap kDa
		builderPipeline[15] = new OrthographicFeatureBuilder("[A-Z]{2}");
		builderPipeline[16] = new OrthographicFeatureBuilder("[A-Z]{3}");
		builderPipeline[17] = new OrthographicFeatureBuilder("[A-Z]{4,}");
		builderPipeline[18] = new OrthographicFeatureBuilder("[0-9]");
		builderPipeline[19] = new OrthographicFeatureBuilder("[0-9]{2}");
		builderPipeline[20] = new OrthographicFeatureBuilder("[0-9]{3}");
		builderPipeline[21] = new OrthographicFeatureBuilder("[0-9]{4,}");
		builderPipeline[22] = new OrthographicFeatureBuilder("[\\,\\.\\;\\:\\?\\!\\-\\+\\'\\\"\\']");
		builderPipeline[23] = new OrthographicFeatureBuilder("[A-Z]|[a-z]");
		
		builderPipeline[24] = new MorphologyTypeIIFeatureBuilder();
		builderPipeline[25] = new MorphologyTypeIIIFeatureBuilder();
		builderPipeline[26] = new ShortLetterNumFeatureBuilder();
		builderPipeline[27] = new SingleCharFeatureBuilder();
		
		builderPipeline[28] = new POSFeatureBuilder();
		builderPipeline[29] = new TokenLengthFeatureBuilder();
		builderPipeline[30] = new WordListFeatureBuilder(GlobalConfig.GREEK_LIST_FILEPATH,false);
		
		builderPipeline[31] = new OrthographicFeatureBuilder("[ATCGU]+|[atcgu]+");
		builderPipeline[32] = new WordListFeatureBuilder(GlobalConfig.NUCLEIC_ACID_LIST_FILEPATH,false);
		builderPipeline[33] = new WordListFeatureBuilder(GlobalConfig.NUCLEOSIDE_LIST_FILEPATH,false);
		builderPipeline[34] = new WordListFeatureBuilder(GlobalConfig.NUCLEOTIDE_LIST_FILEPATH,false);
		
		builderPipeline[35] = new WordListFeatureBuilder(GlobalConfig.AMINO_ACID_LIST_FILEPATH,false);
		builderPipeline[36] = new WordListFeatureBuilder(GlobalConfig.AMINO_ACID_SHORT_LIST_FILEPATH,true);
		builderPipeline[37] = new AminoAcidPositionFeatureBuilder(GlobalConfig.AMINO_ACID_SHORT_LIST_FILEPATH, false);
		
		BioNERDictionary diseaseDict = KnowledgeBaseDictBuilder.getDiseaseDictionary();
		BioNERDictionary drugDict = KnowledgeBaseDictBuilder.getDrugDictionary();
		builderPipeline[38] = new IndexNERLabelFeatureBuilder(IndexConfig.GENE_INDEX_DIRECTORY);
		//builderPipeline[39] = new IndexNERLabelFeatureBuilder(IndexConfig.SPECIES_INDEX_DIRECTORY);
		builderPipeline[39] = new LinnaeusSpeciesFeatureBuilder();
		builderPipeline[40] = new DictionaryFeatureBuilder(drugDict);
		builderPipeline[41] = new DictionaryFeatureBuilder(diseaseDict);
		builderPipeline[42] = new PPIPatternFeatureBuilder();
		
		
		builderPipeline[43] = new OrthographicFeatureBuilder(".*[A-Z].*");
		builderPipeline[44] = new OrthographicFeatureBuilder(".*[A-Z0-9].*");
		builderPipeline[45] = new OrthographicFeatureBuilder("h[A-Z].*");
		builderPipeline[46] = new OrthographicFeatureBuilder("[a-gi-z][A-Z].*");
		builderPipeline[47] = new OrthographicFeatureBuilder(".*(alpha|beta|zeta|gamma|delta).*");
		builderPipeline[48] = new OrthographicFeatureBuilder(".*[IVX]+");
		builderPipeline[49] = new ABNERBCGMFeatureBuilder();
		builderPipeline[50] = new ABNERNLPBAFeatureBuilder("PROTEIN");
		builderPipeline[51] = new ABNERNLPBAFeatureBuilder("DNA");
		builderPipeline[52] = new BANNERGMFeatureBuilder();
		return builderPipeline;
	}
}
