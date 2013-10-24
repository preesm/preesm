#!/bin/sh

# This scripts removes everything that is not wanted in a tutorial zip.
# Copy it in a tutorial root before running it.

archivename=`pwd | awk -F / '{print $NF}'`
echo $archivename
find . -iname "*.layout" -print0 | xargs -0 -I layoutfiles rm -f layoutfiles
rm -Rf Code/bin
rm -f Code/generated/*
rm -f DAG/*
rm -Rf Code/lib/pthread*
rm -Rf Code/lib/SDL*
cd ..
zip -r $archivename $archivename -x *preparetutozip.sh* *.svn*