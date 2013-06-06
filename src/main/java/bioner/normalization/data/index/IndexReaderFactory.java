package bioner.normalization.data.index;

public class IndexReaderFactory {
	private static IndexReader reader = new LuceneIndexReader(IndexConfig.GENE_INDEX_DIRECTORY);
	public static IndexReader createGeneIndexReader()
	{
		return reader;
	}
}
