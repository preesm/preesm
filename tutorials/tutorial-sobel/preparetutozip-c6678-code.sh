#!/bin/sh

# This scripts removes everything that is not wanted in a tutorial zip.
# It then zips the tutorial

archivename=`pwd | awk -F / '{print $NF}'`
echo $archivename
find . -iname "*.layout" -print0 | xargs -0 -I layoutfiles rm -f layoutfiles
# rm -Rf Code/bin
rm -f Code/generated/*
rm -f DAG/*

# excluding elements from the zip without deleting them
zip -r $archivename-c6678-code Code6678 -x *.svn*  *.zip* *.yuv* *.dat* *Debug/** *Release/** \
*generated/** *packages* *evmc6678l_xds100v1.ccxml* *makefile.defs* \
*TMS320C6678.ccxml* *.ccsproject* *.cproject* *.project* *.xdchelp* *yuv2dat/bin* \
*Debug* *Release* *.settings* *.config*