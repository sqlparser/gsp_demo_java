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
git clone https://github.com/shenhuan2021/dlineage_base.git
sudo mkdir -p $p1/dlineage_base/lib/
sudo mkdir -p $p1/dlineage_base/src/main/java/demos/lineage

sudo cp $p1/lib/*.jar $p1/dlineage_base/lib/
sudo cp $p1/external_lib/sqlflow-exporter.jar $p1/dlineage_base/lib/
sudo cp $p1/src/main/java/demos/dlineage_base/DataFlowAnalyzer.java $p1/dlineage_base/src/main/java/demos/lineage/
sudo cp $p1/src/main/java/demos/dlineage_base/SqlflowIngester.java $p1/dlineage_base/src/main/java/demos/lineage/
cd dlineage_base
sudo chmod 777 maven.sh
sh maven.sh
echo 'maven dlineage_base jar success.'

cd $p1
sudo mkdir -p $p1/dlineage_base/lib
sudo mkdir -p $p1/dlineage_base/src
sudo mkdir -p $p1/dlineage_base/bin
sudo mkdir -p $p1/dlineage_base/doc
sudo mkdir -p $p1/dlineage_base/simple
sudo mkdir -p $p1/dlineage_base/script
echo 'mkdir success.'

sudo cp $p1/dlineage_base/target/*.jar $p1/dlineage_base/bin/
sudo cp $p1/dlineage_base/script/start.sh $p1/dlineage_base/script/
sudo cp $p1/dlineage_base/script/start.bat $p1/dlineage_base/script/
sudo cp $p1/lib/*.jar $p1/dlineage_base/lib/
sudo cp $p1/src/main/java/demos/dlineage_base/DataFlowAnalyzer.java $p1/dlineage_base/src/
sudo cp $p1/src/main/java/demos/dlineage_base/SqlflowIngester.java $p1/dlineage_base/src/
sudo cp $p1/src/main/java/demos/dlineage_base/sample/*.sql $p1/dlineage_base/sample/
sudo cp $p1/dlineage_base/sqlflow-data-lineage-model-reference.pdf $p1/dlineage_base/doc/
sudo cp $p1/src/main/java/demos/dlineage_base/readme.md $p1/dlineage_base/
echo 'cp gsp jar success.'

sudo zip -r gudusoft.dlineage_base-$version.zip dlineage_base/
mkdir dlienagezip
cp gudusoft.dlineage_base-$version.zip dlienagezip/
