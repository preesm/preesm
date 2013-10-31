#!/bin/sh

# This scripts removes everything that is not wanted in a tutorial zip.
# It then zips the tutorial

archivename=`pwd | awk -F / '{print $NF}'`
echo $archivename
find . -iname "*.layout" -print0 | xargs -0 -I layoutfiles rm -f layoutfiles
# rm -Rf Code/bin
rm -f Code/generated/*
rm -f DAG/*

# replacing top_display by top_display_2build
mv Algo/top_display.graphml Algo/top_display.graphml.save
cp Algo/top_display_2build.graphml Algo/top_display.graphml

cd ..

# excluding elements from the zip without deleting them
zip -r $archivename-2build $archivename -x *preparetutozip*.sh* \
*.svn* *Code/lib/pthread* *Code/lib/SDL* *.yuv* *.dat* *Code/bin* \
*.save* *Algo/top_display_2build.graphml* *4core.scenario* *8core.scenario* *8corec6678.scenario* \
*Archi/4CoreX86.slam* *Archi/8CoreX86.slam* *Archi/8CoreC6678.slam* *Code6678* *Code/src/sobel.c* \
*Code/src/splitMerge.c*  *Code/include/sobel.h* *Code/include/splitMerge.h* \
*Code/IDL/merge.idl* *Code/IDL/sobel.idl* *Code/IDL/split.idl*

cd $archivename

# retrieving the original top_display
rm Algo/top_display.graphml
mv Algo/top_display.graphml.save Algo/top_display.graphml

cd ..