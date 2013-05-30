#!/bin/bash

#
# Dependencies:
#  -crfpp needs to be installed

export MAVEN_OPTS="-Xmx2g -d64"
# need paths to both libCRFPP.so and libcrfpp.so.0.0.0
# first is from java directory, created by SWIG. second is from
# main body of crfpp.
export LD_LIBRARY_PATH=.:/usr/local/lib

# phases
# - DB & Lucene
CREATE_DB=0
CREATE_INDEX=0
# - CRF
PREPARE_TRAIN=1
TRAIN=1
# - WEKA
NORMALIZATION=1
RERANK=1
# - Tasks
TASK=0
RUN=1

# mac
#BC2_DATA=/Users/roederc/work/sources/biocreative2
#BC3_DATA=/Users/roederc/work/sources/biocreativeiii
#BIOMED_NER_HOME=/Users/roederc/work/git/biomedner

# linux
BC2_DATA=/home/roederc/work/sources/biocreative2
BC3_DATA=/home/roederc/work/sources/biocreativeiii
BIOMED_NER_HOME=/home/roederc/work/git/biomedner



# Create DB
if (( $CREATE_DB )) 
then
	# FINISH THIS ********
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "maven.sh: ERROR preparing BC2GM feature data for crfpp_learn: $STATUS"
		exit -1
	fi
fi

# Create Index
if (( $CREATE_INDEX )) 
then
	mkdir -p index/gene 2> /dev/null
	java -cp "target/classes:./bin/:./lib/biomedner.jar:./lib/lucene-core-3.0.1.jar:./lib/servlet-api-2.4.jar"\
	 	bioner.normalization.data.index.LuceneSpeciesIndexBuilder \
		./data/dict/FullNameNew.txt ./index/gene
	java -cp "target/classes:./bin/:./lib/biomedner.jar:./lib/lucene-core-3.0.1.jar:./lib/servlet-api-2.4.jar" \
		bioner.normalization.data.index.LuceneGeneIndexBuilder \
		gene_info.gz

fi



# Train CRFPP: BC2GMFeatureBuildRun, crf_learn
TRAIN_FILE=$BC2_DATA/bc2geneMention/train/train.in
EVAL_FILE=$BC2_DATA/bc2geneMention/train/GENE.eval
FEATURE_FILE=$BC2_DATA/bc2geneMention/train/Train_Data.gm.crfpp
CRFPP_MODEL=$BIOMED_NER_HOME/train/Crfpp.gm.model
if (( $PREPARE_TRAIN )) 
then
	echo "maven.sh: preparing training data"
	mvn -e exec:java -Dexec.mainClass="crf.featurebuild.bc2gm.BC2GMFeatureBuildRun" \
				 -Dexec.args="$TRAIN_FILE $EVAL_FILE $FEATURE_FILE" > /dev/null
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "maven.sh: ERROR preparing BC2GM feature data for crfpp_learn: $STATUS"
		exit -1
	fi
fi 

if (( $TRAIN )) 
then
	TEMPLATE_FILE=$BIOMED_NER_HOME/train/template.txt
	echo "maven.sh: training crfpp $TEMPLATE_FILE  $FEATURE_FILE $CRFPP_MODEL"	
	crf_learn $TEMPLATE_FILE $FEATURE_FILE $CRFPP_MODEL
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "maven.sh: ERROR running crf_learn: $STATUS"
		exit -1
	fi
fi



XMLS_DIR_32="$BC3_DATA/BC3GNTraining/32_xmls/"   ## String dataDir = "../../BC3GN/xmls/";
XMLS_DIR="$BC3_DATA/BC3GNTraining/xmls"   ## String dataDir = "../../BC3GN/xmls/";
GENE_LIST_FILE=$BC3_DATA/BC3GNTraining/TrainingSet1.txt ## String genelistFilename = "../../BC3GN/data/TrainingSet2.txt";
FILTER_FILE="./data/filter/tabulist.txt";
NORM_FILE=$BIOMED_NER_HOME/RankTrainData_webtool.txt  ## String outputFilename = "../../BC3GN/TrainData_10.txt";
##NORM_FILE=$BIOMED_NER_HOME/RankTrainData_bc3gn.txt  ## String outputFilename = "../../BC3GN/TrainData_10.txt";
##NORM_FILE=train/TrainData_1.txt

if (( $NORMALIZATION )) 
then
echo "maven.sh: prepparing normalization train data"
echo "LD_LIBRARY_PATH: $LD_LIBRARY_PATH"
# Pipeline stage 1: BC3GNBuildNormalizationTrainData
#mvn -e exec:java -Dexec.mainClass="bioner.application.bc3gn.BC3GNBuildNormalizationTrainData" \
#                 -Dexec.args="$XMLS_DIR_32 $GENE_LIST_FILE $NORM_FILE_BC3GN $CRFPP_MODEL"  
mvn -e exec:java -Dexec.mainClass="bioner.application.webtool.BC3GNBuildNormalizationTrainData" \
                 -Dexec.args="$XMLS_DIR_32 $GENE_LIST_FILE $NORM_FILE $CRFPP_MODEL $FILTER_FILE"

	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "maven.sh: ERROR preparing BC3GN normalization data: $STATUS"
		exit -1
	fi
fi

# FORCE creation of a new model file by removing the old one
# else the new training data won't make a difference
rm $NORM_FILE.model 2> /dev/null > /dev/null



# Pipeline stage 3: BC3GNBuildRerankTrainData
###RERANK_DATA=RerankTrainData.txt
RERANK_DATA=RerankTrainData_webtool.txt
if (( $RERANK )) 
then
mvn -e exec:java -Dexec.mainClass="bioner.application.webtool.rank.BC3GNBuildRerankTrainData" \
				  -Dexec.args="$XMLS_DIR_32 $GENE_LIST_FILE $NORM_FILE $FILTER_FILE $RERANK_DATA"  
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "maven.sh: ERROR preparing BC3GN rerank  data: $STATUS"
		exit -1
	fi
fi
# FORCE creation of a new model file by removing the old one
# else the new training data won't make a difference
rm $RERANK_DATA.svm_model


# Pipeline stage 4: B3GNTaskRun
# many versions:
#   bc3gn, bc2gm, webtool, bc2gn???
# bc3gn and webtool differ in htat one reranks by logistic, the other by svm
# args:
#  0 xmls               XMLS_DIR
#  1 TrainData_50.txt  AKA TrainData_1.txt 
#  3 rerankTrainData     RERANK_DATA
#  4 gn.eval (output)    GN_TXT
GN_TXT=gn.txt
DIST_TRAIN_DATA=train/TrainData_1.txt
DIST_RERANK_DATA=train/RerankTrainData_1.txt
if (( $TASK )) 
then
##mvn -e exec:java -Dexec.mainClass="bioner.application.bc3gn.BC3GNTaskRun" \
              #   -Dexec.args="$XMLS_DIR $DIST_TRAIN_DATA $RANK_DATA $SECOND_RANK_DATA $RERANK_DATA_WEB $GN_TXT"  
mvn -e exec:java -Dexec.mainClass="bioner.application.webtool.BC3GNTaskRun" \
                 -Dexec.args="$XMLS_DIR $NORM_FILE meaningless_placeholder $RERANK_DATA $GN_TXT"  
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "maven.sh: ERROR running task: $STATUS"
		exit -1
	fi
fi

if (( $RUN )) 
then
## args are all different here, but what files does it use? where does it find them?:
mvn -e exec:java -Dexec.mainClass="bioner.application.webtool.GNRun" \
				  -Dexec.args="-x $XMLS_DIR/2660273.nxml $CRFPP_MODEL $NORM_FILE $RERANK_DATA $FILTER_FILE"
				  #-Dexec.args="-x $XMLS_DIR $CRFPP_MODEL $NORM_FILE $RERANK_DATA"
				  ##-Dexec.args="-x $XMLS_DIR/2660273.nxml /home/roederc/GeneTUKit/GeneTUKit/train/model  $NORM_FILE $DIST_RERANK_DATA -banner"
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "maven.sh: ERROR running task: $STATUS"
		exit -1
	fi
fi



