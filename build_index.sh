java -cp "./bin/:./lib/biomedner.jar:./lib/lucene-core-3.0.1.jar:./lib/servlet-api-2.4.jar" bioner.normalization.data.index.LuceneSpeciesIndexBuilder ./data/dict/FullNameNew.txt
java -cp "./bin/:./lib/biomedner.jar:./lib/lucene-core-3.0.1.jar:./lib/servlet-api-2.4.jar" bioner.normalization.data.index.LuceneGeneIndexBuilder ../../EntrezGene/geneDatabase.gz
