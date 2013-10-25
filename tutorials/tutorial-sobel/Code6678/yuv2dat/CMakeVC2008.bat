@echo off
mkdir bin
cd bin
mkdir VS_main
cd VS_main
mkdir Debug
mkdir Release

cmake ..\..\ -G "Visual Studio 9 2008"

pause