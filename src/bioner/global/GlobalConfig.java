package bioner.global;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bioner.normalization.data.database.DatabaseConfig;
import bioner.normalization.data.index.IndexConfig;

public class GlobalConfig {
	//public static String ROOT_DIR = "/home/ljc/WebTool/WebTool/";
	public static String ROOT_DIR = "./";
	public static String REPLACE_RULE_FILE_PATH = ROOT_DIR+"data/token_normal/replace_rules.txt";
	//public static String PROTEIN_DICT_PATH = "data/dict/newRltEntrySwissProt.dat";
	public static String PROTEIN_DICT_PATH = ROOT_DIR+"/data/dict/rlt_sprot_20061003";
	public static String SOTPWORD_LIST_PATH = ROOT_DIR+"data/dict/stopword.txt";
	public static String ORGNISM_DICT_PATH = ROOT_DIR+"data/dict/FullNameNew.txt";
	public static String ENTITYFILTER_TABULIST_PATH = ROOT_DIR+"data/filter/tabulist.txt";
	public static String POSTPROCESS_ENTITYFILTER_TABULIST_PATH = ROOT_DIR+"data/filter/tabulist.post.txt";
	
	public static String AICORP_INPUT_FILE_PATH = ROOT_DIR+"data/AIMed_Corp/AIMed_corp.xml";
	public static String AICORP_OUTPUT_DIR = "../../AIMed_Corp/JavaOutput/";
	
	public static int THREAD_NUM = 1;
	
	public static String PROTEIN_TYPE_LABEL = "PROTEIN";
	public static String ORGANISM_TYPE_LABEL = "ORGANISM";
	
	public static String GENIA_INPUT_FILE_PATH = ROOT_DIR+"data/GENIA_corp/GENIAcorpus3.02.xml";
	public static String GENIA_OUTPUT_DIR = "../../GENIA_Corp/JavaOutput/";
	public static String GENIA_CRF_TRAIN_TEST_OUTPUT_DIR = "../../GENIA_Corp/test/";
	
	public static String PROTEIN_TOKEN_B_LABEL = "B-PRO";
	public static String PROTEIN_TOKEN_I_LABEL = "I-PRO";
	public static String PROTEIN_TOKEN_E_LABEL = "E-PRO";
	public static String ORGANISM_TOKEN_B_LABEL = "B-ORG";
	public static String ORGANISM_TOKEN_I_LABEL = "I-ORG";
	public static String ORGANISM_TOKEN_E_LABEL = "E-ORG";
	
	public static String CONFIG_FILE_PATH = ROOT_DIR+"config";
	
	public static String BC2_GM_TRAIN_DATA_FILEPATH = ROOT_DIR+"data/Biocreative2/GM.train.in";
	public static String BC2_GM_TEST_DATA_FILEPATH = ROOT_DIR+"data/Biocreative2/GM.test.in";
	public static String BC2_GM_TRAIN_OUTPUT_PATH = "../../BC2_GM/JavaOutput/";
	public static String BC2_GM_CRF_TRAIN_TEST_OUTPUT_DIR = "../../BC2_GM/test/";
	public static String CRF_MODEL_FILEPATH = ROOT_DIR+"train/model";
	
	public static String BC2_GN_TEST_DATA_FILEPATH = ROOT_DIR+"data/Biocreative2/bc2GNtestdocs/";
	public static String BC2_GN_TEST_RESULT_OUTPUTPATH = "../../BC2GN/gn.eval";
	
	public static String AMINO_ACID_LIST_FILEPATH = ROOT_DIR+"data/featurebuild/aminoAcid.list.txt";
	public static String AMINO_ACID_SHORT_LIST_FILEPATH = ROOT_DIR+"data/featurebuild/aminoAcidShort.list.txt";
	public static String GREEK_LIST_FILEPATH = ROOT_DIR+"data/featurebuild/greek.list.txt";
	public static String NUCLEIC_ACID_LIST_FILEPATH = ROOT_DIR+"data/featurebuild/nucleicAcid.list.txt";
	public static String NUCLEOSIDE_LIST_FILEPATH = ROOT_DIR+"data/featurebuild/nucleoside.list.txt";
	public static String NUCLEOTIDE_LIST_FILEPATH = ROOT_DIR+"data/featurebuild/nucleotide.list.txt";
	public static String SPECIES_TREE_FILENAME = ROOT_DIR+"data/dict/species_tree.gz";
	
	
	
	
	
	
	

	public static String CRF_INEXACT_MODEL_FILEPATH = "../../BC2GM/model_gm";
	
	
	public static String ENTITY_LABEL_ITATIC = "ita";
	public static String ENTITY_LABEL_CRF = "crf";
	public static String ENTITY_LABEL_BANNER = "banner";
	public static String ENTITY_LABEL_ABNER_BC = "abner_bc";
	public static String ENTITY_LABEL_ABNER_PROTEIN = "abner_pro";
	public static String ENTITY_LABEL_ABNER_DNA = "abner_dna";
	public static String ENTITY_LABEL_INDEX = "index";
	public static String ENTITY_LABEL_INEXACT_CRF = "inexact_crf";
	public static String ENTITY_LABEL_IDGM = "id_gm";
	
	public static String ENJU_BC2GN_ALLSENTENCES = "./data/enju/bc2gn_all.txt";
	public static String ENJU_BC2GN_PARSED = "./data/enju/bc2gn_all.parsed.txt";
	
	public static String BC3GN_DATADIR="../../BC3GN/xmls/";
	public static void ReadConfigFile()
	{
		DatabaseConfig.ReadConfigFile();
		IndexConfig.ReadConfigFile();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));
			String line;
			while((line=freader.readLine()) != null)
			{
				if(line.startsWith("THREAD_NUM"))
				{
					String valueStr = getValueStr(line);
					THREAD_NUM = Integer.parseInt(valueStr);
				}else if(line.startsWith("CRF_MODEL_FILEPATH"))
				{
					String valueStr = getValueStr(line);
					CRF_MODEL_FILEPATH = valueStr;
				}
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String getValueStr(String line)
	{
		int pos = line.indexOf('=');
		String valueStr = line.substring(pos+1).trim();
		return valueStr;
	}
}
