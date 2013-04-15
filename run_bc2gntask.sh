java -Xms128m -Xmx1024m -Djava.library.path="./:/usr/local/lib/" -classpath "./lib/biomedner.jar:./lib/mallet.jar:./lib/lucene-core-3.0.1.jar:./lib/rank-tool.jar:./lib/mysql-connector-java-5.1.12-bin.jar:./lib/ant.jar:./lib/jakarta-ant-optional.jar:./lib/jwnl-1.3.3.jar:./lib/maxent-2.5.2.jar:./lib/opennlp-tools-1.4.3.jar:./lib/trove.jar:./lib/CRFPP.jar:./lib/stanford-postagger-2009-12-24.jar:./lib/weka.jar:./lib/libsvm.jar:./lib/abner.jar:./lib/abner.jar:./lib/banner.jar:./lib/dragontool.jar:./lib/heptag.jar:./lib/junit-4.4.jar:./lib/medpost.jar" bioner.application.bc2gn.BC2GNTaskRun

cd ~/BC2GN/
perl eval_gn.pl
cd ~/workspace/BioMedNER/
java -Xms128m -Xmx1024m -Djava.library.path="./:/usr/local/lib/" -classpath "./lib/biomedner.jar:./lib/mallet.jar:./lib/lucene-core-3.0.1.jar:./lib/rank-tool.jar:./lib/mysql-connector-java-5.1.12-bin.jar:./lib/ant.jar:./lib/jakarta-ant-optional.jar:./lib/jwnl-1.3.3.jar:./lib/maxent-2.5.2.jar:./lib/opennlp-tools-1.4.3.jar:./lib/trove.jar:./lib/CRFPP.jar:./lib/stanford-postagger-2009-12-24.jar:./lib/abner.jar:./lib/banner.jar:./lib/dragontool.jar:./lib/heptag.jar:./lib/junit-4.4.jar:./lib/medpost.jar" bioner.application.bc2gn.ColorTagger

