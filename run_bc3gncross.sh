for((i=0;i<=3;i++))
do
#java -Xms128m -Xmx2048m -Djava.library.path="./:/usr/local/lib/" -classpath "./bin/:./lib/biomedner.jar:./lib/grmm.jar:./lib/lucene-core-3.0.1.jar:./lib/rank-tool.jar:./lib/mysql-connector-java-5.1.12-bin.jar:./lib/ant.jar:./lib/jakarta-ant-optional.jar:./lib/jwnl-1.3.3.jar:./lib/maxent-2.5.2.jar:./lib/opennlp-tools-1.4.3.jar:./lib/trove.jar:./lib/CRFPP.jar:./lib/stanford-postagger-2009-12-24.jar:./lib/abner.jar:./lib/grmm-deps.jar:./lib/mallet-deps.jar:./lib/banner.jar:./lib/dragontool.jar:./lib/heptag.jar:./lib/junit-4.4.jar:./lib/medpost.jar" bioner.application.bc3gn.BC3GNBuildNormalizationTrainData ../../BC3GN/cross/$i/training/ ../../BC3GN/cross/$i/TrainingSet.txt ../../BC3GN/cross/$i/RankTrainData.txt

#java -Xms128m -Xmx2048m -Djava.library.path="./:/usr/local/lib/" -classpath "./bin/:./lib/biomedner.jar:./lib/grmm.jar:./lib/lucene-core-3.0.1.jar:./lib/rank-tool.jar:./lib/mysql-connector-java-5.1.12-bin.jar:./lib/ant.jar:./lib/jakarta-ant-optional.jar:./lib/jwnl-1.3.3.jar:./lib/maxent-2.5.2.jar:./lib/opennlp-tools-1.4.3.jar:./lib/trove.jar:./lib/CRFPP.jar:./lib/stanford-postagger-2009-12-24.jar:./lib/abner.jar:./lib/grmm-deps.jar:./lib/mallet-deps.jar:./lib/weka.jar:./lib/libsvm.jar:./lib/banner.jar:./lib/dragontool.jar:./lib/heptag.jar:./lib/junit-4.4.jar:./lib/medpost.jar" bioner.application.bc3gn.rank.BC3GNBuildRerankTrainData ../../BC3GN/cross/$i/training/ ../../BC3GN/cross/$i/TrainingSet.txt ../../BC3GN/cross/$i/RankTrainData.txt ../../BC3GN/train/secondRankTrainData.txt ../../BC3GN/cross/$i/RerankTrainData.txt

java -Xms128m -Xmx1024m -Djava.library.path="./:/usr/local/lib/" -classpath "./bin/:./lib/biomedner.jar:./lib/grmm.jar:./lib/lucene-core-3.0.1.jar:./lib/rank-tool.jar:./lib/mysql-connector-java-5.1.12-bin.jar:./lib/ant.jar:./lib/jakarta-ant-optional.jar:./lib/jwnl-1.3.3.jar:./lib/maxent-2.5.2.jar:./lib/opennlp-tools-1.4.3.jar:./lib/trove.jar:./lib/CRFPP.jar:./lib/stanford-postagger-2009-12-24.jar:./lib/abner.jar:./lib/weka.jar:./lib/grmm-deps.jar:./lib/mallet-deps.jar:./lib/libsvm.jar:./lib/banner.jar:./lib/dragontool.jar:./lib/heptag.jar:./lib/junit-4.4.jar:./lib/medpost.jar" bioner.application.bc3gn.BC3GNTaskRun ../../BC3GN/cross/$i/testing/ ../../BC3GN/cross/$i/RankTrainData.txt ../../BC3GN/train/secondRankTrainData.txt ../../BC3GN/cross/$i/RerankTrainData.txt ../../BC3GN/cross/$i/gn.txt
done
cat ../../BC3GN/cross/0/gn.txt ../../BC3GN/cross/1/gn.txt ../../BC3GN/cross/2/gn.txt ../../BC3GN/cross/3/gn.txt > ../../BC3GN/gn.txt
cat ../../BC3GN/cross/0/gn.txt.detail ../../BC3GN/cross/1/gn.txt.detail ../../BC3GN/cross/2/gn.txt.detail ../../BC3GN/cross/3/gn.txt.detail > ../../BC3GN/gn.txt.detail
