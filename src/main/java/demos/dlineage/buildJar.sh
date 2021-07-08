#!/bin/bash

cur_dir=$(pwd)


src_dir=$cur_dir
bin_dir=../../../../../lib
class_dir=$cur_dir/class


sudo rm -rf $src_dir/sources.list
sudo find $src_dir -name "*.java" > $src_dir/sources.list
sudo cat  $src_dir/sources.list

sudo mkdir $cur_dir/lib
sudo cp $bin_dir/gudusoft.gsqlparser.jar $cur_dir/lib
sudo rm -rf $class_dir
sudo rm -rf $class_dir/lib
sudo mkdir $class_dir
sudo mkdir $class_dir/lib
sudo cp $cur_dir/MANIFEST.MF $class_dir
sudo cp -r $bin_dir/gudusoft.gsqlparser.jar $class_dir/lib




javac -d $class_dir  -cp .:$bin_dir/gudusoft.gsqlparser.jar -g -sourcepath $src_dir @$src_dir/sources.list

sudo cd $class_dir
sudo jar -cvfm $cur_dir/data_flow_analyzer.jar MANIFEST.MF *
sudo sudo rm -rf $class_dir

