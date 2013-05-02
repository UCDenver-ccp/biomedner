#!/bin/bash

#
# Dependencies:
#  -crfpp needs to be installed

BC2_DATA=/Users/roederc/work/sources/biocreative2
BC3_DATA=/Users/roederc/work/sources/biocreativeiii
BIOMED_NER_HOME=/Users/roederc/work/git/biomedner

TRAIN=0

# Train CRFPP: BC2GMFeatureBuildRun, crf_learn
TRAIN_FILE=$BC2_DATA/bc2geneMention/train/train.in
EVAL_FILE=$BC2_DATA/bc2geneMention/train/GENE.eval
FEATURE_FILE=$BC2_DATA/bc2geneMention/train/Train_Data.gm.crfpp
CRFPP_MODEL=$BIOED_NER_HOME/train/Crfpp.gm.model
echo "preparing training data"
if (( $TRAIN )) 
then
mvn -e exec:java -Dexec.mainClass="crf.featurebuild.bc2gm.BC2GMFeatureBuildRun" \
				 -Dexec.args="$TRAIN_FILE $EVAL_FILE $FEATURE_FILE" > /dev/null
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "ERROR preparing BC2GM feature data for crfpp_learn: $STATUS"
		exit -1
	fi

echo "training crfpp $FEATURE_FILE $CRFPP_MODEL"	
TEMPLATE_FILE=$BIOMED_NER_HOME/train/template.txt
crf_learn $TEMPLATE_FILE $FEATURE_FILE $CRFPP_MODEL
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "ERROR running crf_learn: $STATUS"
		exit -1
	fi
fi



echo "prepparing normalization train data"
# Pipeline stage 1: BC3GNBuildNormalizationTrainData
XMLS_DIR=$BC3_DATA/BC3GNTraining/xmls
GENE_LIST_FILE=$BC3_DATA/BC3GNTraining/TrainingSet1.txt
NORM_FILE=$BIOMD_NER_HOME/RankTrainDAta.txt
mvn -e exec:java -Dexec.mainClass="bioner.application.bc3gn.BC3GNBuildNormalizationTrainData" \
				  -Dexec.args="$XMLS_DIR $GENE_LIST_FILE $NORM_FILE" 
	STATUS=$?
	if (( $STATUS != 0 ))
	then
		echo "ERROR preparing BC3GN normalization data: $STATUS"
		exit -1
	fi


# Pipeline stage 2: BC3GNBuildSecondRankTrainData
#mvn -e exec:java -Dexec.mainClass="crf.featurebuild.bc2gm.BC2GMFeatureBuildRun" \


# Pipeline stage 3: BC3GNBuildRerankTrainData
#mvn -e exec:java -Dexec.mainClass="crf.featurebuild.bc2gm.BC2GMFeatureBuildRun" \


# Pipeline stage 4: B3GNTaskRun
#mvn -e exec:java -Dexec.mainClass="crf.featurebuild.bc2gm.BC2GMFeatureBuildRun" \




