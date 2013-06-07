CLASSPATH="-classpath \"./lib/biomedner.jar:./lib/grmm.jar:./lib/lucene-core-3.0.1.jar:./lib/rank-tool.jar:./lib/mysql-connector-java-5.1.12-bin.jar:./lib/ant.jar:./lib/jakarta-ant-optional.jar:./lib/jwnl-1.3.3.jar:./lib/maxent-2.5.2.jar:./lib/opennlp-tools-1.4.3.jar:./lib/trove.jar:./lib/CRFPP.jar:./lib/stanford-postagger-2009-12-24.jar:./lib/abner.jar:./lib/grmm-deps.jar:./lib/mallet-deps.jar\""

#DATA_DIR=../../BC3GN/train
DATA_DIR=/Users/roederc/work/git/biomedner/train_data
 
echo "ONE: $DATA_DIR/xmls $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt"
#java -Xms128m -Xmx2048m -Djava.library.path="./:/usr/local/lib/"  $CLASSPATH \ bioner.application.bc3gn.BC3GNBuildNormalizationTrainData \
#$DATA_DIR/xmls/ $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt
mvn exec:java -Dexec.mainClass="bioner.application.bc3gn.BC3GNBuildNormalizationTrainData" \
-Dexec.args="$DATA_DIR/xmls $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt"


echo "TWO: $DATA_DIR/xmls/ $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt $DATA_DIR/secondRankTrainData.txt"
#java -Xms128m -Xmx2048m -Djava.library.path="./:/usr/local/lib/"  $CLASSPATH \ bioner.application.bc3gn.BC3GNBuildSecondRankTrainData 
#$DATA_DIR/xmls/ $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt $DATA_DIR/secondRankTrainData.txt
mvn exec:java -Dexec.mainClass="bioner.application.bc3gn.BC3GNBuildSecondRankTrainData" \
-Dexec.args="$DATA_DIR/xmls/ $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt $DATA_DIR/secondRankTrainData.txt"


echo "THREE:$DATA_DIR//xmls/ $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt $DATA_DIR/secondRankTrainData.txt $DATA_DIR/RerankTrainData.txt"
#java -Xms128m -Xmx2048m -Djava.library.path="./:/usr/local/lib/"  $CLASSPATH \ bioner.application.bc3gn.rank.BC3GNBuildRerankTrainData \
#$DATA_DIR//xmls/ $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt $DATA_DIR/secondRankTrainData.txt $DATA_DIR/RerankTrainData.txt
mvn exec:java -Dexec.mainClass="bioner.application.bc3gn.rank.BC3GNBuildRerankTrainData" \
-Dexec.args="$DATA_DIR//xmls/ $DATA_DIR/TrainingSet.txt $DATA_DIR/TrainData_50.txt $DATA_DIR/secondRankTrainData.txt $DATA_DIR/RerankTrainData.txt"

