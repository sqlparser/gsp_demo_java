#!/bin/bash

cur_dir=$(pwd)

src_dir=$cur_dir
bin_dir=../../../../../lib
class_dir=$cur_dir/class


rm -rf $src_dir/sources.list
find $src_dir -name "*.java" > $src_dir/sources.list
cat  $src_dir/sources.list

mkdir $cur_dir/lib
cp $bin_dir/gudusoft.gsqlparser.jar $cur_dir/lib
rm -rf $class_dir
rm -rf $class_dir/lib
mkdir $class_dir
mkdir $class_dir/lib
cp $cur_dir/MANIFEST.MF $class_dir
cp -r $bin_dir/gudusoft.gsqlparser.jar $class_dir/lib




javac -d $class_dir  -cp .:$bin_dir/gudusoft.gsqlparser.jar -g -sourcepath $src_dir @$src_dir/sources.list

cd $class_dir
jar -cvfm $cur_dir/data_flow_analyzer.jar MANIFEST.MF *
rm -rf $class_dir
