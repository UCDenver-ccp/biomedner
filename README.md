biomedner
=========

a fork of: http://biomedner.googlecode.com/svn

a key component of GeneTUKit, http://www.qanswers.net/GeneTUKit/demo.jsp

License:
http://www.eclipse.org/legal/epl-v10.html

See:
- http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3065680/
- ftp://ftp.research.microsoft.com/pub/tr/TR-2007-40.pdf
- https://code.google.com/p/learning-to-rank-listnet/

TODO:
- The jar dependencies aren't fully identified, but included. 
  The pom.xml file makes generic refereneces to libs that you
  (likelY) won't find in any repository, but need to install 
  locally from the lib directory. This needs to be scripted
  and ultimatley cleaned up by identifying the version and
  making reference to publicly available jars when/where possible.

INTRODUCTION:
    see the ?? file for canonical detail. Notes below are supplemental and
explain the maven.sh script that runs this code from maven pom files.

SUMMARY:
    As decribed in the paper, the ultimate normalization pipeline goes through the following
four phases:
    1. Gene Mention Recognition
        a. CRF using a model: crfpp.gm.model
        b. dictionary using the NCBI gene_info.gz loaded into Lucene
        c. ABNER
    2. Generate Gene ID Candidates
        a. lookup against a lucene index of species and generate top 50 IDs
    3. Disambiguation and Ranking
    4. Confidence Score and re-ranking
        a. SVM classifier using which model????

TRAINING:

