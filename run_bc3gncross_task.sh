for((i=0;i<4;i++))
do
java -Xms128m -Xmx2048m -Djava.library.path="./:/usr/local/lib/" -classpath "./lib/biomedner.jar:./lib/grmm.jar:./lib/lucene-core-3.0.1.jar:./lib/rank-tool.jar:./lib/mysql-connector-java-5.1.12-bin.jar:./lib/ant.jar:./lib/jakarta-ant-optional.jar:./lib/jwnl-1.3.3.jar:./lib/maxent-2.5.2.jar:./lib/opennlp-tools-1.4.3.jar:./lib/trove.jar:./lib/CRFPP.jar:./lib/stanford-postagger-2009-12-24.jar:./lib/abner.jar:./lib/weka.jar:./lib/grmm-deps.jar:./lib/mallet-deps.jar" bioner.application.bc3gn.BC3GNTaskRun ../../BC3GN/cross/$i/testing/ ../../BC3GN/cross/$i/TrainData_50.txt ../../BC3GN/cross/$i/secondRankTrainData.txt ../../BC3GN/cross/$i/RerankTrainData.txt "../../BC3GN/cross/gn_$i.eval"
done
