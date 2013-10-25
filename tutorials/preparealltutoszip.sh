#!/bin/sh

# This script successively invokes zip creation for each tutorial
version=1.0.0
cd introduction
./preparetutozip.sh
cd ..
mv introduction.zip introduction-$version.zip

cd tutorial-sobel
./preparetutozip-2build.sh
./preparetutozip-x86.sh
./preparetutozip-x86-c6678.sh
cd ..
mv tutorial-sobel-2build.zip tutorial-sobel-2build-$version.zip
mv tutorial-sobel-x86.zip tutorial-sobel-x86-$version.zip
mv tutorial-sobel-x86-c6678.zip tutorial-sobel-x86-c6678-$version.zip