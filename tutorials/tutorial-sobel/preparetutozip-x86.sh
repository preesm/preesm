#!/bin/sh

# This scripts removes everything that is not wanted in a tutorial zip.
# It then zips the tutorial

archivename=`pwd | awk -F / '{print $NF}'`
echo $archivename
find . -iname "*.layout" -print0 | xargs -0 -I layoutfiles rm -f layoutfiles
# rm -Rf Code/bin
rm -f Code/generated/*
rm -f DAG/*
cd ..
zip -r $archivename-x86 $archivename -x *preparetutozip.sh* *.svn* *Code/lib/pthread* *Code/lib/SDL* *.yuv* *.dat* *Code/bin*