@echo off
mkdir bin
cd bin
mkdir CB_main
cd CB_main

cmake ..\..\ -G "CodeBlocks - MinGW Makefiles"
pause