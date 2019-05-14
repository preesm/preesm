#!/bin/sh

mkdir bin
cd bin
mkdir make
cd make
# Generating the Makefile
# Run ccmake gui to debug cmake problems
cmake ../..
