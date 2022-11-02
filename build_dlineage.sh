#!/bin/bash

#source dir
p1=$1

cd $p1/lib
jarpath=$(ls)
echo 'zippath is '
ps=(${jarpath//.jar/ })
dirpath=${ps[0]}
ps2=(${dirpath//gudusoft\.gsqlparser\./ })
version=$ps2

echo 'start build'
cd $p1
git clone https://shenhuan2021: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@github.com/sqlparser/dlineage.git
sudo cp $p1/lib/*.jar $p1/dlineage/lib/
sudo cp $p1/external_lib/sqlflow-exporter.jar $p1/dlineage/lib/
sudo cp $p1/src/main/java/demos/dlineage/DataFlowAnalyzer.java $p1/dlineage/src/main/java/
sudo cp $p1/src/main/java/demos/dlineage/SqlflowIngester.java $p1/dlineage/src/main/java/
cd dlineage
chmod 777 maven.sh
sh maven.sh
echo 'maven dlineage jar success.'

cd $p1
sudo mkdir -p $p1/Dlineage/lib
sudo mkdir -p $p1/Dlineage/src
sudo mkdir -p $p1/Dlineage/bin
sudo mkdir -p $p1/Dlineage/doc
sudo mkdir -p $p1/Dlineage/simple
sudo mkdir -p $p1/Dlineage/script
echo 'mkdir success.'

sudo cp $p1/dlineage/target/*.jar $p1/Dlineage/bin/
sudo cp $p1/dlineage/script/start.sh $p1/Dlineage/script/
sudo cp $p1/dlineage/script/start.bat $p1/Dlineage/script/
sudo cp $p1/lib/*.jar $p1/Dlineage/lib/
sudo cp $p1/src/main/java/demos/dlineage/DataFlowAnalyzer.java $p1/Dlineage/src/
sudo cp $p1/src/main/java/demos/dlineage/SqlflowIngester.java $p1/Dlineage/src/
sudo cp $p1/src/main/java/demos/dlineage/sample/*.sql $p1/Dlineage/sample/
sudo cp $p1/dlineage/sqlflow-data-lineage-model-reference.pdf $p1/Dlineage/doc/
sudo cp $p1/src/main/java/demos/dlineage/readme.md $p1/Dlineage/
echo 'cp gsp jar success.'

sudo zip -r gudusoft.dlineage-$version.zip Dlineage/
mkdir dlienagezip
cp gudusoft.dlineage-$version.zip dlienagezip/
