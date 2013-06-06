package bioner.tools;

import bioner.normalization.data.index.IndexConfig;
import bioner.normalization.data.index.LuceneIndexNER;
import bioner.process.organismner.OrgnismDictionaryBuilder;
import bioner.process.proteinner.ProteinDictionaryBuilder;

public class NERFactory {
	private static NERProcessor m_geneIndexNER = new LuceneIndexNER(IndexConfig.GENE_INDEX_DIRECTORY);
	private static NERProcessor m_speicesIndexNER = new LuceneIndexNER(IndexConfig.SPECIES_INDEX_DIRECTORY);
	public static NERProcessor getGeneIndexNER()
	{
		return m_geneIndexNER;
	}
	public static NERProcessor getSpeciesIndexNER()
	{
		return m_speicesIndexNER;
	}
}
